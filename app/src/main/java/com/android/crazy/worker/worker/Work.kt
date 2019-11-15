package com.android.crazy.worker.worker

import android.os.Bundle


/**
 * Lambda function that take a Bundle
 * as parameter and return a generic
 */
typealias WorkFun<R> = suspend (args: Bundle) -> R


/**
 * A work is an abstract class
 * that defines a
 */
interface Work<R> {
    /**
     * Interface used to exec a work in background
     *
     * @return The result value of type R
     */
    suspend fun doWork(args: Bundle): R
    /**
     * This function is called by the the framework
     * to notify the work that the cancel signal has been
     * sent to this work by the user
     */
    fun cancel()
}

