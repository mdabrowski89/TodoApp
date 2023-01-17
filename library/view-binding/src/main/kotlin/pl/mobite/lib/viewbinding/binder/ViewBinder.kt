package pl.mobite.lib.viewbinding.binder

import android.app.Activity
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import pl.mobite.lib.viewbinding.binder.ViewBinderType.BIND
import pl.mobite.lib.viewbinding.binder.ViewBinderType.INFLATE

/**
 * Interface of component which is responsible for creating an instance of [ViewBinding]
 *
 * @param T - type of object which will be using this binder (currently [Fragment] or [Activity])
 * @param VB - type of expected [ViewBinding]
 */
interface ViewBinder<in T : Any, VB : ViewBinding> {

    fun bind(owner: T): VB
}

/**
 * Two types of [ViewBinder] interface implementations:
 * - [BIND] is provided by [ViewBinderBind] class
 * - [INFLATE] is provided by [ViewBinderInflate] class
 */
enum class ViewBinderType { BIND, INFLATE }
