package com.android.crazy.worker.worker

import kotlinx.coroutines.Deferred


/**
 * Interface that represents a scheduled job, see [Deferred]
 * for more information
 */
interface WorkSchedule<r> : Work<r> {
    /**
     * The [Work] reference identifier, see [WorkId]
     */
    fun id() : WorkId
    /**
     * The scheduled [Deferred] reference
     */
    fun job() : Deferred<r>
}
// ---------------------------------------------------------------------------------------------
