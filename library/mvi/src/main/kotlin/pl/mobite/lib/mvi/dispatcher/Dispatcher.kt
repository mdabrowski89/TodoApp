package pl.mobite.lib.mvi.dispatcher

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface Dispatcher<I, O>  {

    fun dispatch(element: I)

    val output: Flow<O>
}

interface StateDispatcher<I, O>: Dispatcher<I, O> {

    override val output: StateFlow<O>
}
