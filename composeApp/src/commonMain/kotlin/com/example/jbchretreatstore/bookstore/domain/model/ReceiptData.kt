package com.example.jbchretreatstore.bookstore.domain.model

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.uuid.Uuid

data class ReceiptData @OptIn(ExperimentalTime::class) constructor(
    val id: Uuid = Uuid.random(),
    val buyerName: String = "Unknown",
    val checkoutList: List<CheckoutItem> = emptyList(),
    val paymentMethod: PaymentMethod = PaymentMethod.CASH,
    val checkoutStatus: CheckoutStatus = CheckoutStatus.PENDING,
    val dateTime: LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
)