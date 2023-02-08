package pl.mobite.lib.mvi.dispatcher

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.receiveAsFlow
import pl.mobite.lib.mvi.Action
import pl.mobite.lib.mvi.ViewState
import pl.mobite.lib.mvi.ViewStateMutation

interface ActionDispatcher<VS : ViewState> : Dispatcher<Action<VS>, ViewStateMutation<VS>>

class DefaultActionDispatcher<VS : ViewState> : ActionDispatcher<VS> {

    private val actionChannel: Channel<Action<VS>> = Channel(Channel.UNLIMITED)

    override val output: Flow<ViewStateMutation<VS>> = channelFlow {
        val coroutineRunner = CoroutineRunner()
        actionChannel.receiveAsFlow().collect { action: Action<VS> ->
            // get id in order to identify the coroutine which is processing the action
            val id = action.getId()
            with(coroutineRunner) {
                // cancel current coroutine which is processing the action
                // and suspend until it is canceled
                cancelAndJoin(id)

                // launch new coroutine which will process the action
                // and send view state mutation to the output channel flow
                launchCoroutine(id) { action.process().collect(::send) }
            }
        }
    }

    override fun dispatch(element: Action<VS>) {
        actionChannel.trySend(element)
    }
}
