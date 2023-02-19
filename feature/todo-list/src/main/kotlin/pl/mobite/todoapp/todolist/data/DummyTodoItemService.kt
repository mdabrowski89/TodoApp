package pl.mobite.todoapp.todolist.data

import pl.mobite.todoapp.todolist.domain.model.TodoItem
import pl.mobite.todoapp.todolist.domain.service.TodoItemService
import kotlin.random.Random

object DummyTodoItemService: TodoItemService {

    private val todoItems: MutableMap<Long, TodoItem> = mutableMapOf()

    override suspend fun getAll(): List<TodoItem> = todoItems.values.toList()

    override suspend fun getAllDone(): List<TodoItem> = todoItems.values.filter { it.isDone }.toList()

    override suspend fun addNew(todoItem: TodoItem): Long {
        val id = Random.nextLong()
        todoItems[id] = todoItem.copy(id = id)
        return id
    }

    override suspend fun update(todoItem: TodoItem) {
        todoItems[todoItem.id] = todoItem
    }

    override suspend fun delete(todoItems: List<TodoItem>) {
        todoItems.forEach {
            this.todoItems.remove(it.id)
        }
    }
}
