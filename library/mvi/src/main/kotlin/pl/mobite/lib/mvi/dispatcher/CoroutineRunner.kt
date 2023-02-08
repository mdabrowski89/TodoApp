package pl.mobite.lib.mvi.dispatcher

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import kotlin.coroutines.CoroutineContext

class CoroutineRunner {

    private val scopes: HashMap<Any, CoroutineScope> = hashMapOf()

    fun CoroutineScope.launchCoroutine(
        id: Any,
        context: CoroutineContext = Dispatchers.Default,
        block: suspend CoroutineScope.() -> Unit,
    ) : Job {
        val scope = scopes.getOrPut(id) { this + Job() }
        return scope.launch(context, block = block)
    }

    suspend fun cancelAndJoin(id: Any) {
        val job = getJob(id) ?: return
        val children = job.children.toList()
        job.cancelChildren()
        children.joinAll()
    }

    fun getChildren(id: Any) = getJob(id)?.children?.toList().orEmpty()

    private fun getJob(id: Any) = scopes[id]?.coroutineContext?.get(Job)
}
