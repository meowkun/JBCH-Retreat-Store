package com.example.jbchretreatstore.bookstore.data.repository

import com.example.jbchretreatstore.bookstore.data.datasource.BookStoreLocalDataSource
import com.example.jbchretreatstore.bookstore.data.mapper.DisplayItemMapper
import com.example.jbchretreatstore.bookstore.data.mapper.ReceiptDataMapper
import com.example.jbchretreatstore.bookstore.domain.model.DisplayItem
import com.example.jbchretreatstore.bookstore.domain.model.ReceiptData
import com.example.jbchretreatstore.bookstore.domain.repository.BookStoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class BookStoreRepositoryImpl(
    private val localDataSource: BookStoreLocalDataSource
) : BookStoreRepository {

    override suspend fun updateDisplayItems(items: List<DisplayItem>) {
        val dtoList = DisplayItemMapper.toDtoList(items)
        localDataSource.saveDisplayItems(dtoList)
    }

    override fun fetchDisplayItems(): Flow<List<DisplayItem>> {
        return localDataSource.getDisplayItems().map { dtoList ->
            DisplayItemMapper.toDomainList(dtoList)
        }
    }

    override suspend fun updateReceiptList(items: List<ReceiptData>) {
        val dtoList = ReceiptDataMapper.toDtoList(items)
        localDataSource.saveReceipts(dtoList)
    }

    override fun fetchReceiptList(): Flow<List<ReceiptData>> {
        return localDataSource.getReceipts().map { dtoList ->
            ReceiptDataMapper.toDomainList(dtoList)
        }
    }
}

