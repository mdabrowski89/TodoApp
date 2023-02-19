package pl.mobite.todoapp.todolist.domain.usecase

import kotlinx.coroutines.delay
import pl.mobite.todoapp.todolist.domain.model.TodoItem
import pl.mobite.todoapp.todolist.domain.service.TodoItemService

class UpdateTodoItemUseCase(
    private val todoItemService: TodoItemService
) {

    suspend operator fun invoke(todoItem: TodoItem) {
        delay(500)
        todoItemService.update(todoItem)
    }
}
