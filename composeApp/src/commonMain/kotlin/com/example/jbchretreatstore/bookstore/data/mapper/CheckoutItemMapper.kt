package com.example.jbchretreatstore.bookstore.data.mapper

import com.example.jbchretreatstore.bookstore.data.model.CheckoutItemDto
import com.example.jbchretreatstore.bookstore.domain.model.CheckoutItem

object CheckoutItemMapper {

    fun toDomain(dto: CheckoutItemDto): CheckoutItem {
        return CheckoutItem(
            id = dto.id,
            itemName = dto.itemName,
            quantity = dto.quantity,
            variantsMap = dto.optionsMap,
            totalPrice = dto.totalPrice
        )
    }

    fun toDto(domain: CheckoutItem): CheckoutItemDto {
        return CheckoutItemDto(
            id = domain.id,
            itemName = domain.itemName,
            quantity = domain.quantity,
            optionsMap = domain.variantsMap,
            totalPrice = domain.totalPrice
        )
    }

    fun toDomainList(dtoList: List<CheckoutItemDto>): List<CheckoutItem> {
        return dtoList.map { toDomain(it) }
    }

    fun toDtoList(domainList: List<CheckoutItem>): List<CheckoutItemDto> {
        return domainList.map { toDto(it) }
    }
}

