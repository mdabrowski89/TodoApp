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
import pl.mobite.todoapp.todolist.ui.TodoListSideEffect.ErrorSideEffect
import pl.mobite.todoapp.todolist.ui.TodoListSideEffect.ItemUpdatedSideEffect
import kotlin.random.Random

class TodoListViewModel(
    savedStateHandle: SavedStateHandle,
) : MviViewModel<TodoListViewState, TodoListSideEffect>(
    savedStateHandle = savedStateHandle,
    initialViewState = TodoListViewState()
) {

    private val getAllTodoItemUseCase = GetAllTodoItemsUseCase(DummyTodoItemService)
    private val addTodoItemUseCase = AddTodoItemUseCase(DummyTodoItemService)
    private val deleteAllDoneTodoItemsUseCase = DeleteAllDoneTodoItemsUseCase(DummyTodoItemService)
    private val updateTodoItemUseCase = UpdateTodoItemUseCase(DummyTodoItemService)

    override suspend fun defaultErrorHandler(t: Throwable, reduce: suspend (Reducer<TodoListViewState>) -> Unit) {
        sendSideEffect(ErrorSideEffect)
        reduce { errorResult() }
    }

    override fun isViewStateSavable(viewState: TodoListViewState) = !viewState.inProgress

    fun loadItems() {
        if (currentViewState.todoItems != null) {
            return
        }
        processAction("loadItems") {
            reduce { inProgressResult() }
            val todoItems = getAllTodoItemUseCase()
            reduce { itemsResult(todoItems) }
        }
    }

    fun addItem(todoItemContent: String) {
        if (todoItemContent.isBlank()) {
            return
        }
        processAction("addItem") {
            reduce { inProgressResult() }
            val newItem = addTodoItemUseCase(TodoItem(Random.nextLong(), todoItemContent, false))
            reduce {
                val newItems = todoItems?.plus(newItem)
                itemsResult(newItems)
            }
        }
    }

    fun deleteCompletedItems() {
        processAction("deleteCompletedItems") {
            reduce { inProgressResult() }
            val deletedItems = deleteAllDoneTodoItemsUseCase()
            reduce {
                val newItems = todoItems?.toMutableList()?.apply { removeAll(deletedItems) }
                itemsResult(newItems)
            }
        }
    }

    fun updateItem(item: TodoItem, isDone: Boolean) {
        processAction("updateItem-${item.id}") {
            reduce { inProgressResult() }
            val updatedItem = item.copy(isDone = isDone)
            updateTodoItemUseCase(updatedItem)
            sendSideEffect(ItemUpdatedSideEffect)
            reduce {
                val newItems = todoItems?.map { item -> if (item.id == updatedItem.id) updatedItem else item }
                itemsResult(newItems)
            }
        }
    }
}

private fun TodoListViewState.inProgressResult() = copy(
    inProgress = true
)

private fun TodoListViewState.errorResult() = copy(
    inProgress = false
)

private fun TodoListViewState.itemsResult(todoItems: List<TodoItem>?) = copy(
    inProgress = false,
    todoItems = todoItems
)
