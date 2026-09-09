package com.example.ogym_kmp.core.common

import kotlin.time.Instant
import kotlin.time.Clock

expect fun currentDateLabel(): String

expect fun currentDateIso(): String

fun currentInstant(): Instant = Clock.System.now()
