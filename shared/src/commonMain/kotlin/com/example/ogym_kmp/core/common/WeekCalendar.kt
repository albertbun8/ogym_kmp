package com.example.ogym_kmp.core.common

data class WeekDate(
    val isoDate: String,
    val shortDayLabel: String,
    val fullDayLabel: String,
    val dayNumber: Int,
    val isToday: Boolean,
)

fun currentWeekDates(): List<WeekDate> = weekDatesContaining(currentDateIso())

internal fun weekDatesContaining(todayIsoDate: String): List<WeekDate> {
    val today = SimpleDate.parse(todayIsoDate)
    val monday = today.plusDays(-today.mondayBasedDayIndex())
    val shortLabels = listOf("M", "T", "W", "T", "F", "S", "S")
    val fullLabels = listOf(
        "Monday",
        "Tuesday",
        "Wednesday",
        "Thursday",
        "Friday",
        "Saturday",
        "Sunday",
    )

    return (0..6).map { index ->
        val date = monday.plusDays(index)
        WeekDate(
            isoDate = date.toIsoString(),
            shortDayLabel = shortLabels[index],
            fullDayLabel = fullLabels[index],
            dayNumber = date.day,
            isToday = date == today,
        )
    }
}

private data class SimpleDate(
    val year: Int,
    val month: Int,
    val day: Int,
) {
    fun mondayBasedDayIndex(): Int {
        val monthOffsets = intArrayOf(0, 3, 2, 5, 0, 3, 5, 1, 4, 6, 2, 4)
        val adjustedYear = if (month < 3) year - 1 else year
        val sundayBasedIndex = (
            adjustedYear + adjustedYear / 4 - adjustedYear / 100 + adjustedYear / 400 +
                monthOffsets[month - 1] + day
            ) % 7
        return (sundayBasedIndex + 6) % 7
    }

    fun plusDays(days: Int): SimpleDate {
        var result = this
        if (days >= 0) {
            repeat(days) { result = result.nextDay() }
        } else {
            repeat(-days) { result = result.previousDay() }
        }
        return result
    }

    fun toIsoString(): String =
        "${year.toString().padStart(4, '0')}-${month.toString().padStart(2, '0')}-${day.toString().padStart(2, '0')}"

    private fun nextDay(): SimpleDate {
        val daysInCurrentMonth = daysInMonth(year, month)
        return when {
            day < daysInCurrentMonth -> copy(day = day + 1)
            month < 12 -> SimpleDate(year, month + 1, 1)
            else -> SimpleDate(year + 1, 1, 1)
        }
    }

    private fun previousDay(): SimpleDate = when {
        day > 1 -> copy(day = day - 1)
        month > 1 -> SimpleDate(year, month - 1, daysInMonth(year, month - 1))
        else -> SimpleDate(year - 1, 12, 31)
    }

    companion object {
        fun parse(isoDate: String): SimpleDate {
            require(isoDate.length == 10) { "Date must use yyyy-MM-dd format." }
            val date = SimpleDate(
                year = isoDate.substring(0, 4).toInt(),
                month = isoDate.substring(5, 7).toInt(),
                day = isoDate.substring(8, 10).toInt(),
            )
            require(date.month in 1..12) { "Month is out of range." }
            require(date.day in 1..daysInMonth(date.year, date.month)) { "Day is out of range." }
            return date
        }
    }
}

private fun daysInMonth(year: Int, month: Int): Int = when (month) {
    1, 3, 5, 7, 8, 10, 12 -> 31
    4, 6, 9, 11 -> 30
    2 -> if (year.isLeapYear()) 29 else 28
    else -> error("Month is out of range.")
}

private fun Int.isLeapYear(): Boolean = this % 400 == 0 || (this % 4 == 0 && this % 100 != 0)
