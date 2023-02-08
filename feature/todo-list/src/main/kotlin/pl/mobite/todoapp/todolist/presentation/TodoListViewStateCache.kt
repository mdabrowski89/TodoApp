package pl.mobite.todoapp.todolist.presentation

import androidx.lifecycle.SavedStateHandle
import pl.mobite.lib.mvi.ViewStateCache

class TodoListViewStateCache(
    savedStateHandle: SavedStateHandle
) : ViewStateCache<TodoListViewState>(
    savedStateHandle = savedStateHandle
) {

    override fun isSavable(viewState: TodoListViewState) = !viewState.inProgress
}
