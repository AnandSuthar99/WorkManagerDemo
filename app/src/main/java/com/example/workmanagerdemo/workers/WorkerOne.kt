package com.example.workmanagerdemo.workers

import android.content.Context
import android.util.Log
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.workmanagerdemo.util.Constants
import com.example.workmanagerdemo.util.Utils

class WorkerOne(context: Context, workParams: WorkerParameters) : Worker(context, workParams) {

    companion object {
        const val TAG = "WorkerOne"
    }

    override fun doWork(): Result {
        val startTime = Utils.getCurrentTimeFormatted()
        return try {
            val inputString = inputData.getString(Constants.INPUT_DATA_KEY)
            Log.d(TAG, "Input String is: $inputString")

            for (counter in 0..10000) {
                if (isStopped) {
                    Log.d(TAG, "$TAG Stopped before: $counter")
                    break
                }
                Log.d(TAG, "$TAG: $counter")
            }

            val endTime = Utils.getCurrentTimeFormatted()
            val outputData = Data.Builder()
                .putString(Constants.START_TIME_KEY, startTime)
                .putString(Constants.END_TIME_KEY, endTime)
                .build()

            Log.e(TAG, "Completed on time: $endTime")
            Result.success(outputData)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }
}