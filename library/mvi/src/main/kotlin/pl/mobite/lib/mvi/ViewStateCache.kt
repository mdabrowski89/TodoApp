package pl.mobite.lib.mvi

import androidx.lifecycle.SavedStateHandle

abstract class ViewStateCache<VS : ViewState>(
    private val savedStateHandle: SavedStateHandle,
    id: String
) {
    private val viewStateKey = "cache.states.$id"

    fun get() = savedStateHandle.get<VS>(viewStateKey)

    fun set(viewState: VS) {
        if (isSavable(viewState)) {
            savedStateHandle[viewStateKey] = fold(viewState)
        }
    }

    protected abstract fun isSavable(viewState: VS): Boolean

    protected open fun fold(viewState: VS): VS = viewState
}

inline fun <reified VS : ViewState> createViewStateCache(
    savedStateHandle: SavedStateHandle,
    id: String = VS::class.simpleName ?: "",
    crossinline isSavable: (VS) -> Boolean = { true },
    crossinline fold: (VS) -> VS = { it }
) = object : ViewStateCache<VS>(savedStateHandle, id) {

    override fun isSavable(viewState: VS) = isSavable(viewState)

    override fun fold(viewState: VS): VS = fold(viewState)
}
