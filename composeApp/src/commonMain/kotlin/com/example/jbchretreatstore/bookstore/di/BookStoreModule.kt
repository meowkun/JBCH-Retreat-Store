package com.example.jbchretreatstore.bookstore.di

import com.example.jbchretreatstore.bookstore.createDataStore
import com.example.jbchretreatstore.bookstore.data.datasource.BookStoreLocalDataSource
import com.example.jbchretreatstore.bookstore.data.datasource.BookStoreLocalDataSourceImpl
import com.example.jbchretreatstore.bookstore.data.repository.BookStoreRepositoryImpl
import com.example.jbchretreatstore.bookstore.data.testdata.TestDataLoader
import com.example.jbchretreatstore.bookstore.domain.repository.BookStoreRepository
import com.example.jbchretreatstore.bookstore.domain.usecase.CheckoutUseCase
import com.example.jbchretreatstore.bookstore.domain.usecase.ManageCartUseCase
import com.example.jbchretreatstore.bookstore.domain.usecase.ManageDisplayItemsUseCase
import com.example.jbchretreatstore.bookstore.domain.usecase.PurchaseHistoryUseCase
import com.example.jbchretreatstore.bookstore.presentation.shared.CartStateHolder
import com.example.jbchretreatstore.bookstore.presentation.shared.ShareManager
import com.example.jbchretreatstore.bookstore.presentation.shared.SnackbarManager
import com.example.jbchretreatstore.bookstore.presentation.shared.getShareManager
import com.example.jbchretreatstore.bookstore.presentation.ui.checkout.CheckoutViewModel
import com.example.jbchretreatstore.bookstore.presentation.ui.purchasehistory.PurchaseHistoryViewModel
import com.example.jbchretreatstore.bookstore.presentation.ui.shop.ShopViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val bookStoreModule = module {
    // DataStore
    single { createDataStore() }

    // Share Manager
    single<ShareManager> { getShareManager() }

    // Shared State Holders
    single { CartStateHolder() }
    single { SnackbarManager() }

    // Data Sources
    singleOf(::BookStoreLocalDataSourceImpl) bind BookStoreLocalDataSource::class

    // Repositories
    singleOf(::BookStoreRepositoryImpl) bind BookStoreRepository::class

    // Test Data Loader
    single { TestDataLoader(get()) }

    // Use Cases
    singleOf(::ManageDisplayItemsUseCase)
    singleOf(::ManageCartUseCase)
    singleOf(::CheckoutUseCase)
    singleOf(::PurchaseHistoryUseCase)

    // ViewModels
    viewModelOf(::ShopViewModel)
    viewModelOf(::CheckoutViewModel)
    viewModelOf(::PurchaseHistoryViewModel)
}

