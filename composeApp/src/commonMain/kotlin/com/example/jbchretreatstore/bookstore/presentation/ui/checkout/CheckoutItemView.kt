package com.example.jbchretreatstore.bookstore.presentation.ui.checkout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.jbchretreatstore.bookstore.domain.model.CheckoutItem
import com.example.jbchretreatstore.bookstore.presentation.viewmodel.BookStoreIntent
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun CheckoutItemView(
    checkoutItem: CheckoutItem,
    onUserIntent: (BookStoreIntent) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = checkoutItem.name,
            style = MaterialTheme.typography.titleMedium,
        )
    }
}

@Preview
@Composable
fun CheckoutItemViewPreview() {
    CheckoutItemView(
        checkoutItem = CheckoutItem(
            name = "T-shirt",
            quantity = 2,
            totalPrice = 15.00,
        ),
    ) {}
}