package pl.mobite.todoapp.todolist.domain.usecase

import pl.mobite.lib.utilities.SuspendableUseCase1
import pl.mobite.todoapp.todolist.domain.model.TodoItem
import pl.mobite.todoapp.todolist.domain.service.TodoItemService

interface AddTodoItemUseCase : SuspendableUseCase1<TodoItem, TodoItem>

@Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
class AddTodoItemUseCaseImpl(
    private val todoItemService: TodoItemService
) : AddTodoItemUseCase {

    override suspend fun invoke(todoItem: TodoItem): TodoItem {
        val id = todoItemService.addNew(todoItem)
        return todoItem.copy(id = id)
    }
}
