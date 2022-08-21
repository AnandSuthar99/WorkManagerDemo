package com.example.workmanagerdemo.util

import java.text.SimpleDateFormat
import java.util.*

object Utils {
    fun getCurrentTimeFormatted(): String {
        val sdf = SimpleDateFormat("dd-MM-yyyy, HH:mm:ss:SSS", Locale.getDefault())
        return sdf.format(Date())
    }
}