package pl.mobite.lib.mvi

import android.os.Parcelable
import kotlinx.coroutines.flow.Flow

interface ViewState : Parcelable

interface Action<VS : ViewState> {

    operator fun invoke(): Flow<Reduction<VS>>

    fun getId(): String
}

typealias Reduction<VS> = (VS) -> VS
