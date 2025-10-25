package com.example.jbchretreatstore.bookstore.presentation.ui.itemlist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.jbchretreatstore.bookstore.domain.model.CheckoutItem
import com.example.jbchretreatstore.bookstore.presentation.navigation.BookStoreNavDestination
import com.example.jbchretreatstore.bookstore.presentation.viewmodel.BookStoreIntent
import com.example.jbchretreatstore.core.presentation.UiConstants.spacing_xl
import com.example.jbchretreatstore.core.presentation.toPriceFormatString
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ViewCartIconButton(
    modifier: Modifier,
    cartList: List<CheckoutItem>,
    onUserIntent: (BookStoreIntent) -> Unit
) {
    val totalItems = cartList.sumOf { it.quantity }
    BadgedBox(
        modifier = modifier,
        badge = {
            if (totalItems > 0) {
                Badge(
                    containerColor = Color.Red,
                    contentColor = Color.White
                ) {
                    Text("$totalItems")
                }
            }
        }
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IconButton(
                modifier = Modifier
                    .width(spacing_xl)
                    .height(spacing_xl),
                onClick = {
                    onUserIntent.invoke(BookStoreIntent.OnNavigate(BookStoreNavDestination.Checkout))
                }
            ) {
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = null,
                    tint = Color.White
                )
            }

            if (totalItems > 0) {
                Text(
                    text = "$${cartList.sumOf { it.totalPrice }.toPriceFormatString()}",
                    color = Color.White
                )
            }
        }

    }
}

@Preview
@Composable
fun ViewCartIconButtonPreview() {
    ViewCartIconButton(
        modifier = Modifier,
        cartList = listOf(
            CheckoutItem(
                name = "Bible",
                quantity = 1,
                totalPrice = 40.00,
            ),
            CheckoutItem(
                name = "T-shirt",
                quantity = 2,
                totalPrice = 15.00,
            )
        ),
        onUserIntent = {}
    )
}