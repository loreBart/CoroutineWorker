package com.android.crazy.worker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.android.crazy.worker.util.log.d
import com.android.crazy.worker.worker.test.WorkerTest

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        d("==========================================")
        val workerTester = WorkerTest(this)
        workerTester.test()
        d("--------------------------------------------")
    }
}
