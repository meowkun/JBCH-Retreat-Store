package com.example.jbchretreatstore.bookstore.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.jbchretreatstore.bookstore.presentation.navigation.BookStoreNavDestination
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.BookStoreTheme
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.Dimensions
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun CustomFloatingActionButton(
    state: CustomFabState,
    onCheckoutClick: () -> Unit,
    onCheckoutButtonClick: () -> Unit,
    onAddItemClick: () -> Unit,
    modifier: Modifier = Modifier,
    onShareClick: () -> Unit = {}
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Checkout button in center - only visible when there are items in cart on ShopScreen
        if (state.showCheckCartButton) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.weight(1f))
                CheckCartButton(
                    itemCount = state.itemCount,
                    onClick = onCheckoutClick
                )
                Spacer(modifier = Modifier.weight(1f))
            }
        }

        // CheckoutButton - only visible on CheckoutScreen
        if (state.showCheckoutButton) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = Dimensions.spacing_xxxxl),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.weight(1f))
                CheckoutButton(
                    totalPrice = state.totalPrice,
                    onClick = onCheckoutButtonClick
                )
                Spacer(modifier = Modifier.weight(1f))
            }
        }

        // Add Item button on the right - only visible on ShopScreen when cart is empty
        if (state.showAddItemButton) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.weight(1f))
                AddDisplayItemButton(
                    onClick = onAddItemClick
                )
            }
        }

        // Share button on the right - only visible on Receipt screen
        if (state.showShareButton) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.weight(1f))
                ShareButton(
                    onClick = onShareClick
                )
            }
        }
    }
}

@Preview
@Composable
fun CustomFloatingActionButtonPreview() {
    BookStoreTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Preview with Add Item button (empty cart, on shop screen)
            CustomFloatingActionButton(
                state = CustomFabState(
                    currentScreen = BookStoreNavDestination.ShopScreen,
                    hasItemsInCart = false
                ),
                onCheckoutClick = {},
                onCheckoutButtonClick = {},
                onAddItemClick = {}
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Preview with Checkout button (items in cart)
            CustomFloatingActionButton(
                state = CustomFabState(
                    currentScreen = BookStoreNavDestination.ShopScreen,
                    hasItemsInCart = true,
                    itemCount = 5
                ),
                onCheckoutClick = {},
                onCheckoutButtonClick = {},
                onAddItemClick = {}
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Preview with CheckoutButton (on checkout screen)
            CustomFloatingActionButton(
                state = CustomFabState(
                    currentScreen = BookStoreNavDestination.CheckoutScreen,
                    hasItemsInCart = true,
                    itemCount = 5,
                    totalPrice = 99.99
                ),
                onCheckoutClick = {},
                onCheckoutButtonClick = {},
                onAddItemClick = {}
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Preview with Share button (on receipt screen with data)
            CustomFloatingActionButton(
                state = CustomFabState(
                    currentScreen = BookStoreNavDestination.ReceiptScreen,
                    hasReceiptData = true
                ),
                onCheckoutClick = {},
                onCheckoutButtonClick = {},
                onAddItemClick = {},
                onShareClick = {}
            )
        }
    }
}
