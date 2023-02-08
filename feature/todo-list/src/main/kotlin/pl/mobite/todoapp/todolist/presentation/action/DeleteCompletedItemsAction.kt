package pl.mobite.todoapp.todolist.presentation.action

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import pl.mobite.lib.mvi.Action
import pl.mobite.todoapp.todolist.data.DummyTodoItemService
import pl.mobite.todoapp.todolist.domain.usecase.DeleteTodoItemUseCaseImpl
import pl.mobite.todoapp.todolist.domain.usecase.GetAllDoneTodoItemsUseCaseImpl
import pl.mobite.todoapp.todolist.presentation.TodoListViewState

class DeleteCompletedItemsAction : Action<TodoListViewState> {

    private val getAllDoneTodoItemsUseCase = GetAllDoneTodoItemsUseCaseImpl(DummyTodoItemService)
    private val deleteTodoItemsUseCase = DeleteTodoItemUseCaseImpl(DummyTodoItemService)

    override fun process() = flow {
        emit(TodoListViewState.inProgress())
        delay(500)
        val doneItems = getAllDoneTodoItemsUseCase()
        deleteTodoItemsUseCase(doneItems)
        emit(TodoListViewState.itemsRemoved(doneItems))
    }.catch {
        emit(TodoListViewState.error(it))
    }
}
