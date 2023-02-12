package pl.mobite.todoapp.todolist.ui

import androidx.lifecycle.SavedStateHandle
import pl.mobite.lib.mvi.MviViewModel
import pl.mobite.lib.mvi.Reduction
import pl.mobite.todoapp.todolist.data.DummyTodoItemService
import pl.mobite.todoapp.todolist.domain.model.TodoItem
import pl.mobite.todoapp.todolist.domain.usecase.AddTodoItemUseCase
import pl.mobite.todoapp.todolist.domain.usecase.DeleteAllDoneTodoItemsUseCase
import pl.mobite.todoapp.todolist.domain.usecase.GetAllTodoItemsUseCase
import pl.mobite.todoapp.todolist.domain.usecase.UpdateTodoItemUseCase
import kotlin.random.Random

class TodoListViewModel(
    savedStateHandle: SavedStateHandle,
) : MviViewModel<TodoListViewState>(
    savedStateHandle = savedStateHandle,
    initialViewState = TodoListViewState()
) {

    private val getAllTodoItemUseCase = GetAllTodoItemsUseCase(DummyTodoItemService)
    private val addTodoItemUseCase = AddTodoItemUseCase(DummyTodoItemService)
    private val deleteAllDoneTodoItemsUseCase = DeleteAllDoneTodoItemsUseCase(DummyTodoItemService)
    private val updateTodoItemUseCase = UpdateTodoItemUseCase(DummyTodoItemService)

    override fun defaultErrorHandler(t: Throwable): Reduction<TodoListViewState> = { it.withoutProgress() }

    override fun isViewStateSavable(viewState: TodoListViewState) = !viewState.progressVisible

    fun loadItems() {
        if (currentViewState.todoItems != null) {
            return
        }
        processAction("loadItems") {
            reduce { it.withProgress() }
            val todoItems = getAllTodoItemUseCase()
            reduce { it.withoutProgress().withItems(todoItems) }
        }
    }

    fun addItem(todoItemContent: String) {
        if (todoItemContent.isBlank()) {
            return
        }
        processAction("addItem") {
            reduce { it.withProgress() }
            val newItem = addTodoItemUseCase(TodoItem(Random.nextLong(), todoItemContent, false))
            reduce {
                it.withoutProgress()
                    .withItems(it.todoItems?.plus(newItem))
            }
        }
    }

    fun deleteCompletedItems() = processAction("deleteCompletedItems") {
        reduce { it.withProgress() }
        val deletedItems = deleteAllDoneTodoItemsUseCase()
        reduce {
            it.withoutProgress()
                .withItems(it.todoItems?.toMutableList()?.apply { removeAll(deletedItems) })
        }
    }

    fun updateItem(item: TodoItem, isDone: Boolean) = processAction("updateItem-${item.id}") {
        reduce { it.withProgress() }
        val updatedItem = item.copy(isDone = isDone)
        updateTodoItemUseCase(updatedItem)
        reduce {
            val newItems = it.todoItems?.map { item -> if (item.id == updatedItem.id) updatedItem else item }
            it.withoutProgress()
                .withItems(newItems)
        }
    }

    private fun TodoListViewState.withProgress() = this.copy(progressVisible = true)
    private fun TodoListViewState.withoutProgress() = this.copy(progressVisible = false)
    private fun TodoListViewState.withItems(todoItems: List<TodoItem>?) = this.copy(todoItems = todoItems)
}
