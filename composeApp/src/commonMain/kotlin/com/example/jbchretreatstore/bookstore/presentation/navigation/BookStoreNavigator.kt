package com.example.jbchretreatstore.bookstore.presentation.navigation

import androidx.navigation.NavHostController

class BookStoreNavigator(private val navController: NavHostController) {
    fun navigateTo(destination: BookStoreNavDestination) {
        when (destination) {
            is BookStoreNavDestination.CheckoutScreen -> navController.navigate(BookStoreNavDestination.CheckoutScreen.route)
            is BookStoreNavDestination.ShopScreen -> navController.navigate(BookStoreNavDestination.ShopScreen.route)
            is BookStoreNavDestination.ReceiptScreen -> navController.navigate(BookStoreNavDestination.ReceiptScreen.route)
        }
    }
}