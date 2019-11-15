package com.android.crazy.worker.worker

/**
 * Base class for work result (success/failure) notification
 */
sealed class Result<R>(val workId : WorkId)

/**
 * Success
 */
class Success<R>(workId : WorkId, val res: R) : Result<R>(workId) {
    override fun toString() = res.toString()
}

/**
 * Failure
 */
class Failure(workId : WorkId, val cause: Throwable?) : Result<Unit>(workId) {
    override fun toString() = cause?.toString() ?: "Unknown"
}
