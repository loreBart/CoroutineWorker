# CoroutineWorker

Simple Android minimalist framework to write background task
using coroutine.

Written in Kotlin.

Example of use:
            val args1 = Bundle()
            val workId1 = worker.exec(args1, {
              // Do some network or IO call 
            }, { // OnSuccess callback
              // it.workId -> the work id
              // it.res    -> the result returned from work lambda
            }, {
              // it.workId -> the work id
              // it.cause  -> the error cause
            })

