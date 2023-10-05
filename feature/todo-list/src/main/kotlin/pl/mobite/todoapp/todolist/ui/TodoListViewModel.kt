package pl.mobite.todoapp.todolist.ui

import androidx.lifecycle.SavedStateHandle
import pl.mobite.lib.mvi.MviViewModel
import pl.mobite.lib.mvi.Reducer
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

    override suspend fun defaultErrorHandler(t: Throwable): Reducer<TodoListViewState> {
        sendSideEffect(ErrorSideEffect)
        return { withError(t) }
    }

    override fun isViewStateSavable(viewState: TodoListViewState) = !viewState.inProgress

    fun loadItems() {
        if (viewState.todoItems != null) {
            return
        }
        processAction("loadItems") {
            reduce { withProgress() }
            val todoItems = getAllTodoItemUseCase()
            reduce { withItems(todoItems) }
        }
    }

    fun addItem(todoItemContent: String) {
        if (todoItemContent.isBlank()) {
            return
        }
        processAction("addItem") {
            reduce { withProgress() }
            val newItem = addTodoItemUseCase(TodoItem(Random.nextLong(), todoItemContent, false))
            reduce {
                val newItems = todoItems?.plus(newItem)
                withItems(newItems)
            }
        }
    }

    fun deleteCompletedItems() = processAction("deleteCompletedItems") {
        reduce { withProgress() }
        val deletedItems = deleteAllDoneTodoItemsUseCase()
        reduce {
            val newItems = todoItems?.toMutableList()?.apply { removeAll(deletedItems) }
            withItems(newItems)
        }
    }

    fun updateItem(item: TodoItem, isDone: Boolean) = processAction("updateItem-${item.id}") {
        reduce { withProgress() }
        val updatedItem = item.copy(isDone = isDone)
        updateTodoItemUseCase(updatedItem)
        sendSideEffect(ItemUpdatedSideEffect)
        reduce {
            val newItems = todoItems?.map { item -> if (item.id == updatedItem.id) updatedItem else item }
            withItems(newItems)
        }
    }

    private fun TodoListViewState.withProgress() = this.copy(inProgress = true)
    private fun TodoListViewState.withError(t: Throwable) = this.copy(inProgress = false)
    private fun TodoListViewState.withItems(todoItems: List<TodoItem>?) = this.copy(inProgress = false, todoItems = todoItems)
}
