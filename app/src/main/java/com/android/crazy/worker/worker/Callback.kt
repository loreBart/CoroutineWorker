package com.android.crazy.worker.worker


typealias OnSuccess<R> = (result: Success<R>) -> Unit
typealias OnFailure    = (result: Failure) -> Unit

interface Callback<R> {
    fun onComplete(result: Success<R>)
    fun onFailure(t: Failure)
}

open class CallbackWrapper<R>(private val success: OnSuccess<R>?, private val failure: OnFailure?): Callback<R> {
    override fun onComplete(result: Success<R>)= success?.invoke(result)?:Unit
    override fun onFailure(reason: Failure)= failure?.invoke(reason)?:Unit
}

