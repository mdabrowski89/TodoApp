package pl.mobite.todoapp.todolist.domain.usecase

import pl.mobite.lib.utilities.SuspendableUseCase1n
import pl.mobite.todoapp.todolist.domain.model.TodoItem
import pl.mobite.todoapp.todolist.domain.service.TodoItemService

interface DeleteTodoItemUseCase : SuspendableUseCase1n<List<TodoItem>>

@Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
class DeleteTodoItemUseCaseImpl(
    private val todoItemService: TodoItemService
) : DeleteTodoItemUseCase {

    override suspend fun invoke(todoItems: List<TodoItem>) {
        todoItemService.delete(todoItems)
    }
}
