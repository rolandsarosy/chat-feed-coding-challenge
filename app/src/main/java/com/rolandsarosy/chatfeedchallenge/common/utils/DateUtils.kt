package com.rolandsarosy.chatfeedchallenge.common.utils

import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

object DateUtils {

    fun getTimeOfDay(timestamp: Long): String {
        return SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(TimeUnit.SECONDS.toMillis(timestamp)))
    }
}
