package com.example.jbchretreatstore.bookstore.presentation.ui.itemlist

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Shop
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.jbchretreatstore.bookstore.presentation.BookStoreIntent
import com.example.jbchretreatstore.bookstore.presentation.navigation.BookStoreNavDestination
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.BookStoreTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun BottomNavigationBar(
    saveForLaterCount: Int,
    currentDestination: BookStoreNavDestination,
    onUserIntent: (BookStoreIntent) -> Unit
) {
    NavigationBar(windowInsets = NavigationBarDefaults.windowInsets) {
        NavigationBarItem(
            selected = currentDestination == BookStoreNavDestination.CheckoutScreen,
            onClick = {
                onUserIntent.invoke(BookStoreIntent.OnNavigate(destination = BookStoreNavDestination.CheckoutScreen))
            },
            icon = {
                BadgedBox(
                    badge = {
                        if (saveForLaterCount > 0) {
                            Badge(
                                containerColor = Color.Red,
                                contentColor = Color.White
                            ) {
                                Text("$saveForLaterCount")
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.AddShoppingCart,
                        contentDescription = null
                    )
                }
            }
        )
        NavigationBarItem(
            selected = currentDestination == BookStoreNavDestination.ItemListScreen,
            onClick = {
                onUserIntent.invoke(BookStoreIntent.OnNavigate(destination = BookStoreNavDestination.ItemListScreen))
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.Shop,
                    contentDescription = null
                )
            }
        )
        NavigationBarItem(
            selected = currentDestination == BookStoreNavDestination.ReceiptScreen,
            onClick = {
                onUserIntent.invoke(BookStoreIntent.OnNavigate(destination = BookStoreNavDestination.ReceiptScreen))
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.Receipt,
                    contentDescription = null
                )
            }
        )
    }
}

@Preview
@Composable
fun BottomNavigationBarPreview() {
    BookStoreTheme {
        BottomNavigationBar(
            currentDestination = BookStoreNavDestination.ItemListScreen,
            saveForLaterCount = 2
        ) {}
    }
}