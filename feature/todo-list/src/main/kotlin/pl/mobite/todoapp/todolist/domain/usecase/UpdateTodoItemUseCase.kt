package pl.mobite.todoapp.todolist.domain.usecase

import pl.mobite.lib.utilities.SuspendableUseCase1n
import pl.mobite.todoapp.todolist.domain.model.TodoItem
import pl.mobite.todoapp.todolist.domain.service.TodoItemService

interface UpdateTodoItemUseCase : SuspendableUseCase1n<TodoItem>

@Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
class UpdateTodoItemUseCaseImpl(
    private val todoItemService: TodoItemService
) : UpdateTodoItemUseCase {

    override suspend fun invoke(todoItem: TodoItem) {
        todoItemService.update(todoItem)
    }
}
