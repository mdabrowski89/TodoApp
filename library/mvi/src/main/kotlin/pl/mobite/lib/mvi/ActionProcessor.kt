package pl.mobite.lib.mvi

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch

/**
 * Engine which takes and process [Action]s, collects its [Reduction]s and emits new [ViewState]s by reducing the collected reductions with
 * current viewState value. Actions are taken by the [process] method and view states are emitted via [viewStateFlow].
 *
 * @param initialViewState - [ViewState] which is emitted as a first object by the flow of view states
 * @param coroutineScope - coroutine scope on which the flow of [Reduction] is collected. It is also used as a parent scope for the individual scopes
 * in which actions are executed.
 */
internal class ActionProcessor<VS : ViewState>(
    initialViewState: VS,
    coroutineScope: CoroutineScope
) {

    /**
     * Output flow which emits [ViewState] objects
     */
    val viewStateFlow = MutableStateFlow(initialViewState)

    /**
     * Channel to which the [Action]s are send
     */
    private val actionChannel: Channel<Action<VS>> = Channel(Channel.UNLIMITED)

    init {
        collectAndProcessActions()
            .onEach(::reduce)
            .launchIn(coroutineScope)
    }

    /**
     * Send action for the processing.
     * Each action `process()` methods is executed in a new coroutine which runs on a [Dispatchers.Default] and in scope dedicated to the [Action.id].
     */
    fun process(action: Action<VS>) {
        actionChannel.trySend(action)
    }

    /**
     * Starts new channel for [Reduction] objects and returns it as flow.
     * Channel is created in a way that it collects all actions from the [actionChannel],
     * process them, and sends the resulted [Reduction] objects to itself.
     */
    private fun collectAndProcessActions(): Flow<Reduction<VS>> = channelFlow {

        // For each [action.id] it keeps the dedicated coroutine scope in which this action is executed.
        // This allows for easy cancellation of currently processed action when new action (with the same id) is send to the processing.
        val coroutineScopePerActionId: HashMap<String, CoroutineScope> = hashMapOf()

        // collect actions from actionsChannel
        actionChannel.receiveAsFlow().collect { action: Action<VS> ->

            // for each action: get the [CoroutineScope] of this action based on its id
            val actionCoroutineScope = coroutineScopePerActionId.getOrPut(action.id) {
                // if not present then create new one from the [Reduction] channel context and new Job object
                CoroutineScope(this.coroutineContext + Job())
            }

            // cancel any actions which are currently processed in the coroutine scope which is dedicated to this action.id
            actionCoroutineScope.coroutineContext[Job]?.cancelChildrenAndJoin()

            // within the coroutine scope dedicated to the action create new coroutine which will start the action processing
            actionCoroutineScope.launch(Dispatchers.Default) {
                // results of the processing are sent to the [Reduction] channel
                action.process().collect(::send)
            }
        }
    }

    /**
     * Emits new [ViewState] on the [viewStateFlow] by applying [Reduction] to the current [ViewState]
     */
    private fun reduce(reduction: Reduction<VS>) {
        viewStateFlow.value = reduction(viewStateFlow.value)
    }

    /**
     * Helper function which cancel all children of the [Job] and suspend until all of them are stopped.
     */
    private suspend fun Job.cancelChildrenAndJoin() {
        val childrenJobs = children.toList()
        cancelChildren()
        childrenJobs.joinAll()
    }
}
