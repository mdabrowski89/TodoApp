package pl.mobite.lib.viewbinding

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

/**
 * Triggers [action] when [LifecycleOwner] reaches
 * [androidx.lifecycle.Lifecycle.State.DESTROYED] state.
 */
class OnDestroyLifecycleObserver(
    private val action: (owner: LifecycleOwner) -> Unit
) : DefaultLifecycleObserver {

    override fun onDestroy(owner: LifecycleOwner) {
        owner.lifecycle.removeObserver(this)
        action(owner)
    }
}
