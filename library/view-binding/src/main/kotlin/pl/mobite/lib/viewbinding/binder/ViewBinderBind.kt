package pl.mobite.lib.viewbinding.binder

import android.view.View
import androidx.viewbinding.ViewBinding
import kotlin.reflect.KClass
import kotlin.reflect.cast

/**
 * Implementation of [ViewBinder] which corresponds to the [ViewBinderType.BIND] type.
 * It creates [ViewBinding] using `ViewBinding.bind(View)` function.
 *
 * @param viewBindingClass - klass of expected [ViewBinding]
 * @param getView - provider of [View] object
 */
class ViewBinderBind<in T : Any, VB : ViewBinding>(
    private val viewBindingClass: KClass<VB>,
    private val getView: (T) -> View
) : ViewBinder<T, VB> {

    /**
     * Cache static method `ViewBinding.bind(View)`
     */
    private val bindViewMethod by lazy(LazyThreadSafetyMode.NONE) {
        viewBindingClass.java.getMethod("bind", View::class.java)
    }

    /**
     * Create new [ViewBinding] instance
     */
    override fun bind(owner: T): VB = viewBindingClass.cast(
        bindViewMethod(null, getView(owner))
    )
}
