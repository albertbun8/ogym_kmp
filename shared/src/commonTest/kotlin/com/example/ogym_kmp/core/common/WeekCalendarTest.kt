package com.example.ogym_kmp.core.common

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class WeekCalendarTest {
    @Test
    fun weekStartsOnMondayAndMarksToday() {
        val week = weekDatesContaining("2026-09-21")

        assertEquals("2026-09-21", week.first().isoDate)
        assertEquals("2026-09-27", week.last().isoDate)
        assertTrue(week.first().isToday)
    }

    @Test
    fun weekCanCrossMonthAndYearBoundaries() {
        val week = weekDatesContaining("2026-01-01")

        assertEquals("2025-12-29", week.first().isoDate)
        assertEquals("2026-01-04", week.last().isoDate)
        assertEquals("2026-01-01", week.single { it.isToday }.isoDate)
    }

    @Test
    fun leapDayIsHandled() {
        val week = weekDatesContaining("2024-02-29")

        assertEquals("2024-02-26", week.first().isoDate)
        assertEquals("2024-03-03", week.last().isoDate)
    }
}
