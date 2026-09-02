package com.example.ogym_kmp.core.common

import platform.Foundation.NSDate
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSLocale

actual fun currentDateLabel(): String {
    val formatter = NSDateFormatter().apply {
        locale = NSLocale(localeIdentifier = "en_US_POSIX")
        dateFormat = "EEEE, MMMM d"
    }
    return formatter.stringFromDate(NSDate())
}
