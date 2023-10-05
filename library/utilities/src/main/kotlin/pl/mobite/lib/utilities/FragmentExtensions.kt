package pl.mobite.lib.utilities

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector

suspend fun <T> Flow<T>.collectWithLifecycle(lifecycle: Lifecycle, flowCollector: FlowCollector<T>) {
    flowWithLifecycle(lifecycle)
        .collect(flowCollector)
}
