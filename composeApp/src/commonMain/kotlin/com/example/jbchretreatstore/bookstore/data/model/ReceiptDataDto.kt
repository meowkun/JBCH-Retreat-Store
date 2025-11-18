package com.example.jbchretreatstore.bookstore.data.model

import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Serializable
data class ReceiptDataDto @OptIn(ExperimentalUuidApi::class) constructor(
    val id: Uuid = Uuid.random(), // Default for backward compatibility
    val buyerName: String = "", // Default for backward compatibility
    val checkoutList: List<CheckoutItemDto> = emptyList(), // Default for backward compatibility
    val paymentMethod: String = "CASH", // Default for backward compatibility
    val checkoutStatus: String = "PENDING", // Default for backward compatibility
    val dateTime: String = "" // Default for backward compatibility (ISO-8601 format string)
)

