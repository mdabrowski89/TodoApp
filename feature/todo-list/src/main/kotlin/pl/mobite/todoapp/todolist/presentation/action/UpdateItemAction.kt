package pl.mobite.todoapp.todolist.presentation.action

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import pl.mobite.lib.mvi.Action
import pl.mobite.todoapp.todolist.data.DummyTodoItemService
import pl.mobite.todoapp.todolist.domain.usecase.GetTodoItemUseCaseImpl
import pl.mobite.todoapp.todolist.domain.usecase.UpdateTodoItemUseCaseImpl
import pl.mobite.todoapp.todolist.presentation.TodoListViewState

data class UpdateItemAction(
    private val itemId: Long,
    private val isDone: Boolean
) : Action<TodoListViewState> {

    private val getTodoItemUseCase = GetTodoItemUseCaseImpl(DummyTodoItemService)
    private val updateTodoItemUseCase = UpdateTodoItemUseCaseImpl(DummyTodoItemService)

    override fun process() = flow {
        emit(TodoListViewState.inProgress())
        delay(500)
        val item = getTodoItemUseCase(itemId)?.copy(isDone = isDone)
        if (item == null) {
            emit(TodoListViewState.error(Exception("Todo item with id $itemId not found")))
        } else {
            updateTodoItemUseCase(item)
            emit(TodoListViewState.itemUpdated(item))
        }
    }.catch {
        emit(TodoListViewState.error(it))
    }

    override fun getId() = super.getId().toString() + itemId
}
