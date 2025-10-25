package com.example.jbchretreatstore.bookstore.presentation.viewmodel

import com.example.jbchretreatstore.bookstore.domain.model.CheckoutItem
import com.example.jbchretreatstore.bookstore.domain.model.DisplayItem
import com.example.jbchretreatstore.bookstore.presentation.navigation.BookStoreNavDestination

sealed interface BookStoreIntent {
    data class OnSearchQueryChange(val query: String) : BookStoreIntent
    data class OnAddNewItem(val newItem: DisplayItem) : BookStoreIntent
    data class OnRemoveItem(val displayItem: DisplayItem) : BookStoreIntent
    data class OnAddToCart(val checkoutItem: CheckoutItem) : BookStoreIntent
    data class OnNavigate(val destination: BookStoreNavDestination) : BookStoreIntent
}