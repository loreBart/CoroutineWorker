package com.android.crazy.worker.worker


typealias OnSuccess<R> = (result: Success<R>) -> Unit
typealias OnFailure    = (failure: Failure) -> Unit

/**
 * Interface used for for Work notification
 *
 */
interface Callback<R> {
    fun onComplete(result: Success<R>)
    fun onFailure(t: Failure)
}

open class CallbackWrapper<R>(private val onSuccess: OnSuccess<R>?, private val onFailure: OnFailure?): Callback<R> {
    override fun onComplete(result: Success<R>)= onSuccess?.invoke(result) ?: Unit
    override fun onFailure(failure: Failure)= onFailure?.invoke(failure) ?: Unit
}
