package com.example.jbchretreatstore.bookstore.presentation.utils

import kotlinx.datetime.LocalDateTime

/**
 * Formats a Double price value to a string with 2 decimal places.
 * Example: 12.5 -> "12.50", 12 -> "12.00", 12.999 -> "12.99"
 */
fun Double.toCurrency(): String {
    val rawPrice = this.toString().split(".")
    val priceInteger = rawPrice[0]
    val priceDecimal = when {
        rawPrice.size == 1 -> "00"
        rawPrice[1].length == 1 -> "${rawPrice[1]}0"
        rawPrice[1].length > 2 -> rawPrice[1].substring(0, 2)
        else -> rawPrice[1]
    }
    return "$$priceInteger.$priceDecimal"
}

/**
 * Formats a LocalDateTime to a readable date string.
 * Format: MM/DD/YYYY HH:mm
 */
fun LocalDateTime.toFormattedDateString(): String {
    val month = month.ordinal.plus(1).toString().padStart(2, '0')
    val day = this.day.toString().padStart(2, '0')
    val hour = hour.toString().padStart(2, '0')
    val minute = minute.toString().padStart(2, '0')
    return "$month/$day/$year $hour:$minute"
}

