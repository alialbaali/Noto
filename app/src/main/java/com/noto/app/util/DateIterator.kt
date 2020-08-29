package com.noto.app.util


//operator fun LocalDate.rangeTo(other: LocalDate) = DateProgression(this, other)
//
//class DateIterator(val startDate: LocalDate, val endDateInclusive: LocalDate, val stepDays: Long) : Iterator<LocalDate> {
//
//    private var currentDate = startDate
//
//    override fun hasNext() = currentDate <= endDateInclusive
//
//    override fun next(): LocalDate {
//
//        val next = currentDate
//
//        currentDate = currentDate.plusDays(stepDays)
//
//        return next
//    }
//}
//
//class DateProgression(override val start: LocalDate, override val endInclusive: LocalDate, val stepDays: Long = 1) :
//    Iterable<LocalDate>, ClosedRange<LocalDate> {
//
//    override fun iterator(): Iterator<LocalDate> = DateIterator(start, endInclusive, stepDays)
//
//    infix fun step(days: Long) = DateProgression(start, endInclusive, days)
//
//}