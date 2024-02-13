package pl.mobite.todoapp.todolist.ui

import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import pl.mobite.lib.mvi.SideEffect
import pl.mobite.lib.mvi.ViewState
import pl.mobite.todoapp.todolist.domain.model.TodoItem

@Parcelize
data class TodoListViewState(
    val inProgress: Boolean = false,
    val todoItems: List<TodoItem>? = null,
) : ViewState {

    @IgnoredOnParcel
    val deleteButtonEnabled
        get() = todoItems?.find { it.isDone } != null

    @IgnoredOnParcel
    val addingItemsEnabled
        get() = !inProgress

    @IgnoredOnParcel
    val progressVisible
        get() = inProgress
}

sealed class TodoListSideEffect : SideEffect {

    data object ErrorSideEffect : TodoListSideEffect()

    data object ItemUpdatedSideEffect : TodoListSideEffect()
}
