package pl.mobite.lib.mvi.dispatcher

import kotlinx.coroutines.flow.MutableStateFlow
import pl.mobite.lib.mvi.Reduction
import pl.mobite.lib.mvi.ViewState

class ReductionDispatcher<VS : ViewState>(
    initialState: VS,
) {

    val output = MutableStateFlow(initialState)

    fun dispatch(reduction: Reduction<VS>) {
        output.value = reduction(output.value)
    }
}
