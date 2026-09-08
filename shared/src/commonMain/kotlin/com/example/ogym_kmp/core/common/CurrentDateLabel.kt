package com.example.ogym_kmp.core.common

import kotlin.time.Instant

expect fun currentDateLabel(): String

expect fun currentDateIso(): String

expect fun currentInstant(): Instant
