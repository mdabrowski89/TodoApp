package pl.mobite.lib.mvi

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

abstract class MviViewModel<VS : ViewState>(
    savedStateHandle: SavedStateHandle,
    defaultViewState: VS
) : ViewModel() {

    private val viewStateCache = ViewStateCache(
        id = this::class.simpleName ?: "",
        savedStateHandle = savedStateHandle,
        isViewStateSavable = ::isViewStateSavable,
        foldViewStateOnSave = ::foldViewStateOnSave
    )

    private val actionProcessor = ActionProcessor(initialState = viewStateCache.get() ?: defaultViewState)

    val viewStateFlow: StateFlow<VS> = actionProcessor.viewStateFlow

    protected val currentViewState
        get() = viewStateFlow.value

    init {
        viewStateFlow
            .onEach(viewStateCache::set)
            .launchIn(viewModelScope)

        actionProcessor.init(viewModelScope)
    }

    fun processAction(
        actionId: String,
        errorHandler: ((Throwable) -> Reduction<VS>)? = null,
        actionBlock: suspend FlowCollector<Reduction<VS>>.() -> Unit
    ) {
        val action = Action(actionId) {
            flow(actionBlock)
                .catch { t ->
                    reduce(errorHandler?.invoke(t) ?: defaultErrorHandler(t))
                }
        }
        actionProcessor.process(action)
    }

    abstract fun defaultErrorHandler(t: Throwable): Reduction<VS>

    abstract fun isViewStateSavable(viewState: VS): Boolean

    protected open fun foldViewStateOnSave(viewState: VS): VS = viewState

    suspend fun FlowCollector<Reduction<VS>>.reduce(reduction: Reduction<VS>) {
        emit(reduction)
    }
}
