package com.example.jbchretreatstore.bookstore.presentation.viewmodel

import com.example.jbchretreatstore.bookstore.domain.model.AlertDialogType
import com.example.jbchretreatstore.bookstore.domain.model.CheckoutItem
import com.example.jbchretreatstore.bookstore.domain.model.CheckoutStatus
import com.example.jbchretreatstore.bookstore.domain.model.DisplayItem
import com.example.jbchretreatstore.bookstore.presentation.navigation.BookStoreNavDestination

sealed interface BookStoreIntent {
    data class OnSearchQueryChange(val query: String) : BookStoreIntent
    data class OnAddDisplayItem(val newItem: DisplayItem) : BookStoreIntent
    data class OnRemoveDisplayItem(val displayItem: DisplayItem) : BookStoreIntent
    data class OnAddToCheckoutItem(val checkoutItem: CheckoutItem) : BookStoreIntent
    data class OnCheckout(val buyerName: String, val checkoutStatus: CheckoutStatus) : BookStoreIntent
    data class OnRemoveFromCheckoutItem(val checkoutItem: CheckoutItem) : BookStoreIntent
    data class OnNavigate(val destination: BookStoreNavDestination) : BookStoreIntent
    data class OnUpdateDialogVisibility(val alertDialogType: AlertDialogType, val isVisible: Boolean) : BookStoreIntent
}