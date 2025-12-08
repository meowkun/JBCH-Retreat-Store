package com.example.jbchretreatstore.bookstore.data.mapper

import com.example.jbchretreatstore.bookstore.data.model.CheckoutItemDto
import com.example.jbchretreatstore.bookstore.domain.model.CheckoutItem
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
object CheckoutItemMapper {

    fun toDomain(dto: CheckoutItemDto): CheckoutItem {
        // Use new variants structure if available, otherwise fall back to legacy optionsMap
        val variants = if (dto.variants.isNotEmpty()) {
            dto.variants.map { variantDto ->
                CheckoutItem.Variant(
                    key = variantDto.key,
                    valueList = variantDto.valueList,
                    selectedValue = variantDto.selectedValue
                )
            }
        } else {
            // Backward compatibility: convert legacy optionsMap to variants
            // Note: valueList will only contain the selected value since we don't have other options
            dto.optionsMap.map { (key, value) ->
                CheckoutItem.Variant(
                    key = key,
                    valueList = listOf(value),
                    selectedValue = value
                )
            }
        }

        return CheckoutItem(
            id = dto.id,
            itemName = dto.itemName,
            quantity = dto.quantity,
            variants = variants,
            totalPrice = dto.totalPrice
        )
    }

    fun toDto(domain: CheckoutItem): CheckoutItemDto {
        return CheckoutItemDto(
            id = domain.id,
            itemName = domain.itemName,
            quantity = domain.quantity,
            optionsMap = domain.variantsMap, // Keep for backward compatibility
            variants = domain.variants.map { variant ->
                CheckoutItemDto.VariantDto(
                    key = variant.key,
                    valueList = variant.valueList,
                    selectedValue = variant.selectedValue
                )
            },
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

