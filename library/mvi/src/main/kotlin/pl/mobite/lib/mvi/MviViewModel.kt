package pl.mobite.lib.mvi

import android.os.TransactionTooLargeException
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach

/**
 * Base [ViewModel] class which is able to create and process [Action]s and it exposes the flow of [ViewState]s.
 * Actions are created and sends to the processing in [processAction] method. View states are emitted via [viewStateFlow]
 *
 * Internally it uses [ActionsProcessor] as an engine which process [Action]s, collects its [Reducer]s and uses them to create new [ViewState]s.
 *
 * It is also caching the recently emitted [ViewState] and it is later used as  [ViewModel] recreation.
 *
 * @param savedStateHandle - used as a cache to store recently emitted [ViewState]s.
 * @param initialViewState - [ViewState] which is emitted as a first object by the flow of view states (it is ignored on viewModel recreation when
 * there is already a different view state object in the cache - the view state from the is emitted in this case).
 */
abstract class MviViewModel<VS : ViewState, SE : SideEffect>(
    private val savedStateHandle: SavedStateHandle,
    initialViewState: VS
) : ViewModel() {

    private val viewStateKey = "cache.viewState.${this::class.simpleName}"

    private val actionsProcessor: ActionsProcessor<VS> = ActionsProcessor(
        initialViewState = savedStateHandle.get<VS>(viewStateKey) ?: initialViewState,
        coroutineScope = viewModelScope
    )

    val viewStateFlow: StateFlow<VS> = actionsProcessor.viewStateFlow.asStateFlow()

    val currentViewState: VS
        get() = viewStateFlow.value

    private val _sideEffectFlow = MutableSharedFlow<SE>()
    val sideEffectFlow: Flow<SE> = _sideEffectFlow.asSharedFlow()

    init {
        viewStateFlow
            .onEach(::saveViewState)
            .launchIn(viewModelScope)
    }

    override fun onCleared() {
        actionsProcessor.cancelAll()
        super.onCleared()
    }

    /**
     * Creates new [Action] object and sends it to be processed by the [ActionsProcessor].
     * @param actionId - when action processing starts then the previously processed action with the same id is canceled.
     * @param errorHandler - is invoked in case the action processing is interrupted by an unhandled exception. Can also produce reducer, which is then emitted via [Reducer] flow.
     * @param actionBlock - action body is defined as method which is passed to the [Reducer] flow build
     */
    protected fun processAction(
        actionId: String,
        errorHandler: suspend (Throwable, suspend (Reducer<VS>) -> Unit) -> Unit = ::defaultErrorHandler,
        actionBlock: suspend FlowCollector<Reducer<VS>>.() -> Unit
    ) {
        val action = Action(actionId) {
            flow(actionBlock)
                .catch { throwable ->
                    errorHandler(throwable) { reducer -> emit(reducer) }
                }
        }
        actionsProcessor.process(action)
    }

    /**
     * Returns [Reducer] which is emitted after the action processing is interrupted by an unhandled exception.
     * This is used as a default when the [processAction] method does not provide its own error handler.
     */
    protected abstract suspend fun defaultErrorHandler(t: Throwable, reduce: suspend (Reducer<VS>) -> Unit)

    /**
     * Stores the provided [ViewState] object in the [savedStateHandle] in order to be able to restore it on ViewModel recreation.
     */
    private fun saveViewState(viewState: VS) {
        if (isViewStateSavable(viewState)) {
            savedStateHandle[viewStateKey] = foldViewStateOnSaveToCache(viewState)
        }
    }

    /**
     * Returns information whether provided [viewState] can be save to [savedStateHandle].
     * In general the view states which represents any pending operations (like fetching data) should not be saved because the view state
     * from the cache is used only in case when whole process is recreated and then all pending operations are canceled. Restoring state which
     * represents some pending operation can result in displaying wrong UI components - eg. application may look like its loading the data
     * but in fact the load operation is not performed.
     */
    protected abstract fun isViewStateSavable(viewState: VS): Boolean

    /**
     * If the [viewState] contains a lot of data it may exceed IPC transaction limits and the [TransactionTooLargeException] can be thrown during
     * the process (and ViewModel) recreation.
     * https://developer.android.com/reference/android/os/TransactionTooLargeException
     * This method allows to remove some heavy data from the view state before it is saved to [savedStateHandle].
     */
    protected open fun foldViewStateOnSaveToCache(viewState: VS): VS = viewState

    /**
     * Helper function which emitting provided [Reducer] objects. It is added as a part of DLS of the [processAction] method.
     */
    protected suspend fun FlowCollector<Reducer<VS>>.reduce(reducer: Reducer<VS>) {
        emit(reducer)
    }

    protected suspend fun sendSideEffect(sideEffect: SE) {
        _sideEffectFlow.emit(sideEffect)
    }
}
