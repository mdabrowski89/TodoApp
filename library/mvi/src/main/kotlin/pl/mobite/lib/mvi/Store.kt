package pl.mobite.lib.mvi

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import pl.mobite.lib.mvi.dispatcher.ActionDispatcher
import pl.mobite.lib.mvi.dispatcher.DefaultActionDispatcher
import pl.mobite.lib.mvi.dispatcher.MutationDispatcher
import pl.mobite.lib.mvi.dispatcher.DefaultMutationDispatcher

abstract class Store<VS : ViewState>(
    initialViewState: VS,
    coroutineScope: CoroutineScope,
    private val actionDispatcher: ActionDispatcher<VS> = DefaultActionDispatcher(),
    private val mutationDispatcher: MutationDispatcher<VS> = DefaultMutationDispatcher(initialViewState)
) {

    val viewStateFlow: StateFlow<VS> = mutationDispatcher.output

    protected val currentViewState
        get() = viewStateFlow.value

    init {
        viewStateFlow
            .launchIn(coroutineScope)

        actionDispatcher.output
            .onEach(mutationDispatcher::dispatch)
            .launchIn(coroutineScope)
    }

    fun accept(action: Action<VS>) {
        actionDispatcher.dispatch(action)
    }
}
