package com.example.jbchretreatstore.bookstore.presentation.ui.checkout

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import com.example.jbchretreatstore.bookstore.domain.model.CheckoutItem
import com.example.jbchretreatstore.bookstore.presentation.viewmodel.BookStoreIntent

@Composable
fun CheckoutScreen(
    checkoutItemList: List<CheckoutItem>,
    onUserIntent: (BookStoreIntent) -> Unit
) {
    Column {

    }
}