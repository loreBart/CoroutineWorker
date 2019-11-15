package com.android.crazy.worker.worker


typealias OnSuccess<R> = (result: Success<R>) -> Unit
typealias OnFailure    = (failure: Failure) -> Unit

/**
 * Interface used for for Work notification
 *
 */
interface Callback<R> {
    /**
     * Called when the work as completed to return
     * the result to the called thread
     */
    fun onComplete(result: Success<R>)
    /**
     * Called when same exception happens
     * during work execution or the user cancel
     * the it calling cancel
     */
    fun onFailure(t: Failure)
}

/**
 * Wrapper class used to write
 * the callback object as lambda
 */
open class CallbackWrapper<R>(private val onSuccess: OnSuccess<R>?, private val onFailure: OnFailure?): Callback<R> {
    override fun onComplete(result: Success<R>)= onSuccess?.invoke(result)?: Unit
    override fun onFailure(failure: Failure)= onFailure?.invoke(failure)?: Unit
}
