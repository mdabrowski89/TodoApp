package pl.mobite.lib.viewbinding

import android.app.Activity
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding
import pl.mobite.lib.viewbinding.binder.ViewBinder
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * Delegated property which creates and stores a reference to the [ViewBinding] object.
 * [clearReference] method allows to clear the stored reference
 *
 * @param T - type of object which owns this delegated property (currently [Fragment] or [Activity])
 * @param VB - type of expected [ViewBinding]
 * @param binder - function responsible for creating [ViewBinding] object
 */
class ViewBindingProperty<in T : LifecycleOwner, VB : ViewBinding>(
    private val binder: ViewBinder<T, VB>,
    private val onBindingCreated: (ViewBindingProperty<T, VB>) -> Unit
) : ReadOnlyProperty<T, VB> {

    private var viewBinding: VB? = null

    /**
     * Return [ViewBinding] reference (and creates if needed using [binder] function)
     */
    override fun getValue(thisRef: T, property: KProperty<*>): VB =
        viewBinding ?: createBinding(thisRef)

    private fun createBinding(thisRef: T): VB {
        return binder.bind(thisRef).also {
            viewBinding = it
            onBindingCreated(this)
        }
    }

    @PublishedApi
    internal fun clearReference() {
        viewBinding = null
    }
}
