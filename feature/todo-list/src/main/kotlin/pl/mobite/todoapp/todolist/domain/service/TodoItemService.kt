package pl.mobite.todoapp.todolist.domain.service

import pl.mobite.todoapp.todolist.domain.model.TodoItem

interface TodoItemService {

    suspend fun getAll(): List<TodoItem>

    suspend fun getAllDone(): List<TodoItem>

    suspend fun addNew(todoItem: TodoItem): Long

    suspend fun update(todoItem: TodoItem)

    suspend fun delete(todoItems: List<TodoItem>)
}
