package com.example.jbchretreatstore.bookstore.presentation.navigation

import androidx.navigation.NavHostController

class BookStoreNavigator(private val navController: NavHostController) {
    fun navigateTo(destination: BookStoreNavDestination) {
        when (destination) {
            is BookStoreNavDestination.CheckoutScreen -> navController.navigate(BookStoreNavDestination.CheckoutScreen.route)
            is BookStoreNavDestination.ItemListScreen -> navController.navigate(BookStoreNavDestination.ItemListScreen.route)
            is BookStoreNavDestination.ReceiptScreen -> {}
        }
    }
}