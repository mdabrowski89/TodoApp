package pl.mobite.lib.mvi

import android.os.Parcelable
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

/**
 * Representation of the UI state. By convention it is implemented by data classes where each field represents the state of one UI element.
 */
interface ViewState : Parcelable

/**
 * Alias for the function which "reduces" [ViewState] (changes one [ViewState] into a different one)
 */
typealias Reducer<VS> = VS.() -> VS

/**
 * Representation of an asynchronous operation with results in one or multiple UI changes.
 * Each UI change is represented by the [Reducer] which is executed on the latest [ViewState] object.
 * Action are processed by the [ActionProcessor] - thanks to the usage of coroutines multiple actions can run at the same time.
 *
 * @param id - identification string of this action (can be any string). New action cancel the currently processed action with the same [id].
 * @param dispatcher - dispatcher for new coroutine on which action body is invoked.
 * @param process - action body, it is invoked in new a coroutine, on a [dispatcher] and in [viewModelScope].
 * The [Reducer]s which are emitted from the resulted flow are executed on a main thread ([Dispatchers.Main]).
 */
class Action<VS : ViewState>(
    val id: String,
    val dispatcher: CoroutineDispatcher = Dispatchers.IO, // TODO: test Dispatchers.Default vs Dispatchers.IO
    val process: () -> Flow<Reducer<VS>>,
)

interface SideEffect
