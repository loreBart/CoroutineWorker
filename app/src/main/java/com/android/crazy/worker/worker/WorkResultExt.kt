package com.android.crazy.worker.worker


fun <r> WorkId.success(res: r) = Success(workId = this, res = res)
fun     WorkId.failure(cause: Throwable?) = Failure(workId = this, cause = cause)

