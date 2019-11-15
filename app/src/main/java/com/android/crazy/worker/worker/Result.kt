package com.android.crazy.worker.worker


sealed class Result<R>(val workId : WorkId)


open class Success<R>(workId : WorkId, val res: R) : Result<R>(workId) {
    override fun toString() = res.toString()
}

open class Failure(workId : WorkId, val cause: Throwable?) : Result<Unit>(workId) {
    override fun toString() = cause?.toString() ?: "Unknown"
}
