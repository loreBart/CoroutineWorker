package com.android.crazy.worker.worker


import android.os.Bundle
import com.android.crazy.worker.util.log.d
import kotlinx.coroutines.*


/**
 * Experimental !!!!!!!!!!!!!!
 *
 * Some funny on coroutine
 */
class Worker {


    // /////////////////////////////////////////////////////////////////////////////////////////////
    //  JobWrap
    // =============================================================================================
    data class JobWrap(val workId: WorkId, val job: Job)
    // ---------------------------------------------------------------------------------------------

    // /////////////////////////////////////////////////////////////////////////////////////////////
    //  WorkInterface
    // =============================================================================================
    interface WorkInterface<R> : Work<R> {
        fun id() : WorkId
        fun job() : Job
    }
    // ---------------------------------------------------------------------------------------------

    // /////////////////////////////////////////////////////////////////////////////////////////////
    //  WorkFunObject
    // =============================================================================================
    internal class WorkFunObject<R>(private val workFun: WorkFun<R>): Work<R> {
        override suspend fun doWork(args: Bundle): R = workFun.invoke(args)
        override fun cancel() { }
    }
    // ---------------------------------------------------------------------------------------------

    // /////////////////////////////////////////////////////////////////////////////////////////////
    //  WorkWrap
    // =============================================================================================
    internal class WorkWrap<R>(private val jobWrap: JobWrap, private val workWrap: Work<R>) : WorkInterface<R>  {
        // Return the id
        override fun id() : WorkId = jobWrap.workId
        override fun job() : Job = jobWrap.job

        override suspend fun doWork(args: Bundle): R = workWrap.doWork(args)

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

    private val works: MutableMap<WorkId, WorkInterface<*> > = mutableMapOf()
    private val workerScope = CoroutineScope(Dispatchers.IO + SupervisorJob(Job()))
    // ---------------------------------------------------------------------------------------------


    /**
     * Execute a work in background and notify the callback of the result
     *
     * @return Job
     * @Experimental
     */
    fun <R> exec(args: Bundle, work: Work<R>, callback: Callback<R>) : WorkId {
        val workId = WorkId()
        val jobWrap = scheduleWork(workId, args, work, callback)
        return addWork(jobWrap, work)
    }

    fun <R> exec(args: Bundle, doWork: WorkFun<R>, callback: Callback<R>) : WorkId {
        return exec(args, WorkFunObject(doWork), callback)
    }

    fun <R> exec(args: Bundle, work: Work<R>, onSuccess: OnSuccess<R>) : WorkId {
        return exec(args, work, CallbackWrapper(onSuccess, null))
    }

    fun <R> exec(args: Bundle, work: Work<R>, onSuccess: OnSuccess<R>, onFailure: OnFailure?) : WorkId {
        return exec(args, work, CallbackWrapper(onSuccess, onFailure))
    }

    fun <R> exec(args: Bundle, doWork: WorkFun<R>, onSuccess: OnSuccess<R>) : WorkId {
        return exec(args, WorkFunObject(doWork), CallbackWrapper(onSuccess, null))
    }

    fun <R> exec(args: Bundle, doWork: WorkFun<R>, onSuccess: OnSuccess<R>, onFailure: OnFailure?) : WorkId {
        return exec(args, WorkFunObject(doWork), CallbackWrapper(onSuccess, onFailure))
    }

    /**
     * Cancel a previous scheduled work, if any
     * @Experimental
     */
    fun cancel(workId: WorkId) {
        d("cancel: workId -> ${workId.id()}")
        val work = works[workId]
        var workStatus = WorkStatus.UNKNOWN
        if (work != null && work.job().status() != WorkStatus.CANCELLED) {
            workStatus = work.job().status()
            work.cancel()
            work.job().cancel("Cancelled by user")
        }
        removeWork(workId, workStatus)
    }

    fun cancelAll() {
        d("cancelAll")
        for (work in works) {
            cancel(work.key)
        }
        workerScope.cancel("cancelAll")
    }

    // =============================================================================================
    //  internal implementation
    // ---------------------------------------------------------------------------------------------
    private fun <R> scheduleWork(workId: WorkId, args: Bundle, work: Work<R>, callback: Callback<R>): JobWrap {
        val job = workerScope.async(workerHandler) {
            doWork(workId, args, work, callback)
        }
        return JobWrap(workId, job)
    }

    private fun <R> addWork(job: JobWrap, work: Work<R>) : WorkId {
        val wrapper = WorkWrap(job, work)
        works[wrapper.id()] = wrapper
        return wrapper.id()
    }

    private fun removeWork(workId: WorkId, workStatus: WorkStatus) {
        var message = "cancel: work($workId) state $workStatus"
        message += when (works.contains(workId)) {
            true  -> {
                works.remove(workId)
                " cancelled"
            }
            false -> " can't be removed (Already removed?)"
        }
        d(message)
    }

    private suspend fun <R> doWork(workId: WorkId, args: Bundle, work: Work<R>, callback: Callback<R>) {
        var res : R? = null
        var t   : Throwable? = null
        try {
            res =  work.doWork(args)
        } catch (e: Exception) {
            d("doWork", e)
            t = e
        }
        if (res != null) { callback.onComplete(workId.success(res)) }
        else             { callback.onFailure(workId.failure(t))    }
    }
    // ---------------------------------------------------------------------------------------------


}

