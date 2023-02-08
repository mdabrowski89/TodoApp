package pl.mobite.lib.mvi

import kotlinx.coroutines.flow.Flow

interface Action<VS : ViewState> {

    fun process(): Flow<ViewStateMutation<VS>>

    fun getId(): Any = this::class
}
