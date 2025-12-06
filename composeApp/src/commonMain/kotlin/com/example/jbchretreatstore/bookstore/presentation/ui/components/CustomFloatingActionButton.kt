package com.example.jbchretreatstore.bookstore.presentation.ui.components

import androidx.compose.animation.AnimatedVisibility
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
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.BookStoreTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun CustomFloatingActionButton(
    hasItemsInCart: Boolean,
    isOnShopScreen: Boolean,
    isOnCheckoutScreen: Boolean,
    itemCount: Int,
    totalPrice: Double,
    onCheckoutClick: () -> Unit,
    onCheckoutButtonClick: () -> Unit,
    onAddItemClick: () -> Unit,
    modifier: Modifier = Modifier,
    showShareButton: Boolean = false,
    onShareClick: () -> Unit = {}
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Checkout button in center - only visible when there are items in cart on ShopScreen
        AnimatedVisibility(visible = isOnShopScreen && hasItemsInCart) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.weight(1f))
                CheckCartButton(
                    itemCount = itemCount,
                    onClick = onCheckoutClick
                )
                Spacer(modifier = Modifier.weight(1f))
            }
        }

        // CheckoutButton - only visible on CheckoutScreen
        AnimatedVisibility(visible = isOnCheckoutScreen) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.weight(1f))
                CheckoutButton(
                    totalPrice = totalPrice,
                    onClick = onCheckoutButtonClick
                )
                Spacer(modifier = Modifier.weight(1f))
            }
        }

        // Add Item button on the right - only visible on ShopScreen when cart is empty
        AnimatedVisibility(visible = isOnShopScreen && !hasItemsInCart) {
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
        AnimatedVisibility(visible = showShareButton) {
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
                hasItemsInCart = false,
                isOnShopScreen = true,
                isOnCheckoutScreen = false,
                itemCount = 0,
                totalPrice = 0.0,
                onCheckoutClick = {},
                onCheckoutButtonClick = {},
                onAddItemClick = {}
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Preview with Checkout button (items in cart)
            CustomFloatingActionButton(
                hasItemsInCart = true,
                isOnShopScreen = true,
                isOnCheckoutScreen = false,
                itemCount = 5,
                totalPrice = 0.0,
                onCheckoutClick = {},
                onCheckoutButtonClick = {},
                onAddItemClick = {}
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Preview with CheckoutButton (on checkout screen)
            CustomFloatingActionButton(
                hasItemsInCart = true,
                isOnShopScreen = false,
                isOnCheckoutScreen = true,
                itemCount = 5,
                totalPrice = 99.99,
                onCheckoutClick = {},
                onCheckoutButtonClick = {},
                onAddItemClick = {}
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Preview with Share button (on receipt screen with data)
            CustomFloatingActionButton(
                hasItemsInCart = false,
                isOnShopScreen = false,
                isOnCheckoutScreen = false,
                itemCount = 0,
                totalPrice = 0.0,
                showShareButton = true,
                onCheckoutClick = {},
                onCheckoutButtonClick = {},
                onAddItemClick = {},
                onShareClick = {}
            )
        }
    }
}
