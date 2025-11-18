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
import com.example.jbchretreatstore.bookstore.presentation.BookStoreIntent
import com.example.jbchretreatstore.bookstore.presentation.navigation.BookStoreNavDestination
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.BookStoreTheme
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.Dimensions
import com.example.jbchretreatstore.bookstore.presentation.utils.toPriceFormatString
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ViewShoppingCartIconButton(
    modifier: Modifier,
    checkoutList: List<CheckoutItem>,
    onUserIntent: (BookStoreIntent) -> Unit
) {
    val totalItems = checkoutList.sumOf { it.quantity }
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
                    .width(Dimensions.spacing_xl)
                    .height(Dimensions.spacing_xl),
                onClick = {
                    onUserIntent.invoke(BookStoreIntent.OnNavigate(BookStoreNavDestination.CheckoutScreen))
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
                    text = "$${checkoutList.sumOf { it.totalPrice }.toPriceFormatString()}",
                    color = Color.White
                )
            }
        }
    }
}

@Preview
@Composable
fun ViewCartIconButtonPreview() {
    BookStoreTheme {
        ViewShoppingCartIconButton(
            modifier = Modifier,
            checkoutList = listOf(
            CheckoutItem(
                itemName = "Bible",
                quantity = 1,
                totalPrice = 40.00,
            ),
            CheckoutItem(
                itemName = "T-shirt",
                quantity = 2,
                totalPrice = 15.00,
            )
        ),
            onUserIntent = {}
        )
    }
}