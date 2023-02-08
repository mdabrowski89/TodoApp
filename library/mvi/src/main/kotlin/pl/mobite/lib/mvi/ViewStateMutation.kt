package pl.mobite.lib.mvi

typealias ViewStateMutation<VS> = (VS) -> VS

abstract class ViewStateMutator<VS : ViewState> {

    @Suppress("NOTHING_TO_INLINE")
    inline fun mutate(noinline mutation: ViewStateMutation<VS>): ViewStateMutation<VS> = mutation
}
