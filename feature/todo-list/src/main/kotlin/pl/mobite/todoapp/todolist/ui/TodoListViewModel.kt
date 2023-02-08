package pl.mobite.todoapp.todolist.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import pl.mobite.todoapp.todolist.presentation.TodoListStore
import pl.mobite.todoapp.todolist.presentation.TodoListViewState
import pl.mobite.todoapp.todolist.presentation.TodoListViewStateCache

class TodoListViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val viewStateCache: TodoListViewStateCache = TodoListViewStateCache(savedStateHandle)
    private val initialViewState: TodoListViewState = TodoListViewState()

    val todolistStore: TodoListStore = TodoListStore(viewStateCache.get() ?: initialViewState, viewModelScope)
}
