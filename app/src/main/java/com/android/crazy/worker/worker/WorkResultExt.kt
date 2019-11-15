package com.android.crazy.worker.worker


fun <R> WorkId.success(res: R) = Success(workId = this, res = res)
fun     WorkId.failure(cause: Throwable?) = Failure(workId = this, cause = cause)

