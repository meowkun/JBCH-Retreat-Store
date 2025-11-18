package com.example.jbchretreatstore.bookstore.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.jbchretreatstore.bookstore.presentation.BookStoreIntent
import com.example.jbchretreatstore.bookstore.presentation.BookStoreViewModel
import com.example.jbchretreatstore.bookstore.presentation.model.AlertDialogType
import com.example.jbchretreatstore.bookstore.presentation.ui.checkout.CheckoutScreen
import com.example.jbchretreatstore.bookstore.presentation.ui.itemlist.AddItemDialog
import com.example.jbchretreatstore.bookstore.presentation.ui.itemlist.BottomNavigationBar
import com.example.jbchretreatstore.bookstore.presentation.ui.itemlist.ItemListScreen
import com.example.jbchretreatstore.bookstore.presentation.ui.purchasehistory.PurchaseHistoryScreen
import com.example.jbchretreatstore.core.presentation.DarkBlue
import org.jetbrains.compose.resources.stringResource

@Composable
fun BookStoreNavHost(viewModel: BookStoreViewModel) {

    val navController = rememberNavController()
    val navigator = remember { BookStoreNavigator(navController) }
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Derive current destination from the NavController
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = when (navBackStackEntry?.destination?.route) {
        BookStoreNavDestination.CheckoutScreen.route -> BookStoreNavDestination.CheckoutScreen
        BookStoreNavDestination.ReceiptScreen.route -> BookStoreNavDestination.ReceiptScreen
        else -> BookStoreNavDestination.ItemListScreen
    }

    // Show snackbar when snackbarMessage is not null
    val snackbarText = state.snackbarMessage?.let { stringResource(it) }
    LaunchedEffect(snackbarText) {
        snackbarText?.let { message ->
            // Show snackbar with auto-dismiss (suspends until dismissed)
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short // 4 seconds
            )
            // Clear message after snackbar is dismissed
            viewModel.onUserIntent(BookStoreIntent.OnSnackbarDismissed, navigator)
        }
    }

    Scaffold(
        containerColor = DarkBlue,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            if (currentDestination == BookStoreNavDestination.ItemListScreen) {
                FloatingActionButton(
                    onClick = {
                        viewModel.onUserIntent(
                            BookStoreIntent.OnUpdateDialogVisibility(
                                AlertDialogType.ADD_ITEM,
                                true
                            ),
                            navigator
                        )
                    },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                }
            }
        },
        bottomBar = {
            if (currentDestination != BookStoreNavDestination.CheckoutScreen) {
                BottomNavigationBar(
                    saveForLaterCount = state.saveForLaterList.size,
                    currentDestination = currentDestination
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
                ItemListScreen(
                    state = state,
                ) { intent ->
                    viewModel.onUserIntent(intent, navigator)
                }
            }
            composable(BookStoreNavDestination.CheckoutScreen.route) {
                CheckoutScreen(
                    state = state,
                ) { intent ->
                    viewModel.onUserIntent(intent, navigator)
                }
            }
            // New destination: Purchase History (ReceiptScreen)
            composable(BookStoreNavDestination.ReceiptScreen.route) {
                PurchaseHistoryScreen(
                    state = state,
                ) { intent ->
                    viewModel.onUserIntent(intent, navigator)
                }
            }
        }
        if (state.displayAddDisplayItemDialog) {
            AddItemDialog { intent ->
                viewModel.onUserIntent(intent, navigator)
            }
        }
    }
}