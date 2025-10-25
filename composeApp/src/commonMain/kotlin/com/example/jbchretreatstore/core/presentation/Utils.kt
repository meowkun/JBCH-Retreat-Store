package com.example.jbchretreatstore.core.presentation

internal fun Double.toPriceFormatString(): String {
    val rawPrice = this.toString().split(".")
    val priceInteger= rawPrice[0]
    val priceDecimal = when {
        rawPrice.size == 1 -> "00"
        rawPrice[1].length == 1 -> "${rawPrice[1]}0"
        rawPrice[1].length > 2 -> rawPrice[1].substring(0, 2)
        else -> rawPrice[1]
    }
    return "$priceInteger.$priceDecimal"
}