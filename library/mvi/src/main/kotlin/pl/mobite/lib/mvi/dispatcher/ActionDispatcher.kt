package pl.mobite.lib.mvi.dispatcher

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import pl.mobite.lib.mvi.Action
import pl.mobite.lib.mvi.Reduction
import pl.mobite.lib.mvi.ViewState

class ActionDispatcher<VS : ViewState> {

    private val actionChannel: Channel<Action<VS>> = Channel(Channel.UNLIMITED)

    val output: Flow<Reduction<VS>> = channelFlow {
        val coroutineRunner = CoroutineRunner()
        actionChannel.receiveAsFlow().collect { action: Action<VS> ->
            // get id in order to identify the coroutine which is processing the action
            val id = action.getId()
            with(coroutineRunner) {
                // cancel current coroutine which is processing the action
                // and suspend until it is canceled
                cancelAndJoin(id)

                // launch new coroutine which will process the action
                // and send view state reduction to the output channel flow
                launchCoroutine(id) { action().collect(::send) }
            }
        }
    }

    fun dispatch(element: Action<VS>) {
        actionChannel.trySend(element)
    }
}

private class CoroutineRunner {

    private val scopes: HashMap<String, CoroutineScope> = hashMapOf()

    fun CoroutineScope.launchCoroutine(
        id: String,
        block: suspend CoroutineScope.() -> Unit,
    ) : Job {
        val scope = scopes.getOrPut(id) { this + Job() }
        return scope.launch(Dispatchers.Default, block = block)
    }

    suspend fun cancelAndJoin(id: String) {
        val job = getJob(id) ?: return
        val children = job.children.toList()
        job.cancelChildren()
        children.joinAll()
    }

    private fun getJob(id: String) = scopes[id]?.coroutineContext?.get(Job)
}
