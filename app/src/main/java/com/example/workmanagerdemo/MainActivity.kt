package com.example.workmanagerdemo

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.work.*
import com.example.workmanagerdemo.util.Constants
import com.example.workmanagerdemo.util.Utils
import com.example.workmanagerdemo.workers.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var tvWorkState: TextView
    private lateinit var tvWorkerStatus: TextView
    private lateinit var workManager: WorkManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        workManager = WorkManager.getInstance(applicationContext)
        tvWorkState = findViewById(R.id.tvWorkState)
        tvWorkerStatus = findViewById(R.id.tvWorkerStatus)

        findViewById<Button>(R.id.btnStartOneTimeWork).setOnClickListener {
            startOneTimeWork()
        }

        findViewById<Button>(R.id.btnStartPeriodicWork).setOnClickListener {
            startPeriodicWork()
        }

        findViewById<Button>(R.id.btnCancelWork).setOnClickListener {
            cancelAllWorkers()
        }
    }

    private fun startPeriodicWork() {
        reset()

        val periodicWorkRequest = PeriodicWorkRequest
            .Builder(WorkerOne::class.java, 15, TimeUnit.MINUTES)
            .addTag(WorkerOne.TAG)
            .build()
        workManager.enqueue(periodicWorkRequest)
        observeWorkerStatus(periodicWorkRequest)
    }

    private fun startOneTimeWork() {
        reset()

        // Set constraints for the request
//        val workConstraints = Constraints.Builder()
//            .setRequiresCharging(true)
//            .setRequiredNetworkType(NetworkType.CONNECTED)
//            .build()

        val inputData = Data.Builder()
            .putString(Constants.INPUT_DATA_KEY, "Passing data to a worker")
            .build()

        val requestOne = OneTimeWorkRequestBuilder<WorkerOne>()
            .addTag(WorkerOne.TAG)
//            .setConstraints(workConstraints)
            .setInputData(inputData)
            .build()
        val requestTwo = OneTimeWorkRequestBuilder<WorkerTwo>()
            .addTag(WorkerTwo.TAG)
            .build()
        val requestThree = OneTimeWorkRequestBuilder<WorkerThree>()
            .addTag(WorkerThree.TAG)
            .build()
        val requestFour = OneTimeWorkRequestBuilder<WorkerFour>()
            .addTag(WorkerFour.TAG)
            .build()
        val requestFive = OneTimeWorkRequestBuilder<WorkerFive>()
            .addTag(WorkerFive.TAG)
            .build()

        val parallelWorkersOneAndTwo = mutableListOf(requestOne, requestTwo)

        workManager.beginWith(parallelWorkersOneAndTwo)
            .then(requestThree)
            .then(requestFour)
            .then(requestFive)
            .enqueue()
        observeWorkerStatus(requestOne)
        observeWorkerStatus(requestTwo)
        observeWorkerStatus(requestThree)
        observeWorkerStatus(requestFour)
        observeWorkerStatus(requestFive)
    }

    private fun reset() {
        cancelAllWorkers()
        "${WorkerOne.TAG} State:\n".also { tvWorkState.text = it }
        tvWorkerStatus.text = ""
    }

    private fun cancelAllWorkers() {
        workManager.cancelAllWorkByTag(WorkerOne.TAG)
        workManager.cancelAllWorkByTag(WorkerTwo.TAG)
        workManager.cancelAllWorkByTag(WorkerThree.TAG)
        workManager.cancelAllWorkByTag(WorkerFour.TAG)
        workManager.cancelAllWorkByTag(WorkerFive.TAG)
    }

    private fun observeWorkerStatus(
        request: WorkRequest
    ) {
        workManager.getWorkInfoByIdLiveData(request.id).observe(this) {
            val workTag = it.tags.first { tag ->
                !tag.contains('.')
            }

            // Show worker state for the WorkerOne only
            if (workTag.equals(WorkerOne.TAG)) {
                tvWorkState.append("${it.state.name} at ${Utils.getCurrentTimeFormatted()}\n")
            }

            // Show Start and End Time for the worker
            if (it.state.isFinished) {
                // In case of a cancelled state the output data will be null.
                val startedAt = it.outputData.getString(Constants.START_TIME_KEY)
                val completedOn = it.outputData.getString(Constants.END_TIME_KEY)
                tvWorkerStatus.append(
                    "$workTag:\n"
                            + "\tStartedAt: $startedAt\n"
                            + "\tCompletedOn: $completedOn\n\n"
                )

            }
        }
    }
}
