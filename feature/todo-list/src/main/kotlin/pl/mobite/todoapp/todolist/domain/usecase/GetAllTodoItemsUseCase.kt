package pl.mobite.todoapp.todolist.domain.usecase

import pl.mobite.lib.utilities.SuspendableUseCase0
import pl.mobite.todoapp.todolist.domain.model.TodoItem
import pl.mobite.todoapp.todolist.domain.service.TodoItemService

interface GetAllTodoItemsUseCase : SuspendableUseCase0<List<TodoItem>>

class GetAllTodoItemsUseCaseImpl(
    private val todoItemService: TodoItemService
) : GetAllTodoItemsUseCase {

    override suspend fun invoke(): List<TodoItem> {
        return todoItemService
            .getAll()
    }
}
