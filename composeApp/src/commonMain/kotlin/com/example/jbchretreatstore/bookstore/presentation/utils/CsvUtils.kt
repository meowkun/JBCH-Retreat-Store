package com.example.jbchretreatstore.bookstore.presentation.utils

import com.example.jbchretreatstore.bookstore.domain.model.ReceiptData
import kotlinx.datetime.number
import kotlin.math.round

// CSV Constants
private object CsvConstants {
    // Individual column names
    const val BUYER_NAME = "Buyer Name"
    const val PAYMENT_METHOD = "Payment Method"
    const val ITEM_NAME = "Item Name"
    const val VARIANTS = "Variants"
    const val QUANTITY = "Quantity"
    const val UNIT_PRICE = "Unit Price"
    const val TOTAL_PRICE = "Total Price"
    const val TOTAL_QUANTITY = "Total Quantity"

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
        DATETIME, BUYER_NAME, ITEM_NAME, VARIANTS, QUANTITY, UNIT_PRICE, PAYMENT_METHOD, TOTAL_PRICE
    )
    val HEADER: String = HEADER_COLUMNS.joinToString(COMMA) + NEWLINE

    // Grouped by item name header
    val GROUPED_BY_ITEM_HEADER_COLUMNS = listOf(
        ITEM_NAME, TOTAL_QUANTITY, TOTAL_PRICE
    )
    val GROUPED_BY_ITEM_HEADER: String =
        GROUPED_BY_ITEM_HEADER_COLUMNS.joinToString(COMMA) + NEWLINE
}

/**
 * Data class containing all CSV export formats
 */
data class CsvExportResult(
    val detailedCsv: String,
    val groupedByItemCsv: String,
    val groupedByItemWithVariantsCsv: String,
    val groupedByItemPerVariantCsv: String,
    val combinedCsv: String
)

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
private fun formatDateTime(receipt: ReceiptData): String {
    val year = receipt.dateTime.year
    val month = receipt.dateTime.month.number.toString().padStart(CsvConstants.DATE_PADDING, '0')
    val day = receipt.dateTime.day.toString().padStart(CsvConstants.DATE_PADDING, '0')
    val hour = receipt.dateTime.hour.toString().padStart(CsvConstants.TIME_PADDING, '0')
    val minute = receipt.dateTime.minute.toString().padStart(CsvConstants.TIME_PADDING, '0')

    return "$year${CsvConstants.DATE_SEPARATOR}$month${CsvConstants.DATE_SEPARATOR}$day${CsvConstants.DATETIME_SEPARATOR}$hour${CsvConstants.TIME_SEPARATOR}$minute"
}

/**
 * Format variants map to a readable string (e.g., "Size: Large, Color: Blue")
 */
private fun formatVariants(variantsMap: Map<String, String>): String {
    return if (variantsMap.isEmpty()) {
        CsvConstants.EMPTY_VALUE
    } else {
        variantsMap.entries.joinToString("; ") { (key, value) -> "$key: $value" }
    }
}

/**
 * Converts a list of receipt data to CSV format.
 * Returns detailed CSV, grouped by item CSV, grouped with variants, per variant, and a combined CSV.
 */
fun convertReceiptsToCsv(receipts: List<ReceiptData>): CsvExportResult {
    val detailedCsv = generateDetailedCsv(receipts)
    val groupedByItemCsv = convertReceiptsToGroupedByItemCsv(receipts)
    val groupedByItemWithVariantsCsv = generateGroupedByItemWithVariantsCsv(receipts)
    val groupedByItemPerVariantCsv = generateGroupedByItemPerVariantCsv(receipts)

    // Combine all CSVs into one file with section headers
    val combinedCsv = buildString {
        append("=== DETAILED PURCHASE HISTORY ===${CsvConstants.NEWLINE}")
        append(detailedCsv)
        append(CsvConstants.NEWLINE)
        append(CsvConstants.NEWLINE)
        append("=== SUMMARY BY ITEM ===${CsvConstants.NEWLINE}")
        append(groupedByItemCsv)
        append(CsvConstants.NEWLINE)
        append(CsvConstants.NEWLINE)
        append("=== SUMMARY BY ITEM WITH VARIANTS ===${CsvConstants.NEWLINE}")
        append(groupedByItemWithVariantsCsv)
        append(CsvConstants.NEWLINE)
        append(CsvConstants.NEWLINE)
        append("=== SUMMARY BY ITEM PER VARIANT ===${CsvConstants.NEWLINE}")
        append(groupedByItemPerVariantCsv)
    }

    return CsvExportResult(
        detailedCsv = detailedCsv,
        groupedByItemCsv = groupedByItemCsv,
        groupedByItemWithVariantsCsv = groupedByItemWithVariantsCsv,
        groupedByItemPerVariantCsv = groupedByItemPerVariantCsv,
        combinedCsv = combinedCsv
    )
}

/**
 * Generates detailed CSV with all receipt information
 */
private fun generateDetailedCsv(receipts: List<ReceiptData>): String {
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
                        "${CsvConstants.QUOTE}${CsvConstants.EMPTY_VALUE}${CsvConstants.QUOTE}${CsvConstants.COMMA}" +
                        "${CsvConstants.ZERO}${CsvConstants.COMMA}" +
                        "${CsvConstants.ZERO_DECIMAL}${CsvConstants.COMMA}" +
                        "${CsvConstants.QUOTE}$paymentMethod${CsvConstants.QUOTE}${CsvConstants.COMMA}" +
                        "${CsvConstants.ZERO_DECIMAL}${CsvConstants.NEWLINE}"
            )
        } else {
            receipt.checkoutList.forEach { item ->
                val itemName = item.itemName
                val variants = formatVariants(item.variantsMap)
                val quantity = item.quantity
                val unitPrice = if (quantity > 0) {
                    formatDecimal(item.totalPrice / quantity)
                } else {
                    CsvConstants.ZERO_DECIMAL
                }
                val totalPrice = formatDecimal(item.totalPrice)

                // Add to grand total
                grandTotal += item.totalPrice

                csvBuilder.append(
                    "${CsvConstants.QUOTE}$dateTime${CsvConstants.QUOTE}${CsvConstants.COMMA}" +
                            "${CsvConstants.QUOTE}$buyerName${CsvConstants.QUOTE}${CsvConstants.COMMA}" +
                            "${CsvConstants.QUOTE}$itemName${CsvConstants.QUOTE}${CsvConstants.COMMA}" +
                            "${CsvConstants.QUOTE}$variants${CsvConstants.QUOTE}${CsvConstants.COMMA}" +
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

/**
 * Converts receipts to CSV grouped by item name (summary)
 * Columns: Item Name, Total Quantity, Total Price
 */
private fun convertReceiptsToGroupedByItemCsv(receipts: List<ReceiptData>): String {
    val csvBuilder = StringBuilder()

    // Group items by name and aggregate quantity and price
    val itemSummary =
        mutableMapOf<String, Pair<Int, Double>>() // itemName -> (totalQuantity, totalPrice)

    receipts.forEach { receipt ->
        receipt.checkoutList.forEach { item ->
            val current = itemSummary[item.itemName] ?: Pair(0, 0.0)
            itemSummary[item.itemName] = Pair(
                current.first + item.quantity,
                current.second + item.totalPrice
            )
        }
    }

    // CSV Header
    csvBuilder.append(CsvConstants.GROUPED_BY_ITEM_HEADER)

    // CSV Rows sorted by item name
    var grandTotal = 0.0
    var totalQuantity = 0
    itemSummary.entries.sortedBy { it.key }.forEach { (itemName, summary) ->
        val (quantity, price) = summary
        grandTotal += price
        totalQuantity += quantity

        csvBuilder.append(
            "${CsvConstants.QUOTE}$itemName${CsvConstants.QUOTE}${CsvConstants.COMMA}" +
                    "$quantity${CsvConstants.COMMA}" +
                    "${formatDecimal(price)}${CsvConstants.NEWLINE}"
        )
    }

    // Add grand total row
    if (grandTotal > 0) {
        csvBuilder.append(
            "${CsvConstants.QUOTE}${CsvConstants.GRAND_TOTAL_LABEL}${CsvConstants.QUOTE}${CsvConstants.COMMA}" +
                    "$totalQuantity${CsvConstants.COMMA}" +
                    "${formatDecimal(grandTotal)}${CsvConstants.NEWLINE}"
        )
    }

    return csvBuilder.toString()
}

/**
 * Generates CSV grouped by item name with variant combination
 * Columns: Item Name, Variants, Total Quantity, Total Price
 *
 * Example:
 * Item Name,Variants,Total Quantity,Total Price
 * "The Bible","Language: English",10,199.90
 * "The Bible","Language: Spanish",5,99.95
 * "T-Shirt","Color: Blue; Size: Large",3,75.00
 */
private fun generateGroupedByItemWithVariantsCsv(receipts: List<ReceiptData>): String {
    val csvBuilder = StringBuilder()

    // Group by item name + variant combination
    // Key: "itemName|variantString" -> (totalQuantity, totalPrice)
    val itemVariantSummary = mutableMapOf<String, Triple<String, Int, Double>>()

    receipts.forEach { receipt ->
        receipt.checkoutList.forEach { item ->
            val variantString = formatVariants(item.variantsMap)
            val key = "${item.itemName}|$variantString"

            val current = itemVariantSummary[key]
            if (current != null) {
                itemVariantSummary[key] = Triple(
                    variantString,
                    current.second + item.quantity,
                    current.third + item.totalPrice
                )
            } else {
                itemVariantSummary[key] = Triple(
                    variantString,
                    item.quantity,
                    item.totalPrice
                )
            }
        }
    }

    // CSV Header
    csvBuilder.append("${CsvConstants.ITEM_NAME}${CsvConstants.COMMA}${CsvConstants.VARIANTS}${CsvConstants.COMMA}${CsvConstants.TOTAL_QUANTITY}${CsvConstants.COMMA}${CsvConstants.TOTAL_PRICE}${CsvConstants.NEWLINE}")

    // CSV Rows sorted by item name then variant
    var grandTotal = 0.0
    var totalQuantity = 0
    itemVariantSummary.entries.sortedBy { it.key }.forEach { (key, summary) ->
        val itemName = key.substringBefore("|")
        val (variantString, quantity, price) = summary
        grandTotal += price
        totalQuantity += quantity

        csvBuilder.append(
            "${CsvConstants.QUOTE}$itemName${CsvConstants.QUOTE}${CsvConstants.COMMA}" +
                    "${CsvConstants.QUOTE}$variantString${CsvConstants.QUOTE}${CsvConstants.COMMA}" +
                    "$quantity${CsvConstants.COMMA}" +
                    "${formatDecimal(price)}${CsvConstants.NEWLINE}"
        )
    }

    // Add grand total row
    if (grandTotal > 0) {
        csvBuilder.append(
            "${CsvConstants.QUOTE}${CsvConstants.GRAND_TOTAL_LABEL}${CsvConstants.QUOTE}${CsvConstants.COMMA}" +
                    "${CsvConstants.EMPTY_CELL}${CsvConstants.COMMA}" +
                    "$totalQuantity${CsvConstants.COMMA}" +
                    "${formatDecimal(grandTotal)}${CsvConstants.NEWLINE}"
        )
    }

    return csvBuilder.toString()
}

/**
 * Generates CSV grouped by item name with each variant key as a separate group
 * Each item gets sections for each of its variant keys
 *
 * Example for "T-Shirt" with Color and Size variants:
 * --- T-Shirt (by Color) ---
 * Color,Total Quantity,Total Price
 * "Blue",5,125.00
 * "Red",3,75.00
 * "Grand Total",8,200.00
 *
 * --- T-Shirt (by Size) ---
 * Size,Total Quantity,Total Price
 * "Large",4,100.00
 * "Medium",4,100.00
 * "Grand Total",8,200.00
 *
 * Example for "The Bible" with Language variant:
 * --- The Bible (by Language) ---
 * Language,Total Quantity,Total Price
 * "English",10,199.90
 * "Spanish",5,99.95
 * "Grand Total",15,299.85
 */
private fun generateGroupedByItemPerVariantCsv(receipts: List<ReceiptData>): String {
    val csvBuilder = StringBuilder()

    // First, group all checkout items by item name
    val itemsGroupedByName =
        mutableMapOf<String, MutableList<Triple<Map<String, String>, Int, Double>>>()

    receipts.forEach { receipt ->
        receipt.checkoutList.forEach { item ->
            if (!itemsGroupedByName.containsKey(item.itemName)) {
                itemsGroupedByName[item.itemName] = mutableListOf()
            }
            itemsGroupedByName[item.itemName]?.add(
                Triple(item.variantsMap, item.quantity, item.totalPrice)
            )
        }
    }

    var isFirstSection = true

    // Generate CSV sections for each item
    itemsGroupedByName.keys.sorted().forEach { itemName ->
        val itemData = itemsGroupedByName[itemName] ?: emptyList()

        // Collect all unique variant keys for this item
        val allVariantKeys = itemData
            .flatMap { it.first.keys }
            .distinct()
            .sorted()

        if (allVariantKeys.isEmpty()) {
            // Item has no variants - just show total
            if (!isFirstSection) {
                csvBuilder.append(CsvConstants.NEWLINE)
            }
            isFirstSection = false

            csvBuilder.append("--- $itemName ---${CsvConstants.NEWLINE}")
            csvBuilder.append("${CsvConstants.TOTAL_QUANTITY}${CsvConstants.COMMA}${CsvConstants.TOTAL_PRICE}${CsvConstants.NEWLINE}")

            var totalQty = 0
            var totalPrice = 0.0
            itemData.forEach { (_, quantity, price) ->
                totalQty += quantity
                totalPrice += price
            }
            csvBuilder.append("$totalQty${CsvConstants.COMMA}${formatDecimal(totalPrice)}${CsvConstants.NEWLINE}")
        } else {
            // Generate a separate section for each variant key
            allVariantKeys.forEach { variantKey ->
                if (!isFirstSection) {
                    csvBuilder.append(CsvConstants.NEWLINE)
                }
                isFirstSection = false

                // Section header with variant key name
                csvBuilder.append("--- $itemName (by $variantKey) ---${CsvConstants.NEWLINE}")

                // Header: variant key + Total Quantity + Total Price
                csvBuilder.append("$variantKey${CsvConstants.COMMA}${CsvConstants.TOTAL_QUANTITY}${CsvConstants.COMMA}${CsvConstants.TOTAL_PRICE}${CsvConstants.NEWLINE}")

                // Group by this variant's value
                val variantValueSummary = mutableMapOf<String, Pair<Int, Double>>()

                itemData.forEach { (variantsMap, quantity, price) ->
                    val variantValue = variantsMap[variantKey] ?: CsvConstants.EMPTY_VALUE
                    val current = variantValueSummary[variantValue] ?: Pair(0, 0.0)
                    variantValueSummary[variantValue] = Pair(
                        current.first + quantity,
                        current.second + price
                    )
                }

                // Data rows sorted by variant value
                var sectionTotalQty = 0
                var sectionTotalPrice = 0.0

                variantValueSummary.entries.sortedBy { it.key }.forEach { (variantValue, summary) ->
                    val (quantity, price) = summary
                    sectionTotalQty += quantity
                    sectionTotalPrice += price

                    csvBuilder.append(
                        "${CsvConstants.QUOTE}$variantValue${CsvConstants.QUOTE}${CsvConstants.COMMA}" +
                                "$quantity${CsvConstants.COMMA}" +
                                "${formatDecimal(price)}${CsvConstants.NEWLINE}"
                    )
                }

                // Section grand total
                csvBuilder.append(
                    "${CsvConstants.QUOTE}${CsvConstants.GRAND_TOTAL_LABEL}${CsvConstants.QUOTE}${CsvConstants.COMMA}" +
                            "$sectionTotalQty${CsvConstants.COMMA}" +
                            "${formatDecimal(sectionTotalPrice)}${CsvConstants.NEWLINE}"
                )
            }
        }
    }

    return csvBuilder.toString()
}
