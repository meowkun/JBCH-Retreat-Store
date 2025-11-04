package com.example.jbchretreatstore.bookstore.presentation.navigation

sealed class BookStoreNavDestination(val route: String) {
    data object ItemListScreen : BookStoreNavDestination("ItemListScreen")
    data object CheckoutScreen : BookStoreNavDestination("CheckoutScreen")
    data object SettingScreen : BookStoreNavDestination("SettingScreen")
    data object ReceiptScreen : BookStoreNavDestination("ReceiptScreen")
}