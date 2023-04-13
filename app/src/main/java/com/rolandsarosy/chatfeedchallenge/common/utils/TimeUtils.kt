package com.rolandsarosy.chatfeedchallenge.common.utils

import java.text.SimpleDateFormat
import java.util.*

object TimeUtils {
    fun getTimeOfDay(timestamp: Long): String = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(timestamp))
}
