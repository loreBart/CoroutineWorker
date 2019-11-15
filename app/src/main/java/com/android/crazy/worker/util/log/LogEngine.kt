package com.android.crazy.worker.util.log

/**
 * Base interface for log engines
 */
interface LogEngine {
    fun v(msg: String)
    fun i(msg: String)
    fun d(msg: String, t: Throwable? = null)
    fun e(msg: String, t: Throwable? = null)
    fun enable()
    fun disable()
}
