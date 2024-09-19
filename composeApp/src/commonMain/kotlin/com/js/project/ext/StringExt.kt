package com.js.project.ext

import kotlinx.datetime.Instant
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

fun String.parseDateTime(): Instant? {
    val patterns = listOf(
        "yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX",
        "yyyy-MM-dd'T'HH:mm:ss.SSSSSXXX",
        "yyyy-MM-dd'T'HH:mm:ss.SSSSXXX",
        "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
        "yyyy-MM-dd'T'HH:mm:ss.SSXXX",
        "yyyy-MM-dd'T'HH:mm:ss.SXXX",
        "yyyy-MM-dd'T'HH:mm:ssXXX"
    )

    for (pattern in patterns) {
        val formatter = DateTimeFormatter.ofPattern(pattern)
        try {
            val offsetDateTime = OffsetDateTime.parse(this, formatter)
            return Instant.fromEpochSeconds(offsetDateTime.toEpochSecond(), offsetDateTime.nano.toLong())
        } catch (e: DateTimeParseException) {
            // Continue to the next pattern
        }
    }

    println("Failed to parse date time: $this")
    return null
}