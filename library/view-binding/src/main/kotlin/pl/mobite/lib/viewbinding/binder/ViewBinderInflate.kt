package pl.mobite.lib.viewbinding.binder

import android.view.LayoutInflater
import androidx.viewbinding.ViewBinding
import kotlin.reflect.KClass
import kotlin.reflect.cast

/**
 * Implementation of [ViewBinder] which corresponds to the [ViewBinderType.INFLATE] type.
 * It creates [ViewBinding] using `ViewBinding.inflate(LayoutInflater)` function.
 *
 * @param viewBindingClass - klass of expected [ViewBinding]
 * @param getLayoutInflater - provider of [LayoutInflater] object
 */
class ViewBinderInflate<in T : Any, VB : ViewBinding>(
    private val viewBindingClass: KClass<VB>,
    private val getLayoutInflater: (T) -> LayoutInflater
) : ViewBinder<T, VB> {

    /**
     * Cache static method `ViewBinding.inflate(LayoutInflater)`
     */
    private val bindViewMethod by lazy(LazyThreadSafetyMode.NONE) {
        viewBindingClass.java.getMethod("inflate", LayoutInflater::class.java)
    }

    /**
     * Create new [ViewBinding] instance
     */
    override fun bind(owner: T): VB = viewBindingClass.cast(
        bindViewMethod(null, getLayoutInflater(owner))
    )
}
