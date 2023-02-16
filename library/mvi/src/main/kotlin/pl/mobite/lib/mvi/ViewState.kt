package pl.mobite.lib.mvi

import android.os.Parcelable
import kotlinx.coroutines.flow.Flow

interface ViewState : Parcelable

class Action<VS : ViewState>(val id: String, val process: () -> Flow<Reduction<VS>>)

typealias Reduction<VS> = (VS) -> VS
