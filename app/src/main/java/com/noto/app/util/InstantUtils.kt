package com.noto.app.util

import android.content.Context
import android.text.format.DateFormat
import android.text.format.DateUtils
import kotlinx.datetime.*
import java.time.format.DateTimeFormatter

fun Instant.format(context: Context): String {
    val timeZone = TimeZone.currentSystemDefault()
    val localDateTime = this.toLocalDateTime(timeZone)
    val is24HourFormat = DateFormat.is24HourFormat(context)
    val currentDateTime = Clock.System.now().toLocalDateTime(timeZone)
    return if (localDateTime.year == currentDateTime.year) {
        val format = if (is24HourFormat)
            "EEE, d MMM HH:mm"
        else
            "EEE, d MMM h:mm a"
        val formattedDateTime = localDateTime.toJavaLocalDateTime().format(DateTimeFormatter.ofPattern(format))
        val milliseconds = this.toEpochMilliseconds()
        val currentMilliseconds = currentDateTime.toInstant(timeZone).toEpochMilliseconds()
        val timeSpan = DateUtils.getRelativeTimeSpanString(milliseconds, currentMilliseconds, DateUtils.SECOND_IN_MILLIS).toString()
        when {
            localDateTime.dayOfYear == currentDateTime.dayOfYear -> timeSpan
            localDateTime.dayOfYear >= currentDateTime.dayOfYear.minus(6) -> "$timeSpan ($formattedDateTime)"
            else -> formattedDateTime
        }
    } else {
        val format = if (is24HourFormat)
            "EEE, d MMM yyyy HH:mm"
        else
            "EEE, d MMM yyyy h:mm a"

        val formattedDateTime = localDateTime.toJavaLocalDateTime().format(DateTimeFormatter.ofPattern(format))
        formattedDateTime
    }
}

fun LocalDate.format(): String {
    val timeZone = TimeZone.currentSystemDefault()
    val currentDateTime = Clock.System.now().toLocalDateTime(timeZone)
    return if (this.year == currentDateTime.year) {
        val format = "EEE, d MMM"
        val formattedDate = this.toJavaLocalDate().format(DateTimeFormatter.ofPattern(format))
        val milliseconds = atStartOfDayIn(timeZone).toEpochMilliseconds()
        val currentMilliseconds = currentDateTime.toInstant(timeZone).toEpochMilliseconds()
        val timeSpan = DateUtils.getRelativeTimeSpanString(milliseconds, currentMilliseconds, DateUtils.DAY_IN_MILLIS).toString()
        when {
            this.dayOfYear == currentDateTime.dayOfYear -> timeSpan
            this.dayOfYear >= currentDateTime.dayOfYear.minus(6) -> "$timeSpan ($formattedDate)"
            else -> formattedDate
        }
    } else {
        val format = "EEE, d MMM yyyy"
        val formattedDate = this.toJavaLocalDate().format(DateTimeFormatter.ofPattern(format))
        formattedDate
    }
}

fun Instant.toLocalDate() = toLocalDateTime(TimeZone.currentSystemDefault()).date