package com.example.jbchretreatstore.bookstore.presentation.utils

import com.example.jbchretreatstore.bookstore.domain.model.ReceiptData
import kotlin.math.round

// CSV Constants
private object CsvConstants {
    // Individual column names
    const val BUYER_NAME = "Buyer Name"
    const val PAYMENT_METHOD = "Payment Method"
    const val ITEM_NAME = "Item Name"
    const val QUANTITY = "Quantity"
    const val UNIT_PRICE = "Unit Price"
    const val TOTAL_PRICE = "Total Price"

    // CSV formatting
    const val NEWLINE = "\n"
    const val COMMA = ","
    const val QUOTE = "\""
    const val EMPTY_VALUE = "N/A"
    const val ZERO_DECIMAL = "0.00"
    const val ZERO = "0"
    const val TIME_SEPARATOR = ":"
    const val DATE_SEPARATOR = "/"
    const val DATETIME_SEPARATOR = " "
    const val DECIMAL_PADDING = 2
    const val TIME_PADDING = 2
    const val DATE_PADDING = 2
    const val DECIMAL_PLACES = 100
    const val EMPTY_CELL = ""
    const val GRAND_TOTAL_LABEL = "Grand Total"

    // Flexible header (without Receipt ID, combined Date Time, Payment Method after Item Name)
    const val DATETIME = "Date Time"
    val HEADER_COLUMNS = listOf(
        DATETIME, BUYER_NAME, ITEM_NAME, QUANTITY, UNIT_PRICE, PAYMENT_METHOD, TOTAL_PRICE
    )
    val HEADER: String = HEADER_COLUMNS.joinToString(COMMA) + NEWLINE
}

/**
 * Format a double to 2 decimal places as a string
 */
private fun formatDecimal(value: Double): String {
    val rounded = round(value * CsvConstants.DECIMAL_PLACES) / CsvConstants.DECIMAL_PLACES
    val intPart = rounded.toInt()
    val decimalPart = ((rounded - intPart) * CsvConstants.DECIMAL_PLACES).toInt()
    return "$intPart.${decimalPart.toString().padStart(CsvConstants.DECIMAL_PADDING, '0')}"
}

/**
 * Format date and time in yyyy/MM/dd HH:mm format
 */
/**
 * Format date and time in yyyy/MM/dd HH:mm format
 */
@Suppress("DEPRECATION")
private fun formatDateTime(receipt: ReceiptData): String {
    val year = receipt.dateTime.year
    val month = receipt.dateTime.monthNumber.toString().padStart(CsvConstants.DATE_PADDING, '0')
    val day = receipt.dateTime.dayOfMonth.toString().padStart(CsvConstants.DATE_PADDING, '0')
    val hour = receipt.dateTime.hour.toString().padStart(CsvConstants.TIME_PADDING, '0')
    val minute = receipt.dateTime.minute.toString().padStart(CsvConstants.TIME_PADDING, '0')

    return "$year${CsvConstants.DATE_SEPARATOR}$month${CsvConstants.DATE_SEPARATOR}$day${CsvConstants.DATETIME_SEPARATOR}$hour${CsvConstants.TIME_SEPARATOR}$minute"
}

/**
 * Converts a list of receipt data to CSV format
 */
fun convertReceiptsToCsv(receipts: List<ReceiptData>): String {
    val csvBuilder = StringBuilder()

    // CSV Header
    csvBuilder.append(CsvConstants.HEADER)

    // CSV Rows
    var grandTotal = 0.0
    receipts.forEach { receipt ->
        val dateTime = formatDateTime(receipt)
        val buyerName = receipt.buyerName.ifBlank { CsvConstants.EMPTY_VALUE }
        val paymentMethod = receipt.paymentMethod.name

        if (receipt.checkoutList.isEmpty()) {
            // If no items, still add a row with receipt info
            csvBuilder.append(
                "${CsvConstants.QUOTE}$dateTime${CsvConstants.QUOTE}${CsvConstants.COMMA}" +
                        "${CsvConstants.QUOTE}$buyerName${CsvConstants.QUOTE}${CsvConstants.COMMA}" +
                        "${CsvConstants.QUOTE}${CsvConstants.EMPTY_VALUE}${CsvConstants.QUOTE}${CsvConstants.COMMA}" +
                        "${CsvConstants.ZERO}${CsvConstants.COMMA}" +
                        "${CsvConstants.ZERO_DECIMAL}${CsvConstants.COMMA}" +
                        "${CsvConstants.QUOTE}$paymentMethod${CsvConstants.QUOTE}${CsvConstants.COMMA}" +
                        "${CsvConstants.ZERO_DECIMAL}${CsvConstants.NEWLINE}"
            )
        } else {
            receipt.checkoutList.forEach { item ->
                val itemName = item.itemName
                val quantity = item.quantity
                val unitPrice = formatDecimal(item.totalPrice / item.quantity)
                val totalPrice = formatDecimal(item.totalPrice)

                // Add to grand total
                grandTotal += item.totalPrice

                csvBuilder.append(
                    "${CsvConstants.QUOTE}$dateTime${CsvConstants.QUOTE}${CsvConstants.COMMA}" +
                            "${CsvConstants.QUOTE}$buyerName${CsvConstants.QUOTE}${CsvConstants.COMMA}" +
                            "${CsvConstants.QUOTE}$itemName${CsvConstants.QUOTE}${CsvConstants.COMMA}" +
                            "$quantity${CsvConstants.COMMA}" +
                            "$unitPrice${CsvConstants.COMMA}" +
                            "${CsvConstants.QUOTE}$paymentMethod${CsvConstants.QUOTE}${CsvConstants.COMMA}" +
                            "$totalPrice${CsvConstants.NEWLINE}"
                )
            }
        }
    }

    // Add grand total summary row at the bottom
    if (grandTotal > 0) {
        val emptyColumns = List(CsvConstants.HEADER_COLUMNS.size - 2) { CsvConstants.EMPTY_CELL }
        csvBuilder.append(
            emptyColumns.joinToString(CsvConstants.COMMA) +
                    CsvConstants.COMMA +
                    "${CsvConstants.QUOTE}${CsvConstants.GRAND_TOTAL_LABEL}${CsvConstants.QUOTE}${CsvConstants.COMMA}" +
                    "${CsvConstants.QUOTE}${formatDecimal(grandTotal)}${CsvConstants.QUOTE}${CsvConstants.NEWLINE}"
        )
    }

    return csvBuilder.toString()
}
