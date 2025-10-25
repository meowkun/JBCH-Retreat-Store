package com.example.jbchretreatstore.bookstore.presentation.navigation

import androidx.navigation.NavHostController

class BookStoreNavigator(private val navController: NavHostController) {
    fun navigateTo(destination: BookStoreNavDestination) {
        when (destination) {
            is BookStoreNavDestination.Checkout -> navController.navigate(BookStoreNavDestination.Checkout.route)
            is BookStoreNavDestination.ItemList -> navController.navigate(BookStoreNavDestination.ItemList.route)
        }
    }
}