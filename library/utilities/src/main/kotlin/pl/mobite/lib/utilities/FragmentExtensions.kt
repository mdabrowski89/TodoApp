package pl.mobite.lib.utilities

import androidx.fragment.app.Fragment
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.launch


fun <T>Flow<T>.collectWithViewLifecycle(fragment: Fragment, flowCollector: FlowCollector<T>) {
    fragment.viewLifecycleOwner.lifecycleScope.launch {
        this@collectWithViewLifecycle
            .flowWithLifecycle(fragment.viewLifecycleOwner.lifecycle)
            .collect(flowCollector)
    }
}
