package pl.mobite.lib.mvi.dispatcher

import kotlinx.coroutines.flow.MutableStateFlow
import pl.mobite.lib.mvi.ViewState
import pl.mobite.lib.mvi.ViewStateMutation

interface MutationDispatcher<VS : ViewState> : StateDispatcher<ViewStateMutation<VS>, VS>

class DefaultMutationDispatcher<VS : ViewState>(
    initialState: VS,
) : MutationDispatcher<VS> {

    override val output = MutableStateFlow(initialState)

    override fun dispatch(element: ViewStateMutation<VS>) {
        output.value = element(output.value)
    }
}
