package com.example.jbchretreatstore.bookstore.data.mapper

import com.example.jbchretreatstore.bookstore.data.model.ReceiptDataDto
import com.example.jbchretreatstore.bookstore.domain.model.CheckoutStatus
import com.example.jbchretreatstore.bookstore.domain.model.PaymentMethod
import com.example.jbchretreatstore.bookstore.domain.model.ReceiptData
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

object ReceiptDataMapper {

    @OptIn(ExperimentalTime::class)
    fun toDomain(dto: ReceiptDataDto): ReceiptData {
        // Safely parse enums with fallback to defaults for backward compatibility
        val paymentMethod = try {
            PaymentMethod.valueOf(dto.paymentMethod)
        } catch (e: IllegalArgumentException) {
            PaymentMethod.CASH
        }

        val checkoutStatus = try {
            CheckoutStatus.valueOf(dto.checkoutStatus)
        } catch (e: IllegalArgumentException) {
            CheckoutStatus.PENDING
        }

        // Safely parse datetime with fallback to current time
        val dateTime = try {
            LocalDateTime.parse(dto.dateTime)
        } catch (e: Exception) {
            Clock.System.now()
                .toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault())
        }

        return ReceiptData(
            id = dto.id,
            buyerName = dto.buyerName,
            checkoutList = CheckoutItemMapper.toDomainList(dto.checkoutList),
            paymentMethod = paymentMethod,
            checkoutStatus = checkoutStatus,
            dateTime = dateTime
        )
    }

    fun toDto(domain: ReceiptData): ReceiptDataDto {
        return ReceiptDataDto(
            id = domain.id,
            buyerName = domain.buyerName,
            checkoutList = CheckoutItemMapper.toDtoList(domain.checkoutList),
            paymentMethod = domain.paymentMethod.name,
            checkoutStatus = domain.checkoutStatus.name,
            dateTime = domain.dateTime.toString()
        )
    }

    fun toDomainList(dtoList: List<ReceiptDataDto>): List<ReceiptData> {
        return dtoList.map { toDomain(it) }
    }

    fun toDtoList(domainList: List<ReceiptData>): List<ReceiptDataDto> {
        return domainList.map { toDto(it) }
    }
}

