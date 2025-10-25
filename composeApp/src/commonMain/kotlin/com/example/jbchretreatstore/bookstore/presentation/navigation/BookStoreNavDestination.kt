package com.example.jbchretreatstore.bookstore.presentation.navigation

sealed class BookStoreNavDestination(val route: String) {
    data object ItemList : BookStoreNavDestination("itemList")
    data object Checkout : BookStoreNavDestination("checkout")
}