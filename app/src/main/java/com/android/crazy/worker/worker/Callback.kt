package com.android.crazy.worker.worker


typealias OnSuccess<r> = (result: Success<r>) -> Unit
typealias OnFailure    = (failure: Failure) -> Unit

/**
 * Interface used for for Work notification
 *
 */
interface Callback<r> {
    /**
     * Called when the work as completed to return
     * the result. The [Success] result is dispatched
     * in the main thread
     */
    fun onComplete(result: Success<r>)
    /**
     * Called when same exception happens
     * during work execution or the user cancel
     * the it calling cancel. The [Failure]
     * cause is propagated as parameter [t]
     */
    fun onFailure(t: Failure)
}

/**
 * Wrapper class used to write
 * the callback object as lambda
 */
class CallbackWrapper<r>(private val onSuccess: OnSuccess<r>?, private val onFailure: OnFailure?): Callback<r> {
    override fun onComplete(result: Success<r>)= onSuccess?.invoke(result)?: Unit
    override fun onFailure(failure: Failure)= onFailure?.invoke(failure)?: Unit
}
