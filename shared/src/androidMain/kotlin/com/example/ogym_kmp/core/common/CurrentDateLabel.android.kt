package com.example.ogym_kmp.core.common

import java.time.LocalDate

actual fun currentDateIso(): String = LocalDate.now().toString()
