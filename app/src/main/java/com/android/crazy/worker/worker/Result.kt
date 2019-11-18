package com.android.crazy.worker.worker

/**
 * Base class for work result (success/failure) notification.
 * The [WorkId] is passed as parameter
 */
sealed class Result<r>(val workId : WorkId)

/**
 * Success data class. The result is stored in the field res
 * as a generic [r]
 * it extends the [Result] class so it stores the [WorkId]
 */
class Success<r>(workId : WorkId, val res: r) : Result<r>(workId) {
    override fun toString() = res.toString()
}

/**
 * Failure class, if any error is generated during the
 * work execution its propageted to the [cause] field.
 * it extends the [Result] class so it stores the [WorkId]
 */
class Failure(workId : WorkId, val cause: Throwable?) : Result<Unit>(workId) {
    override fun toString() = cause?.toString() ?: "Unknown"
}
