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

class TodoListFragment: Fragment(layout.fragment_todo_list) {

    private val binding: FragmentTodoListBinding by viewBinding()
    private val viewModel: TodoListViewModel by viewModels()

    private val todoListAdapter = TodoListAdapter { item, isDone ->
        viewModel.updateItem(item, isDone)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initTodoList()
        initButtons()
        initRender()
    }

    private fun initTodoList() = with(binding) {
        todoList.adapter = todoListAdapter
    }

    private fun initButtons() = with(binding) {
        addItemButton.setOnClickListener {
            viewModel.addItem(newItemInput.text.toString())
        }
        deleteCompletedItems.setOnClickListener {
            viewModel.deleteCompletedItems()
        }
    }

    private fun initRender() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.viewStateFlow.collect { viewState ->
                    binding.render(viewState)
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.loadItems()
    }

    private fun FragmentTodoListBinding.render(viewState: TodoListViewState) = with(viewState) {
        progressBar.isVisible = progressVisible
        newItemInput.isEnabled = addingItemsEnabled
        addItemButton.isEnabled = addingItemsEnabled
        deleteCompletedItems.isEnabled = deleteButtonEnabled
        todoListAdapter.submitList(todoItems)
    }
}
