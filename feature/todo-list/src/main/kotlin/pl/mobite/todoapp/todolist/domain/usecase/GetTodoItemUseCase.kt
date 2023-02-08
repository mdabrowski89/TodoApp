package pl.mobite.todoapp.todolist.domain.usecase

import pl.mobite.lib.utilities.SuspendableUseCase1
import pl.mobite.todoapp.todolist.domain.model.TodoItem
import pl.mobite.todoapp.todolist.domain.service.TodoItemService

interface GetTodoItemUseCase : SuspendableUseCase1<Long, TodoItem?>

@Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
class GetTodoItemUseCaseImpl(
    private val todoItemService: TodoItemService
) : GetTodoItemUseCase {

    override suspend fun invoke(itemId: Long): TodoItem? {
        return todoItemService
            .getForId(itemId)
            .firstOrNull()
    }
}
