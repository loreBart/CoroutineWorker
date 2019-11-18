package com.android.crazy.worker.worker

import android.os.Bundle


/**
 * Lambda suspend function that take a [Bundle]
 * as parameter and return a generic type [r]
 */
typealias WorkFun<r> = suspend (args: Bundle) -> r


/**
 * A work is an interface that defines a
 * background execution task.
 * A [Bundle] is passed as parameter
 * and a generic type [r]
 */
interface Work<r> {
    /**
     * Function used to execute a background work
     *
     * @return The result value of type r
     */
    suspend fun doWork(args: Bundle): r
    /**
     * This function is called by the the framework
     * to notify the work that the cancel signal has been
     * sent to this work by the user
     */
    fun cancel()
}

