package pl.mobite.todoapp.todolist.domain.usecase

import kotlinx.coroutines.delay
import pl.mobite.todoapp.todolist.domain.model.TodoItem
import pl.mobite.todoapp.todolist.domain.service.TodoItemService

class DeleteAllDoneTodoItemsUseCase(
    private val todoItemService: TodoItemService
) {

    suspend operator fun invoke(): List<TodoItem> {
        delay(500)
        val doneItems = todoItemService.getAllDone()
        todoItemService.delete(doneItems)
        return doneItems
    }
}
