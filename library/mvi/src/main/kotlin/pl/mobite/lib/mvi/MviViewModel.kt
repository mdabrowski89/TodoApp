package pl.mobite.lib.mvi

import android.os.TransactionTooLargeException
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
        foldViewStateOnSave = ::foldViewStateOnSaveToCache
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

    /**
     * Executes provided [actionBlock] in new coroutine on the [viewModelScope]
     * @param actionBlock - method which creates flow of [Reduction]
     */
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

    /**
     * [Reduction] which is emitted on the action processing exception if the [processAction] itself does not define an error handler
     */
    abstract fun defaultErrorHandler(t: Throwable): Reduction<VS>

    /**
     * Returns information whether provided [viewState] can be save to [ViewStateCache].
     * In general the view states which represents any pending operations (like fetching data) should not be saved because the view state
     * from the cache is used only in case when whole process is recreated and then all pending operations are canceled. Restoring state which
     * represents some pending operation can result in displaying wrong UI components - eg. application may look like its loading the data
     * but in fact the load operation is not performed.
     */
    abstract fun isViewStateSavable(viewState: VS): Boolean

    /**
     * If the [viewState] contains a lot of data it may exceed IPC transaction limits and the [TransactionTooLargeException] can be thrown during
     * the process recreation.
     * https://developer.android.com/reference/android/os/TransactionTooLargeException
     * This method allows to remove some heavy data from the view state before it is saved to [ViewStateCache].
     */
    protected open fun foldViewStateOnSaveToCache(viewState: VS): VS = viewState

    /**
     * Helper function which emitting provided [Reduction] objects. It is added as a part of DLS of the [processAction] method.
     */
    suspend fun FlowCollector<Reduction<VS>>.reduce(reduction: Reduction<VS>) {
        emit(reduction)
    }
}
