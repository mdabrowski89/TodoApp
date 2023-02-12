package pl.mobite.lib.mvi.dispatcher

import kotlinx.coroutines.flow.MutableStateFlow
import pl.mobite.lib.mvi.Reduction
import pl.mobite.lib.mvi.ViewState

interface ReductionDispatcher<VS : ViewState> : StateDispatcher<Reduction<VS>, VS>

class DefaultReductionDispatcher<VS : ViewState>(
    initialState: VS,
) : ReductionDispatcher<VS> {

    override val output = MutableStateFlow(initialState)

    override fun dispatch(element: Reduction<VS>) {
        output.value = element(output.value)
    }
}
