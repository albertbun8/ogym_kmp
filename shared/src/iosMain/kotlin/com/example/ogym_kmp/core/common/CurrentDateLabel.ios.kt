package com.example.ogym_kmp.core.common

import platform.Foundation.NSDate
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSLocale
import kotlin.time.Instant

actual fun currentDateLabel(): String {
    val formatter = NSDateFormatter().apply {
        locale = NSLocale(localeIdentifier = "en_US_POSIX")
        dateFormat = "EEEE, MMMM d"
    }
    return formatter.stringFromDate(NSDate())
}

actual fun currentDateIso(): String {
    val formatter = NSDateFormatter().apply {
        locale = NSLocale(localeIdentifier = "en_US_POSIX")
        dateFormat = "yyyy-MM-dd"
    }
    return formatter.stringFromDate(NSDate())
}

actual fun currentInstant(): Instant =
    Instant.fromEpochSeconds(NSDate().timeIntervalSince1970.toLong())
