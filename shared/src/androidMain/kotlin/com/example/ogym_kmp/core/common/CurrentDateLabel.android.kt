package com.example.ogym_kmp.core.common

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

actual fun currentDateLabel(): String {
    val formatter = DateTimeFormatter.ofPattern("EEEE, MMMM d", Locale.ENGLISH)
    return LocalDate.now().format(formatter)
}

actual fun currentDateIso(): String = LocalDate.now().toString()
