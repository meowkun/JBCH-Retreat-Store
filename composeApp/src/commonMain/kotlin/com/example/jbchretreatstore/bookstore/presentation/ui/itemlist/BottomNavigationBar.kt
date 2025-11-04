package com.example.jbchretreatstore.bookstore.presentation.ui.itemlist

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shop
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import com.example.jbchretreatstore.bookstore.presentation.navigation.BookStoreNavDestination
import com.example.jbchretreatstore.bookstore.presentation.viewmodel.BookStoreIntent

@Composable
fun BottomNavigationBar(
    currentDestination: BookStoreNavDestination,
    onUserIntent: (BookStoreIntent) -> Unit
) {
    NavigationBar(windowInsets = NavigationBarDefaults.windowInsets) {
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
            selected = currentDestination == BookStoreNavDestination.SettingScreen,
            onClick = {
                onUserIntent.invoke(BookStoreIntent.OnNavigate(destination = BookStoreNavDestination.SettingScreen))
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.Receipt,
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
                    imageVector = Icons.Default.Settings,
                    contentDescription = null
                )
            }
        )
        NavigationBarItem(
            selected = currentDestination == BookStoreNavDestination.CheckoutScreen,
            onClick = {
                onUserIntent.invoke(BookStoreIntent.OnNavigate(destination = BookStoreNavDestination.CheckoutScreen))
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = null
                )
            }
        )
    }
}