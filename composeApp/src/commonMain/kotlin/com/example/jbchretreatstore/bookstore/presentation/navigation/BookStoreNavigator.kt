package com.example.jbchretreatstore.bookstore.presentation.navigation

import androidx.navigation.NavHostController

class BookStoreNavigator(private val navController: NavHostController) {
    fun navigateTo(destination: BookStoreNavDestination) {
        navController.navigate(destination.route)
    }
}