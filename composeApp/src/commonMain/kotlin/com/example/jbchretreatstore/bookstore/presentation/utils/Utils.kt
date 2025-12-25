package com.example.jbchretreatstore.bookstore.presentation.utils

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.number

/**
 * Formats a Double price value to a string with 2 decimal places.
 * Example: 12.5 -> "12.50", 12 -> "12.00", 12.999 -> "13.00"
 * Uses proper rounding to handle floating point precision issues.
 */
fun Double.toCurrency(): String {
    // Round to 2 decimal places to handle floating point precision
    val rounded = kotlin.math.round(this * 100) / 100
    val intPart = rounded.toLong()
    val decimalPart = kotlin.math.abs(((rounded - intPart) * 100).toInt())
    return "$${intPart}.${decimalPart.toString().padStart(2, '0')}"
}

/**
 * Formats a LocalDate to a readable date string.
 * Format: MM/DD/YYYY
 */
fun LocalDate.toFormattedDateString(): String {
    val month = month.number.toString().padStart(2, '0')
    val day = day.toString().padStart(2, '0')
    return "$month/$day/$year"
}

/**
 * Formats a LocalDateTime to a readable date string.
 * Format: MM/DD/YYYY HH:mm
 */
fun LocalDateTime.toFormattedDateString(): String {
    val month = this.month.number.toString().padStart(2, '0')
    val day = this.dayOfMonth.toString().padStart(2, '0')
    val hour = this.hour.toString().padStart(2, '0')
    val minute = this.minute.toString().padStart(2, '0')
    return "$month/$day/$year $hour:$minute"
}

/**
 * Filters a string to only allow numeric input with max 2 decimal places.
 * Ensures only one decimal point is allowed and limits decimal places to 2.
 *
 * @return Filtered string containing only digits and at most one decimal point with max 2 decimal places, or null if invalid
 */
fun String.filterNumericInputWithMaxDecimals(maxDecimals: Int = 2): String? {
    // Only allow numbers and decimal point
    val filtered = this.filter { char -> char.isDigit() || char == '.' }

    // Ensure only one decimal point
    if (filtered.count { char -> char == '.' } > 1) {
        return null
    }

    // Check decimal places
    val decimalIndex = filtered.indexOf('.')
    if (decimalIndex != -1 && filtered.length - decimalIndex - 1 > maxDecimals) {
        return null
    }

    return filtered
}

