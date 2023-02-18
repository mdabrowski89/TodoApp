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
 * Component responsible for processing [Action] objects, as a result of this processing it emits flow of [ViewState] objects.
 */
internal class ActionProcessor<VS : ViewState>(
    initialState: VS,
    coroutineScope: CoroutineScope
) {

    /**
     * Output flow which emits [ViewState] objects
     */
    val viewStateFlow = MutableStateFlow(initialState)

    private val actionChannel: Channel<Action<VS>> = Channel(Channel.UNLIMITED)

    init {
        collectAndProcessActions()
            .onEach(::reduce)
            .launchIn(coroutineScope)
    }

    /**
     * Send action for the processing
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

        // for every action.id it contains a scope in which this action will be processed
        val actionScopes: HashMap<String, CoroutineScope> = hashMapOf()

        // collect every action from actions channel
        actionChannel.receiveAsFlow().collect { action: Action<VS> ->
            
            // for each action: get the [CoroutineScope] of this action based on its id
            val actionScope = actionScopes.getOrPut(action.id) {
                // if not present then create new from the [Reduction] channel context and new Job object
                CoroutineScope(this.coroutineContext + Job())
            }

            // cancel any actions which are currently processed in the action [CoroutineScope]
            actionScope.coroutineContext[Job]?.cancelChildrenAndJoin()

            // within the action [CoroutineScope] create new coroutine which will start the action processing
            actionScope.launch(Dispatchers.Default) {
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
