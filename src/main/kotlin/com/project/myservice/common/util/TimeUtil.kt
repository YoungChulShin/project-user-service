package com.project.myservice.common.util

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter


/**
 * yyyy-MM-dd HH:mm:ss 포멧
 */
fun Instant.toLocalString() =
    DateTimeFormatter
        .ofPattern("yyyy-MM-dd HH:mm:ss")
        .withZone(ZoneId.systemDefault())
        .format(this)
