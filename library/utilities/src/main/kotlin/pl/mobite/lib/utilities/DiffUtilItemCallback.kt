package pl.mobite.lib.utilities

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil

inline fun <reified T : Any, S> diffUtilItemCallback(crossinline getId: T.() -> S) = object: DiffUtil.ItemCallback<T>() {

    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem.getId() == newItem.getId()
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem == newItem
    }
}
