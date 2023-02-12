package pl.mobite.lib.mvi

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import pl.mobite.lib.mvi.dispatcher.ActionDispatcher
import pl.mobite.lib.mvi.dispatcher.DefaultActionDispatcher
import pl.mobite.lib.mvi.dispatcher.DefaultReductionDispatcher
import pl.mobite.lib.mvi.dispatcher.ReductionDispatcher

abstract class MviViewModel<VS : ViewState>(
    savedStateHandle: SavedStateHandle,
    initialViewState: VS
) : ViewModel() {

    private val viewStateCache = ViewStateCache(
        id = this::class.simpleName ?: "",
        savedStateHandle = savedStateHandle,
        isViewStateSavable = ::isViewStateSavable,
        foldViewStateOnSave = ::foldViewStateOnSave
    )

    private val actionDispatcher: ActionDispatcher<VS> = DefaultActionDispatcher()
    private val reductionDispatcher: ReductionDispatcher<VS> = DefaultReductionDispatcher(viewStateCache.get() ?: initialViewState)

    val viewStateFlow: StateFlow<VS> = reductionDispatcher.output

    protected val currentViewState
        get() = viewStateFlow.value

    init {
        viewStateFlow
            .onEach(viewStateCache::set)
            .launchIn(viewModelScope)

        actionDispatcher.output
            .onEach(reductionDispatcher::dispatch)
            .launchIn(viewModelScope)
    }

    abstract fun defaultErrorHandler(t: Throwable): Reduction<VS>

    abstract fun isViewStateSavable(viewState: VS): Boolean

    protected open fun foldViewStateOnSave(viewState: VS): VS = viewState

    fun processAction(
        id: String,
        errorHandler: ((Throwable) -> Reduction<VS>)? = null,
        // TODO: unbound it from flow
        actionBlock: suspend FlowCollector<Reduction<VS>>.() -> Unit
    ) {
        val action = object : Action<VS> {

            override fun invoke(): Flow<Reduction<VS>> {
                return flow(actionBlock)
                    .catch { t ->
                        reduce(errorHandler?.invoke(t) ?: defaultErrorHandler(t))
                    }
            }

            override fun getId(): String = id
        }
        actionDispatcher.dispatch(action)
    }

    suspend fun FlowCollector<Reduction<VS>>.reduce(reduction: Reduction<VS>) {
        emit(reduction)
    }
}
