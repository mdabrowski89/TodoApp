package pl.mobite.todoapp.todolist.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TodoItem(
    val id: Long,
    val content: String,
    val isDone: Boolean
) : Parcelable
