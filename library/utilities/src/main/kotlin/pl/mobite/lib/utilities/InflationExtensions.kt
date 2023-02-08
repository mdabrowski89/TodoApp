package pl.mobite.lib.utilities

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes

val ViewGroup.inflater: LayoutInflater
    get() = LayoutInflater.from(context)

fun ViewGroup.inflateView(@LayoutRes layout: Int, attachToRoot: Boolean = false): View {
    return inflater.inflate(layout, this, attachToRoot)
}
