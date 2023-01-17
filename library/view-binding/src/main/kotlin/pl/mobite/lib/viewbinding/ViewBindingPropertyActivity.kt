package pl.mobite.lib.viewbinding

import android.R.id
import android.os.Handler
import android.os.Looper
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import pl.mobite.lib.viewbinding.binder.ViewBinder
import pl.mobite.lib.viewbinding.binder.ViewBinderBind
import pl.mobite.lib.viewbinding.binder.ViewBinderInflate
import pl.mobite.lib.viewbinding.binder.ViewBinderType

/**
 * Create new [ViewBinding] associated with the [AppCompatActivity]
 *
 * @param VB - class of expected [ViewBinding]
 * @param viewBinderType - type of [ViewBinder] which will be used to create the [ViewBinding],
 */
@JvmName("viewBindingFragment")
inline fun <reified VB : ViewBinding> AppCompatActivity.viewBinding(
    viewBinderType: ViewBinderType = ViewBinderType.BIND
): ViewBindingProperty<AppCompatActivity, VB> {

    val handler = Handler(Looper.getMainLooper())
    val binder = obtainActivityBinder<VB>(viewBinderType)

    return ViewBindingProperty(binder) { property ->
        lifecycle.addObserver(
            OnDestroyLifecycleObserver { handler.post { property.clearReference() } }
        )
    }
}

/**
 * Return [ViewBinder] object based on provided [viewBinderType]
 */
inline fun <reified VB : ViewBinding> obtainActivityBinder(
    viewBinderType: ViewBinderType
): ViewBinder<AppCompatActivity, VB> =
    when (viewBinderType) {
        ViewBinderType.BIND -> ViewBinderBind(VB::class) {
            it.findViewById<ViewGroup>(id.content).getChildAt(0)
        }
        ViewBinderType.INFLATE -> ViewBinderInflate(VB::class) { it.layoutInflater }
    }
