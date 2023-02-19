package pl.mobite.todoapp.todolist.domain.usecase

import kotlinx.coroutines.delay
import pl.mobite.todoapp.todolist.domain.model.TodoItem
import pl.mobite.todoapp.todolist.domain.service.TodoItemService

class AddTodoItemUseCase(
    private val todoItemService: TodoItemService
) {

   suspend operator fun invoke(todoItem: TodoItem): TodoItem {
        delay(500)
        val id = todoItemService.addNew(todoItem)
        return todoItem.copy(id = id)
    }
}
