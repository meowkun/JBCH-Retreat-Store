package com.example.jbchretreatstore.bookstore.presentation.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.jbchretreatstore.bookstore.presentation.BookStoreIntent
import com.example.jbchretreatstore.bookstore.presentation.BookStoreViewModel
import com.example.jbchretreatstore.bookstore.presentation.ui.checkout.CheckoutScreen
import com.example.jbchretreatstore.bookstore.presentation.ui.dialog.AddItemDialog
import com.example.jbchretreatstore.bookstore.presentation.ui.itemlist.ItemListScreen
import com.example.jbchretreatstore.bookstore.presentation.ui.purchasehistory.PurchaseHistoryScreen
import com.example.jbchretreatstore.bookstore.presentation.ui.shared.BottomNavigationBar
import com.example.jbchretreatstore.bookstore.presentation.ui.shared.CheckoutButton
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.Dimensions
import jbchretreatstore.composeapp.generated.resources.Res
import jbchretreatstore.composeapp.generated.resources.app_logo_description
import jbchretreatstore.composeapp.generated.resources.ic_app_logo
import org.jetbrains.compose.resources.painterResource
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
        topBar = {
            Image(
                modifier = Modifier.fillMaxWidth()
                    .statusBarsPadding(),
                painter = painterResource(Res.drawable.ic_app_logo),
                contentDescription = stringResource(Res.string.app_logo_description),
                contentScale = ContentScale.Inside,
                alignment = Alignment.Center
            )
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Dimensions.spacing_m)
            ) {
                // Checkout button - only visible when there are items in cart
                if (state.currentCheckoutList.checkoutList.isNotEmpty() &&
                    currentDestination != BookStoreNavDestination.CheckoutScreen
                ) {
                    CheckoutButton(
                        itemCount = state.currentCheckoutList.checkoutList.sumOf { it.quantity },
                        onClick = {
                            viewModel.onUserIntent(
                                BookStoreIntent.OnNavigate(BookStoreNavDestination.CheckoutScreen),
                                navigator
                            )
                        }
                    )
                }

                // Bottom Navigation Bar - visible when not on checkout screen
                if (currentDestination != BookStoreNavDestination.CheckoutScreen) {
                    BottomNavigationBar(
                        currentDestination = currentDestination,
                        onUserIntent = { intent ->
                            viewModel.onUserIntent(intent, navigator)
                        },
                        modifier = Modifier.padding(bottom = Dimensions.spacing_xxl)
                    )
                }
            }
        },
        floatingActionButtonPosition = FabPosition.Center
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