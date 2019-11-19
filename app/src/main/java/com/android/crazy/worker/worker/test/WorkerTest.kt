package com.android.crazy.worker.worker.test

import android.content.Context
import android.os.Bundle
import android.os.SystemClock
import com.android.crazy.worker.util.log.d
import com.android.crazy.worker.worker.WorkFun
import com.android.crazy.worker.worker.Worker
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import android.content.res.AssetManager
import android.os.Handler
import com.android.crazy.worker.worker.Work
import kotlinx.serialization.json.json
import java.io.IOException


class WorkerTest(val context: Context) {

    val worker = Worker()

    fun test() {

        d("========================================================")
        d("===================== TEST STARTED =====================")
        runBlocking {
            worker.cancelAll()

            val args = Bundle()
            args.putInt("repeat_count", 10)
            val t0 = SystemClock.elapsedRealtime()
            // -----------------------------------------------------------------------
            //  Work 1
            // -----------------------------------------------------------------------
            val t1 = SystemClock.elapsedRealtime()
            val id1 = worker.exec(Bundle(), {
                val ret = JSONObject("Carla")
                ret.put("surname", "merendine")
                ret.put("age", 26)
                ret.put("year", 1993)
                ret
            }, {
                d("onSuccess<1> id -> ${it.workId} ret -> ${it.res}")
            }, {
                d("onFailure<1> id -> ${it.workId} ret -> ${it?.cause}")
            })
            d("|---> work 1 $id1 scheduled in ${t1 - t0} millis")
            // -----------------------------------------------------------------------
            //  Work 2
            // -----------------------------------------------------------------------
            val t2 = SystemClock.elapsedRealtime()
            var work2: WorkFun<String> =  {
                try {
                    for (i in 0..10) {
                        d("string ${Thread.currentThread()} sleep ...")
                        delay(1000)
                    }
                    "Lorenzo"
                } catch (e: Exception) {
                    "Cancelled $e"
                }
            }
            val id2 = worker.exec(Bundle(), work2, {
                d("onSuccess<2> id -> ${it.workId} ret -> ${it.res}")
            }, {
                d("onFailure<2> id -> ${it?.workId} ret -> ${it?.cause}")
            })
            d("|---> work 2 $id2 scheduled in ${t2 - t1} millis")
            // -----------------------------------------------------------------------
            //  Work 3
            // -----------------------------------------------------------------------
            val t3 = SystemClock.elapsedRealtime()
            var work3: WorkFun<JSONObject> =
                {
                    for (i in 0..10) {
                        d("string ${Thread.currentThread()} sleep ...")
                        delay(1000)
                        throw  RuntimeException("CODE CRASH")
                    }
                    JSONObject()
                }

            val id3 = worker.exec(Bundle(), work3, {
                d("onSuccess<3> id -> ${it.workId} ret -> ${it.res}")
            }, {
                d("onFailure<3> id -> ${it?.workId} ret -> ${it?.cause}")
            })
            d("|---> work 3 $id3 scheduled in ${t3 - t2} millis")
            // -----------------------------------------------------------------------
            //  Work 4
            // -----------------------------------------------------------------------
            val t4 = SystemClock.elapsedRealtime()
            var work4: WorkFun<JSONObject> =
                {
                    val json = JSONObject()
                    for (i in 0..10) {
                        delay(5000)
                        json.put("key_$i", i)
                        d("string ${Thread.currentThread()} sleep ...")
                    }
                    json
                }

            val id4 = worker.exec(Bundle(), work4, {
                d("onSuccess<4> id -> ${it.workId} ret -> ${it.res}")
            }, {
                d("onFailure<4> id -> ${it?.workId} ret -> ${it?.cause}")
            })
            d("|---> work 4 $id4 scheduled in ${t4 - t3} millis")
            // -----------------------------------------------------------------------
            //  Work 5
            // -----------------------------------------------------------------------
            val arg5 = Bundle()
            arg5.putInt("loop_count", 300)
            val id5 = worker.exec(arg5, {
                val loopFor = it.getInt("loop_count")
                d("work 5 loop for ${loopFor}")
                for (i in 0..loopFor) {
                    delay((10*i).toLong())
                }
                "LORELOOPED"
            }, {
                d("onSuccess<5> id -> ${it.workId} ret -> ${it.res}")
            }, {
                d("onFailure<5> id -> ${it?.workId} ret -> ${it?.cause}")
            })
            val t5 = SystemClock.elapsedRealtime()
            d("|---> work 5 $id5 scheduled in ${t5 - t4} millis")
            val idDel1 = worker.exec(Bundle(), {
                delay(5000)
                d("|---> Cancelling work 5")
                worker.cancel(id5)
            }, {}, {})
            // -----------------------------------------------------------------------
            //  Work 6
            // -----------------------------------------------------------------------
            val t6 = SystemClock.elapsedRealtime()
            val args6 = Bundle()
            args6.putString("asset_path", "plants.json")
            val id6 = worker.exec(args6, {
                val assetManager: AssetManager = context.assets
                val path = args6.getString("asset_path") as String
                val jsonPlat = readAsset(assetManager, path)
                jsonPlat
            }, {
                d("onSuccess<6> id -> ${it.workId} ret -> ${it.res}")
            }, {
                d("onFailure<6> id -> ${it?.workId} ret -> ${it?.cause}")
            })
            d("|---> work 6 $id6 scheduled in ${t6 - t5} millis")
            // -----------------------------------------------------------------------
            //  Work 7
            // -----------------------------------------------------------------------
            val t7 = SystemClock.elapsedRealtime()
            val id7 = worker.exec(args6, {
                val assetManager: AssetManager = context.assets
                val path = args6.getString("asset_path") as String
                val jsonPlat = readAsset(assetManager, path)
                jsonPlat
            }, {
                d("onSuccess<7> id -> ${it.workId} ret -> ${it.res}")
            }, {
                d("onFailure<7> id -> ${it?.workId} ret -> ${it?.cause}")
            })
            d("|---> work 7 $id7 scheduled in ${t7 - t6} millis")
            // -----------------------------------------------------------------------
            //  Work 8
            // -----------------------------------------------------------------------
            val t8 = SystemClock.elapsedRealtime()
            val id8 = worker.exec(args6, {
                val assetManager: AssetManager = context.assets
                val path = args6.getString("asset_path") as String
                val jsonPlat = readAsset(assetManager, path)
                jsonPlat
            }, {
                d("onSuccess<8> id -> ${it.workId} ret -> ${it.res}")
            }, {
                d("onFailure<8> id -> ${it?.workId} ret -> ${it?.cause}")
            })
            d("|---> work 8 $id8 scheduled in ${t8 - t7} millis")
            // -----------------------------------------------------------------------
            //  Work 9
            // -----------------------------------------------------------------------
            Handler().postDelayed({
                worker.cancelAll()
                d("===> BEFORE CANCELL All ...")
            }, 3000)

            Handler().postDelayed({
                d("===> BEFORE CANCELL All ...")
                worker.exec(Bundle(), {
                    for (i in 0..9) {
                        delay((50 * i).toLong())
                    }
                    "Lorenzo is suffering"
                }, {
                    d("onSuccess<9> id -> ${it.workId} ret -> ${it.res}")
                })
            }, 5000)


        }
        d("===================== TEST LAUNCHED ====================")
        d("========================================================")
    }


    // =============================================================================================
    private fun readAsset(mgr: AssetManager, path: String): String {
        var contents = ""
        var `is`: InputStream? = null
        var reader: BufferedReader? = null
        try {
            `is` = mgr.open(path)
            reader = BufferedReader(InputStreamReader(`is`))
            contents = reader.readLine()
            while ((reader.read()) != -1) {
                contents += reader.readLine() + '\n'
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (`is` != null) {
                try { `is`.close() }
                catch (ignored: IOException) {}
            }
            if (reader != null) {
                try { reader.close() }
                catch (ignored: IOException) {}
           }
        }
        return contents
    }
    // ---------------------------------------------------------------------------------------------

}
