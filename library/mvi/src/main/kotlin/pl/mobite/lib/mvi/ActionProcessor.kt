package pl.mobite.lib.mvi

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import pl.mobite.lib.mvi.dispatcher.ActionDispatcher
import pl.mobite.lib.mvi.dispatcher.ReductionDispatcher

class ActionProcessor<VS: ViewState>(
    initialState: VS
) {

    private val actionDispatcher = ActionDispatcher<VS>()
    private val reductionDispatcher = ReductionDispatcher(initialState)

    val viewStateFlow: StateFlow<VS> = reductionDispatcher.output

    fun init(coroutineScope: CoroutineScope) {
        actionDispatcher.output
            .onEach(reductionDispatcher::dispatch)
            .launchIn(coroutineScope)
    }

    fun process(action: Action<VS>) {
        actionDispatcher.dispatch(action)
    }
}
