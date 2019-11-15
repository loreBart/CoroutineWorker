package com.android.crazy.worker.util.log


// =================================================================================================
//  Log
// -------------------------------------------------------------------------------------------------
class Log : LogEngine {

    companion object { val log = Log() }

    private val engines = mutableListOf<LogEngine>()

    constructor() {
        // As default add the Android log engine
        engines.add(AndroidLogEngine())
    }

    // =============================================================================================
    override fun v(msg: String)                { for (e in engines) { e.v(msg) }    }
    override fun i(msg: String)                { for (e in engines) { e.i(msg) }    }
    override fun d(msg: String, t: Throwable?) { for (e in engines) { e.d(msg, t) } }
    override fun e(msg: String, t: Throwable?) { for (e in engines) { e.e(msg, t) } }
    override fun enable()                      { for (e in engines) { e.enable()  } }
    override fun disable()                     { for (e in engines) { e.disable() } }
    // =============================================================================================

    fun add(e: LogEngine) = engines.add(e)
    fun remove(e: LogEngine) = engines.remove(e)

}


fun v(msg: String) = Log.log.v(msg)
fun i(msg: String) = Log.log.i(msg)
fun d(msg: String, t: Throwable? = null) = Log.log.d(msg, t)
fun e(msg: String, t: Throwable? = null) = Log.log.e(msg, t)
