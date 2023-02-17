package pl.mobite.lib.mvi

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus

internal class ActionProcessor<VS: ViewState>(
    initialState: VS
) {

    private val dispatcher = Dispatcher<VS>()

    internal val viewStateFlow = MutableStateFlow(initialState)

    internal fun init(coroutineScope: CoroutineScope) {
        dispatcher.reductionFlow
            .onEach(::reduce)
            .launchIn(coroutineScope)
    }

    internal fun process(action: Action<VS>) {
        dispatcher.actionChannel.trySend(action)
    }

    private inline fun reduce(reduction: Reduction<VS>) {
        viewStateFlow.value = reduction(viewStateFlow.value)
    }
}

private class Dispatcher<VS : ViewState> {

    val actionChannel: Channel<Action<VS>> = Channel(Channel.UNLIMITED)

    val reductionFlow: Flow<Reduction<VS>> = channelFlow {

        actionChannel.receiveAsFlow().collect { action: Action<VS> ->
            replaceAction(action.id, action, ::send)
        }
    }

    val coroutineCache = CoroutineCache()

    private suspend fun CoroutineScope.replaceAction(id: String, action: Action<VS>, collector: FlowCollector<Reduction<VS>>) {
        // cancel current coroutine which is processing the action and suspend until it is canceled
        coroutineCache.cancelAndJoin(id)

        // launch new coroutine which will process the action and send view state reduction to the output channel flow
        coroutineCache.launchCoroutine(this, id) {
            action.process().collect(collector)
        }
    }
}

private class CoroutineCache {

    private val scopes: HashMap<String, CoroutineScope> = hashMapOf()

    suspend fun cancelAndJoin(id: String) {
        val job = scopes[id]?.coroutineContext?.get(Job) ?: return
        val children = job.children.toList()
        job.cancelChildren()
        children.joinAll()
    }

    fun launchCoroutine(parentScope: CoroutineScope, id: String, block: suspend CoroutineScope.() -> Unit, ) : Job {
        val scope = scopes.getOrPut(id) { parentScope + Job() }
        return scope.launch(Dispatchers.Default, block = block)
    }
}


