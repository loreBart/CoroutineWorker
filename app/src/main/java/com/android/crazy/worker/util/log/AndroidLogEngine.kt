package com.android.crazy.worker.util.log

import android.util.Log

class AndroidLogEngine(private var tag : String = "###") : LogEngine {
    private var D : Boolean = true
    override fun v(msg: String)                { if (D) Log.v(tag, msg)    }
    override fun i(msg: String)                { if (D) Log.i(tag, msg)    }
    override fun d(msg: String, t: Throwable?) { if (D) Log.d(tag, msg, t) }
    override fun e(msg: String, t: Throwable?) { if (D) Log.e(tag, msg, t) }
    override fun enable()                      { D = true                  }
    override fun disable()                     { D = false                 }
}

