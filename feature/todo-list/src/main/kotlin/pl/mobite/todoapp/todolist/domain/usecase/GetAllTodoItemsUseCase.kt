package pl.mobite.todoapp.todolist.domain.usecase

import kotlinx.coroutines.delay
import pl.mobite.todoapp.todolist.domain.model.TodoItem
import pl.mobite.todoapp.todolist.domain.service.TodoItemService

class GetAllTodoItemsUseCase(
    private val todoItemService: TodoItemService
) {

    suspend operator fun invoke(): List<TodoItem> {
        delay(500)
        return todoItemService
            .getAll()
    }
}
