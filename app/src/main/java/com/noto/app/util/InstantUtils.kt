package com.noto.app.util

import android.content.Context
import android.text.format.DateFormat
import kotlinx.datetime.*
import java.time.format.DateTimeFormatter

fun Instant.format(context: Context): String {
    val timeZone = TimeZone.currentSystemDefault()
    return toLocalDateTime(timeZone)
        .toJavaLocalDateTime()
        .let { localDateTime ->

            val is24HourFormat = DateFormat.is24HourFormat(context)
            val currentDateTime = Clock.System
                .now()
                .toLocalDateTime(timeZone)

            if (localDateTime.year > currentDateTime.year) {

                val format = if (is24HourFormat)
                    "EEE, d MMM yyyy HH:mm"
                else
                    "EEE, d MMM yyyy h:mm a"

                localDateTime.format(DateTimeFormatter.ofPattern(format))
            } else {

                val format = if (is24HourFormat)
                    "EEE, d MMM HH:mm"
                else
                    "EEE, d MMM h:mm a"

                localDateTime.format(DateTimeFormatter.ofPattern(format))
            }
        }
}

fun LocalDate.format(): String {
    val timeZone = TimeZone.currentSystemDefault()
    return toJavaLocalDate()
        .let { localDate ->
            val currentDateTime = Clock.System
                .now()
                .toLocalDateTime(timeZone)
            if (localDate.year > currentDateTime.year) {
                val format = "EEE, d MMM yyyy"
                localDate.format(DateTimeFormatter.ofPattern(format))
            } else {
                val format = "EEE, d MMM"
                localDate.format(DateTimeFormatter.ofPattern(format))
            }
        }
}

fun Instant.toLocalDate() = toLocalDateTime(TimeZone.currentSystemDefault()).date