package pl.mobite.todoapp.todolist.presentation.action

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import pl.mobite.lib.mvi.Action
import pl.mobite.todoapp.todolist.data.DummyTodoItemService
import pl.mobite.todoapp.todolist.domain.model.TodoItem
import pl.mobite.todoapp.todolist.domain.usecase.AddTodoItemUseCaseImpl
import pl.mobite.todoapp.todolist.presentation.TodoListViewState
import kotlin.random.Random

data class AddItemAction(
    private val todoItemContent: String
) : Action<TodoListViewState> {

    private val addTodoItemUseCase = AddTodoItemUseCaseImpl(DummyTodoItemService)

    override fun process() = flow {
        emit(TodoListViewState.inProgress())
        delay(500)
        val newItem = addTodoItemUseCase(TodoItem(Random.nextLong(), todoItemContent, false))
        emit(TodoListViewState.itemAdded(newItem))
    }.catch {
        emit(TodoListViewState.error(it))
    }
}
