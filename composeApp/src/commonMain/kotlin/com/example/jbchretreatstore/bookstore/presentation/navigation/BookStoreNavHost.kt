package com.example.jbchretreatstore.bookstore.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.jbchretreatstore.bookstore.presentation.ui.checkout.CheckoutScreen
import com.example.jbchretreatstore.bookstore.presentation.ui.itemlist.ItemListScreen
import com.example.jbchretreatstore.bookstore.presentation.viewmodel.BookStoreViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun BookStoreNavHost(
    viewModel: BookStoreViewModel = koinViewModel(),
) {
    val navController = rememberNavController()
    val navigator = remember { BookStoreNavigator(navController) }

    val state by viewModel.state.collectAsStateWithLifecycle()
    NavHost(
        navController = navController,
        startDestination = BookStoreNavDestination.ItemList.route
    ) {
        composable(BookStoreNavDestination.ItemList.route) {
            ItemListScreen(
                state = state,
                onUserIntent = { intent ->
                    viewModel.onUserIntent(intent, navigator)
                }
            )
        }

        composable(BookStoreNavDestination.Checkout.route) {
            CheckoutScreen(
                checkoutItemList = state.cartList,
                onUserIntent = { action ->
                }
            )
        }
    }

}