package pl.mobite.todoapp.todolist.domain.usecase

import pl.mobite.lib.utilities.SuspendableUseCase0
import pl.mobite.todoapp.todolist.domain.model.TodoItem
import pl.mobite.todoapp.todolist.domain.service.TodoItemService

interface GetAllDoneTodoItemsUseCase : SuspendableUseCase0<List<TodoItem>>

class GetAllDoneTodoItemsUseCaseImpl(
    private val todoItemService: TodoItemService
) : GetAllDoneTodoItemsUseCase {

    override suspend fun invoke(): List<TodoItem> {
        return todoItemService
            .getAllDone()
    }
}
