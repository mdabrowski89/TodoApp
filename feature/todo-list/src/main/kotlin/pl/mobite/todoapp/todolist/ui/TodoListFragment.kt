package pl.mobite.todoapp.todolist.ui

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch
import pl.mobite.lib.viewbinding.viewBinding
import pl.mobite.todoapp.todolist.R.layout
import pl.mobite.todoapp.todolist.databinding.FragmentTodoListBinding
import pl.mobite.todoapp.todolist.presentation.TodoListViewState

class TodoListFragment: Fragment(layout.fragment_todo_list) {

    private val binding: FragmentTodoListBinding by viewBinding()
    private val viewModel: TodoListViewModel by viewModels()

    private val todoListAdapter = TodoListAdapter { itemId, isDone ->
        viewModel.todolistStore.updateItem(itemId, isDone)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            todoList.adapter = todoListAdapter
            addItemButton.setOnClickListener {
                viewModel.todolistStore.addItem(newItemInput.text.toString())
            }
            deleteCompletedItems.setOnClickListener {
                viewModel.todolistStore.deleteCompletedItems()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.todolistStore.viewStateFlow.collect { viewState ->
                    binding.render(viewState)
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.todolistStore.loadItems()
    }

    private fun FragmentTodoListBinding.render(viewState: TodoListViewState) = with(viewState) {
        progressBar.isVisible = inProgress
        newItemInput.isEnabled = !inProgress
        addItemButton.isEnabled = !inProgress

        deleteCompletedItems.isEnabled = todoItems.orEmpty().any { it.isDone }

        todoItems?.let { newItems ->
            todoListAdapter.submitList(newItems.toList())
        }
    }
}
