package com.example.jbchretreatstore.bookstore.data.mapper

import com.example.jbchretreatstore.bookstore.data.model.ReceiptDataDto
import com.example.jbchretreatstore.bookstore.domain.model.CheckoutStatus
import com.example.jbchretreatstore.bookstore.domain.model.PaymentMethod
import com.example.jbchretreatstore.bookstore.domain.model.ReceiptData
import kotlinx.datetime.LocalDateTime

object ReceiptDataMapper {

    fun toDomain(dto: ReceiptDataDto): ReceiptData {
        return ReceiptData(
            id = dto.id,
            buyerName = dto.buyerName,
            checkoutList = CheckoutItemMapper.toDomainList(dto.checkoutList),
            paymentMethod = PaymentMethod.valueOf(dto.paymentMethod),
            checkoutStatus = CheckoutStatus.valueOf(dto.checkoutStatus),
            dateTime = LocalDateTime.parse(dto.dateTime)
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

