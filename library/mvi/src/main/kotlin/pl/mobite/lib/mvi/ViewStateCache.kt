package pl.mobite.lib.mvi

import androidx.lifecycle.SavedStateHandle

class ViewStateCache<VS : ViewState>(
    id: String,
    private val savedStateHandle: SavedStateHandle,
    private val isViewStateSavable: (VS) -> Boolean,
    private val foldViewStateOnSave: (VS) -> VS
) {
    private val viewStateKey = "cache.states.$id"

    fun get() = savedStateHandle.get<VS>(viewStateKey)

    fun set(viewState: VS) {
        if (isViewStateSavable(viewState)) {
            savedStateHandle[viewStateKey] = foldViewStateOnSave(viewState)
        }
    }
}
