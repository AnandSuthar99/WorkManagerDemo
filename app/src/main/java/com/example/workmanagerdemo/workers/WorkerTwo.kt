package com.example.workmanagerdemo.workers

import android.content.Context
import android.util.Log
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.workmanagerdemo.util.Constants
import com.example.workmanagerdemo.util.Utils

class WorkerTwo(context: Context, workParams: WorkerParameters) : Worker(context, workParams) {
    companion object {
        const val TAG = "WorkerTwo"
    }

    override fun doWork(): Result {
        val startTime = Utils.getCurrentTimeFormatted()
        return try {

            for (counter in 10001..20000) {
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

            Result.success(outputData)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }
}