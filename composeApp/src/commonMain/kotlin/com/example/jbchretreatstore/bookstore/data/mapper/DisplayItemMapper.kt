package com.example.jbchretreatstore.bookstore.data.mapper

import com.example.jbchretreatstore.bookstore.data.model.DisplayItemDto
import com.example.jbchretreatstore.bookstore.domain.model.DisplayItem

object DisplayItemMapper {

    fun toDomain(dto: DisplayItemDto): DisplayItem {
        return DisplayItem(
            id = dto.id,
            name = dto.name,
            price = dto.price,
            options = dto.options.map { optionDto ->
                DisplayItem.Option(
                    optionKey = optionDto.optionKey,
                    optionValueList = optionDto.optionValueList
                )
            },
            isInCart = dto.isInCart
        )
    }

    fun toDto(domain: DisplayItem): DisplayItemDto {
        return DisplayItemDto(
            id = domain.id,
            name = domain.name,
            price = domain.price,
            options = domain.options.map { option ->
                DisplayItemDto.OptionDto(
                    optionKey = option.optionKey,
                    optionValueList = option.optionValueList
                )
            },
            isInCart = domain.isInCart
        )
    }

    fun toDomainList(dtoList: List<DisplayItemDto>): List<DisplayItem> {
        return dtoList.map { toDomain(it) }
    }

    fun toDtoList(domainList: List<DisplayItem>): List<DisplayItemDto> {
        return domainList.map { toDto(it) }
    }
}


