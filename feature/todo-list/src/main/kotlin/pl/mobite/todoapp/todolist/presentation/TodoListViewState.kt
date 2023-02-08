package pl.mobite.todoapp.todolist.presentation

import kotlinx.parcelize.Parcelize
import pl.mobite.lib.mvi.ViewState
import pl.mobite.lib.mvi.ViewStateMutator
import pl.mobite.todoapp.todolist.domain.model.TodoItem

@Parcelize
data class TodoListViewState(
    val inProgress: Boolean = false,
    val todoItems: List<TodoItem>? = null,
) : ViewState {

    companion object : ViewStateMutator<TodoListViewState>() {

        fun inProgress() = mutate {
            it.copy(
                inProgress = true
            )
        }

        fun itemAdded(todoItem: TodoItem) = mutate {
            it.copy(
                inProgress = false,
                todoItems = it.todoItems?.toMutableList()?.apply { add(todoItem) }?.toList() ?: listOf(todoItem),
            )
        }

        fun error(t: Throwable) = mutate {
            it.copy(
                inProgress = false,
            )
        }

        fun itemsUpdated(todoItems: List<TodoItem>) = mutate {
            it.copy(
                inProgress = false,
                todoItems = todoItems
            )
        }

        fun itemUpdated(todoItem: TodoItem) = mutate {
            it.copy(
                inProgress = false,
                todoItems = it.todoItems?.map { currentItem ->
                    if (currentItem.id == todoItem.id) todoItem else currentItem
                }
            )
        }

        fun itemsRemoved(todoItems: List<TodoItem>) = mutate {
            it.copy(
                inProgress = false,
                todoItems = with(todoItems.map { currentItem -> currentItem.id }) {
                    it.todoItems?.filterNot { it.id in this }
                }
            )
        }
    }
}
