package pl.mobite.todoapp.todolist.ui

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer
import pl.mobite.lib.utilities.diffUtilItemCallback
import pl.mobite.lib.utilities.inflateView
import pl.mobite.todoapp.todolist.R.layout
import pl.mobite.todoapp.todolist.databinding.ViewTodoItemBinding
import pl.mobite.todoapp.todolist.domain.model.TodoItem
import pl.mobite.todoapp.todolist.ui.TodoListAdapter.TodoItemViewHolder

class TodoListAdapter(
    private val onItemStateChanged: ((Long, Boolean) -> Unit)
) : ListAdapter<TodoItem, TodoItemViewHolder>(diffUtilItemCallback { this.id }) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoItemViewHolder {
        return TodoItemViewHolder(parent)
    }

    override fun onBindViewHolder(holder: TodoItemViewHolder, position: Int) {
        holder.bind(getItem(position), onItemStateChanged)
    }

    class TodoItemViewHolder(
        parent: ViewGroup,
        override val containerView: View = parent.inflateView(layout.view_todo_item)
    ) : RecyclerView.ViewHolder(containerView), LayoutContainer {

        private val binding = ViewTodoItemBinding.bind(containerView)

        fun bind(item: TodoItem, onItemStateChanged: ((Long, Boolean) -> Unit)) = with(binding) {
            isDoneCheck.setOnCheckedChangeListener(null)
            content.text = item.content
            isDoneCheck.isChecked = item.isDone
            isDoneCheck.setOnCheckedChangeListener { _, isDone ->
                onItemStateChanged(item.id, isDone)
            }
        }
    }
}
