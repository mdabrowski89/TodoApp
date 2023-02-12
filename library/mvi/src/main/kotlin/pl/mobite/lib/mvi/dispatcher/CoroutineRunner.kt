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

    private val scopes: HashMap<String, CoroutineScope> = hashMapOf()

    fun CoroutineScope.launchCoroutine(
        id: String,
        context: CoroutineContext = Dispatchers.Default,
        block: suspend CoroutineScope.() -> Unit,
    ) : Job {
        val scope = scopes.getOrPut(id) { this + Job() }
        return scope.launch(context, block = block)
    }

    suspend fun cancelAndJoin(id: String) {
        val job = getJob(id) ?: return
        val children = job.children.toList()
        job.cancelChildren()
        children.joinAll()
    }

    private fun getJob(id: String) = scopes[id]?.coroutineContext?.get(Job)
}
