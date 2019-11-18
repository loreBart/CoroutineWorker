package com.android.crazy.worker.worker


import android.os.Bundle
import com.android.crazy.worker.util.log.d
import kotlinx.coroutines.*
import java.util.concurrent.ConcurrentHashMap


/**
 * Class used for task execution using coroutine
 *
 * Experimental !!!!!!!!!!!!!!
 *
 * Some funny on coroutine
 */
class Worker {


    /**
     * An object that wrap a [WorkFun]
     */
    data class JobWrapper<r>(val workId: WorkId, val job: Deferred<r>)
    // ---------------------------------------------------------------------------------------------


    /**
     * An object that wrap a [WorkFun]
     */
    internal class WorkFunObject<r>(private val workFun: WorkFun<r>): Work<r> {
        override suspend fun doWork(args: Bundle): r = workFun.invoke(args)
        override fun cancel() { }
    }
    // ---------------------------------------------------------------------------------------------

    // /////////////////////////////////////////////////////////////////////////////////////////////
    //  WorkWrap
    // =============================================================================================
    internal class WorkWrap<r>(private val jobWrap: JobWrapper<r>, private val workWrap: Work<r>) : WorkSchedule<r>  {
        // Return the id
        override fun id() : WorkId = jobWrap.workId
        override fun workAsync() : Deferred<r> = jobWrap.job

        override suspend fun doWork(args: Bundle): r = workWrap.doWork(args)

        override fun cancel() = workWrap.cancel()

        override fun equals(other: Any?): Boolean {
            if (other === this) return true
            if (other is WorkWrap<*>) { return jobWrap.workId === other.jobWrap.workId }
            return false
        }
        override fun hashCode(): Int = jobWrap.workId.hashCode()
        override fun toString(): String = jobWrap.workId.toString()
    }
    // ---------------------------------------------------------------------------------------------


    // /////////////////////////////////////////////////////////////////////////////////////////////
    private val workerHandler = CoroutineExceptionHandler { _, exception ->
        println("WorkerHandler Caught $exception")
    }

    private val works: ConcurrentHashMap<WorkId, WorkSchedule<*> > = ConcurrentHashMap()
    private val workerScope = CoroutineScope(Dispatchers.IO + SupervisorJob(Job())) + CoroutineName("HelloCoroutine")
    // ---------------------------------------------------------------------------------------------


    /**
     * Execute a work in background and notify the callback of the result.
     * If some error occur during the work execution the failure callback
     * is called passing the error cause as parameter (if any).
     *
     * If
     *
     * @return The work identifier
     * @Experimental
     */

    fun <r> exec(args: Bundle, work: Work<r>, callback: Callback<r>) : WorkId {
        val workId = WorkId()
        if (!workerScope.isActive) {
            d("exec: worker not yet active")
            return WorkId.NULL
        }
        try {
            workerScope.ensureActive()
            workerScope.launch {
                val jobWrap = scheduleWork(workId, args, work)
                waitWork(jobWrap, work, callback)
            }
        } catch (e: Throwable) {
            d("exec $e")
        }
        return workId
    }

    fun <r> exec(args: Bundle, doWork: WorkFun<r>, callback: Callback<r>) : WorkId {
        return exec(args, WorkFunObject(doWork), callback)
    }

    fun <r> exec(args: Bundle, work: Work<r>, onSuccess: OnSuccess<r>) : WorkId {
        return exec(args, work, CallbackWrapper(onSuccess, null))
    }

    fun <r> exec(args: Bundle, work: Work<r>, onSuccess: OnSuccess<r>, onFailure: OnFailure?) : WorkId {
        return exec(args, work, CallbackWrapper(onSuccess, onFailure))
    }

    fun <r> exec(args: Bundle, doWork: WorkFun<r>, onSuccess: OnSuccess<r>) : WorkId {
        return exec(args, WorkFunObject(doWork), CallbackWrapper(onSuccess, null))
    }

    fun <r> exec(args: Bundle, doWork: WorkFun<r>, onSuccess: OnSuccess<r>, onFailure: OnFailure?) : WorkId {
        return exec(args, WorkFunObject(doWork), CallbackWrapper(onSuccess, onFailure))
    }

    /**
     * Returns true if the work referred by the given key
     * [WorkId] has been scheduled false otherwise
     */
    fun hasWork(workId : WorkId) : Boolean = works.contains(workId)


    fun <r> than(workId : WorkId, args: Bundle, doWork: WorkFun<r>, onSuccess: OnSuccess<r>) {
        val scheduled = scheduled(workId)
        val job = scheduled?.workAsync()
        if (job != null) {
        }
    }


    /**
     * Returns the scheduled [WorkSchedule] corresponding to the given
     * [WorkId], otherwise returns null
     */
    fun scheduled(workId : WorkId) : WorkSchedule<*>? = when(hasWork(workId)) {
        true  -> works.getValue(workId)
        false -> null
    }

    //fun hasWork(workId : WorkId) : Boolean = works.contains(workId)


    /**
     * Cancel a previous scheduled work, if any, passing
     * [WorkId] as parameter
     *
     * @Experimental
     */
    fun cancel(workId: WorkId) {
        val work = works[workId]
        var workStatus = WorkStatus.UNKNOWN
        if (work != null && work.workAsync().status() != WorkStatus.CANCELLED) {
            workStatus = work.workAsync().status()
            work.cancel()
            work.workAsync().cancel("Work $workId cancelled by user")
        }
        removeWork(workId, workStatus)
    }

    fun cancelAll() {
        // Cancel every work previously scheduled
        for (work in works) {
            cancel(work.key)
        }
        try {
            workerScope.cancel("cancelAll called")
        } catch (e:Throwable) {
            d("cancelAll $e")
        }
    }
    // ---------------------------------------------------------------------------------------------


    // =============================================================================================
    //  internal implementation
    // ---------------------------------------------------------------------------------------------
    private suspend fun <r> scheduleWork(workId: WorkId, args: Bundle, work: Work<r>): JobWrapper<r> {
        val workScope = workerScope.forWork(workId)
        val job : Deferred<r> = workScope.async(workerHandler) {
            work.doWork(args)
        }
        return JobWrapper(workId, job)
    }

    private suspend fun <r> waitWork(job: JobWrapper<r>, work: Work<r>, callback: Callback<r>) {
        val wrapper = WorkWrap(job, work)
        works[wrapper.id()] = wrapper
        var res : r? = null
        var t   : Throwable? = null
        try {
            res =  job.job.await()
        } catch (e: Throwable) {
            t = e
        }
        d("waitWork thread ${Thread.currentThread()}")

        if (res != null) {
            workerScope.launch(Dispatchers.Main) {
                callback.onComplete(job.workId.success(res))
            }
        } else {
            workerScope.launch(Dispatchers.Main) {
                callback.onFailure(job.workId.failure(t))
            }
        }
        // Now that we have done remove the work from the work list
        removeWork(job.workId, job.job.status())
    }

    private fun removeWork(workId: WorkId, workStatus: WorkStatus) {
        var message = "removeWork: work($workId) state $workStatus"
        message += when (works.contains(workId)) {
            true  -> {
                works.remove(workId)
                " removed from work list"
            }
            false -> { " can't be removed (Already removed?)" }
        }
        d(message)
    }

    private fun CoroutineScope.forWork(workId : WorkId) = this + CoroutineName(workId.id())
    // ---------------------------------------------------------------------------------------------

}

