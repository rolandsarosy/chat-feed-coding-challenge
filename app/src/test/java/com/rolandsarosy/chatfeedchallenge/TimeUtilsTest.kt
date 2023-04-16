package com.rolandsarosy.chatfeedchallenge

import com.rolandsarosy.chatfeedchallenge.common.utils.TimeUtils
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.*

class TimeUtilsTest {

    companion object {
        // Unix timestamp for: "Sun Apr 16 2023 14:15:13 GMT+0000"
        private const val UNIX_TIMESTAMP = 1681654513L
    }

    @Test
    fun `getTimeOfDay returns correct time string`() {
        val expected = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(UNIX_TIMESTAMP))
        val actual = TimeUtils.getTimeOfDay(UNIX_TIMESTAMP)
        assert(expected == actual)
    }
}
