package com.noto.app.util

import android.content.Context
import android.text.format.DateFormat
import android.text.format.DateUtils
import com.noto.app.R
import kotlinx.datetime.*
import java.time.format.DateTimeFormatter

fun Instant.format(context: Context): String {
    val timeZone = TimeZone.currentSystemDefault()
    val localDate = this.toLocalDate()
    val localDateTime = this.toLocalDateTime(timeZone)
    val is24HourFormat = DateFormat.is24HourFormat(context)
    val currentInstant = Clock.System.now()
    val currentDateTime = currentInstant.toLocalDateTime(timeZone)
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
            this.isNow -> context.stringResource(R.string.just_now)
            localDate.isToday -> timeSpan
            localDate.isThisWeek -> "$timeSpan ($formattedDateTime)"
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

fun LocalDate.format(lowercaseTimeSpan: Boolean = false): String {
    val timeZone = TimeZone.currentSystemDefault()
    val currentInstant = Clock.System.now()
    val currentDateTime = currentInstant.toLocalDateTime(timeZone)
    return if (this.year == currentDateTime.year) {
        val format = "EEE, d MMM"
        val formattedDate = this.toJavaLocalDate().format(DateTimeFormatter.ofPattern(format))
        val milliseconds = atStartOfDayIn(timeZone).toEpochMilliseconds()
        val currentMilliseconds = currentDateTime.toInstant(timeZone).toEpochMilliseconds()
        val timeSpan = DateUtils.getRelativeTimeSpanString(milliseconds, currentMilliseconds, DateUtils.DAY_IN_MILLIS).toString()
            .let { if (lowercaseTimeSpan) it.lowercase() else it }
        when {
            this.isToday -> timeSpan
            this.isThisWeek -> "$timeSpan ($formattedDate)"
            else -> formattedDate
        }
    } else {
        val format = "EEE, d MMM yyyy"
        val formattedDate = this.toJavaLocalDate().format(DateTimeFormatter.ofPattern(format))
        formattedDate
    }
}

fun LocalTime.format(is24HourFormat: Boolean): String {
    val format = if (is24HourFormat) "HH:mm" else "h:mm a"
    val pattern = DateTimeFormatter.ofPattern(format)
    return toJavaLocalTime().format(pattern)
}

fun Instant.toLocalDate() = toLocalDateTime(TimeZone.currentSystemDefault()).date
fun Instant.toLocalTime() = toLocalDateTime(TimeZone.currentSystemDefault()).time

private val LocalDate.isToday: Boolean
    get() {
        val currentDate = Clock.System.now().toLocalDate()
        return this.dayOfYear == currentDate.dayOfYear
    }

private val LocalDate.isThisWeek: Boolean
    get() {
        val currentDate = Clock.System.now().toLocalDate()
        return this.dayOfYear >= currentDate.dayOfYear.minus(6)
    }

private val Instant.isNow: Boolean
    get() {
        val currentInstant = Clock.System.now()
        val secondsUntilNow = this.until(currentInstant, DateTimeUnit.SECOND)
        return secondsUntilNow in 0..60
    }