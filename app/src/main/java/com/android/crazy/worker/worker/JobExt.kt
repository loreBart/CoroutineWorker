package com.android.crazy.worker.worker

import kotlinx.coroutines.Job

fun Job.status() : WorkStatus {
    if (isActive)    { return WorkStatus.ACTIVE    }
    if (isCompleted) { return WorkStatus.COMPLETED }
    if (isCancelled) { return WorkStatus.CANCELLED }
    return WorkStatus.UNKNOWN
}
