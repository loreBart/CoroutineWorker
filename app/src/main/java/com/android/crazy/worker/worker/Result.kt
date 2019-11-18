package com.android.crazy.worker.worker

/**
 * Base class for work result (success/failure) notification
 */
sealed class Result<R>(val workId : WorkId)

/**
 * Success data class. The result is stored in the field res
 */
class Success<R>(workId : WorkId, val res: R) : Result<R>(workId) {
    override fun toString() = res.toString()
}

/**
 * Failure class, if the scheduled work fails its
 * execution the @see
 */
class Failure(workId : WorkId, val cause: Throwable?) : Result<Unit>(workId) {
    override fun toString() = cause?.toString() ?: "Unknown"
}
