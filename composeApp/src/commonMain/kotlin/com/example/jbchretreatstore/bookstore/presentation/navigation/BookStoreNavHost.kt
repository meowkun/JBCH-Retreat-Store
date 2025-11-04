package com.example.jbchretreatstore.bookstore.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.jbchretreatstore.bookstore.presentation.ui.checkout.CheckoutScreen
import com.example.jbchretreatstore.bookstore.presentation.ui.itemlist.AddItemDialog
import com.example.jbchretreatstore.bookstore.presentation.ui.itemlist.BottomNavigationBar
import com.example.jbchretreatstore.bookstore.presentation.ui.itemlist.ItemListScreen
import com.example.jbchretreatstore.bookstore.presentation.viewmodel.BookStoreViewModel
import com.example.jbchretreatstore.core.presentation.DarkBlue
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun BookStoreNavHost(
    viewModel: BookStoreViewModel = koinViewModel(),
) {

    val navController = rememberNavController()
    val navigator = remember { BookStoreNavigator(navController) }
    val state by viewModel.state.collectAsStateWithLifecycle()
    var showDialog by remember { mutableStateOf(false) }
    var currentNavDestination by remember {
        mutableStateOf<BookStoreNavDestination>(
            BookStoreNavDestination.ItemListScreen
        )
    }
    Scaffold(
        containerColor = DarkBlue,
        floatingActionButton = {
            if (currentNavDestination == BookStoreNavDestination.ItemListScreen) {
                FloatingActionButton(
                    onClick = { showDialog = true },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                }
            }
        },
        bottomBar = {
            if (currentNavDestination != BookStoreNavDestination.CheckoutScreen) {
                BottomNavigationBar(
                    currentDestination = currentNavDestination
                ) { intent ->
                    viewModel.onUserIntent(intent, navigator)
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            modifier = Modifier.padding(innerPadding),
            navController = navController,
            startDestination = BookStoreNavDestination.ItemListScreen.route
        ) {
            composable(BookStoreNavDestination.ItemListScreen.route) {
                currentNavDestination = BookStoreNavDestination.ItemListScreen
                ItemListScreen(
                    state = state,
                ) { intent ->
                    viewModel.onUserIntent(intent, navigator)
                }
            }

            composable(BookStoreNavDestination.CheckoutScreen.route) {
                currentNavDestination = BookStoreNavDestination.CheckoutScreen
                CheckoutScreen(
                    state = state,
                ) { intent ->
                    viewModel.onUserIntent(intent, navigator)
                }
            }
        }
        if (showDialog) {
            AddItemDialog(
                onDismiss = { showDialog = false },
                onConfirm = {
                    showDialog = false
                }
            ) { intent ->
                viewModel.onUserIntent(intent, navigator)
            }
        }
    }
}