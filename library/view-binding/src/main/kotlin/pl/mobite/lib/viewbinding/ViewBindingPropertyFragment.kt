package pl.mobite.lib.viewbinding

import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import pl.mobite.lib.viewbinding.binder.ViewBinder
import pl.mobite.lib.viewbinding.binder.ViewBinderBind
import pl.mobite.lib.viewbinding.binder.ViewBinderInflate
import pl.mobite.lib.viewbinding.binder.ViewBinderType

/**
 * Create new [ViewBinding] associated with the [Fragment]
 *
 * @param VB - class of expected [ViewBinding]
 * @param viewBinderType - type of [ViewBinder] which will be used to create the [ViewBinding],
 */
@JvmName("viewBindingFragment")
inline fun <reified VB : ViewBinding> Fragment.viewBinding(
    viewBinderType: ViewBinderType = ViewBinderType.BIND
): ViewBindingProperty<Fragment, VB> {

    val handler = Handler(Looper.getMainLooper())
    val binder = obtainFragmentBinder<VB>(viewBinderType)

    return ViewBindingProperty(binder) { property ->
        viewLifecycleOwner.lifecycle.addObserver(
            OnDestroyLifecycleObserver { handler.post { property.clearReference() } }
        )
    }
}

/**
 * Return [ViewBinder] object based on provided [viewBinderType]
 */
inline fun <reified VB : ViewBinding> obtainFragmentBinder(
    viewBinderType: ViewBinderType
): ViewBinder<Fragment, VB> =
    when (viewBinderType) {
        ViewBinderType.BIND -> ViewBinderBind(VB::class) { it.requireView() }
        ViewBinderType.INFLATE -> ViewBinderInflate(VB::class) { it.layoutInflater }
    }
