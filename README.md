# CoroutineWorker

Simple Android minimalist framework to write background task
using coroutine.

Written in Kotlin just for fun.

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


Another way to use: extending the lamda work function


            var work2: WorkFun<String> =  {
                 return someString
            }
            val id2 = worker.exec(Bundle(), work2, {
                // onSuccess
            }, {
                // onFailure
            })


Another way is extend the Work class 

            class HeavyWork : Work<JSONObject> {
                override suspend fun doWork(args: Bundle): JSONObject {
                    val json = fetchSomeJsonObject()   
                    return json
                }
                override fun cancel() {}
            }
            val id3 = worker.exec(Bundle(), HeavyWork(), {
                // onSuccess 
            }, {
                // onFailure
            })

