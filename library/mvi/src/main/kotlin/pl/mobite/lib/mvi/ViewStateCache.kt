package pl.mobite.lib.mvi

import androidx.lifecycle.SavedStateHandle

abstract class ViewStateCache<VS : ViewState>(
    private val savedStateHandle: SavedStateHandle
) {
    private val viewStateKey = "cache.states.${this::class}"

    fun get() = savedStateHandle.get<VS>(viewStateKey)

    fun set(viewState: VS) {
        if (isSavable(viewState)) {
            savedStateHandle.set(viewStateKey, fold(viewState))
        }
    }

    protected abstract fun isSavable(viewState: VS): Boolean

    protected open fun fold(viewState: VS): VS = viewState
}
