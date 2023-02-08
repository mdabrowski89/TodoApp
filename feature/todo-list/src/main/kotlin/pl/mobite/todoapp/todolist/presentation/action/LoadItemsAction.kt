package pl.mobite.todoapp.todolist.presentation.action

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import pl.mobite.lib.mvi.Action
import pl.mobite.todoapp.todolist.data.DummyTodoItemService
import pl.mobite.todoapp.todolist.domain.usecase.GetAllTodoItemsUseCaseImpl
import pl.mobite.todoapp.todolist.presentation.TodoListViewState

class LoadItemsAction : Action<TodoListViewState> {

    private val getAllTodoItemUseCase = GetAllTodoItemsUseCaseImpl(DummyTodoItemService)

    override fun process() = flow {
        emit(TodoListViewState.inProgress())
        delay(500)
        val todoItems = getAllTodoItemUseCase()
        emit(TodoListViewState.itemsUpdated(todoItems))
    }.catch {
        emit(TodoListViewState.error(it))
    }
}
