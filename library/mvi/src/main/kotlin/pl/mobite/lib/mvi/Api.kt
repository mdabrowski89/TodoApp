package pl.mobite.lib.mvi

import android.os.Parcelable
import kotlinx.coroutines.flow.Flow

/**
 * Representation of the UI state. By convention it is implemented by data classes where each field represents the state of one UI element.
 */
interface ViewState : Parcelable

/**
 * Alias for the function which "reduces" [ViewState] (changes one [ViewState] into a different one)
 */
typealias Reduction<VS> = (VS) -> VS

/**
 * Representation of an operation with result in multiple changes of the UI. Those changes are represented by the [Flow] of [Reduction] objects.
 */
class Action<VS : ViewState>(val id: String, val process: () -> Flow<Reduction<VS>>)
