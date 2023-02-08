package pl.mobite.todoapp.todolist.presentation

import kotlinx.coroutines.CoroutineScope
import pl.mobite.lib.mvi.Store
import pl.mobite.todoapp.todolist.presentation.action.AddItemAction
import pl.mobite.todoapp.todolist.presentation.action.DeleteCompletedItemsAction
import pl.mobite.todoapp.todolist.presentation.action.LoadItemsAction
import pl.mobite.todoapp.todolist.presentation.action.UpdateItemAction

class TodoListStore(
    initialViewState: TodoListViewState,
    coroutineScope: CoroutineScope,
) : Store<TodoListViewState>(
    initialViewState = initialViewState,
    coroutineScope = coroutineScope
) {

    fun loadItems() {
        if (currentViewState.todoItems == null) {
            accept(LoadItemsAction())
        }
    }

    fun addItem(todoItemContent: String) {
        if (todoItemContent.isNotBlank()) {
            accept(AddItemAction(todoItemContent))
        }
    }

    fun deleteCompletedItems() = accept(DeleteCompletedItemsAction())

    fun updateItem(itemId: Long, isDone: Boolean) = accept(UpdateItemAction(itemId, isDone))
}
