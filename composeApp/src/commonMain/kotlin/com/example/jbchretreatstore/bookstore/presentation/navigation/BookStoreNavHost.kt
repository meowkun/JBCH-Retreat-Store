package com.example.jbchretreatstore.bookstore.presentation.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
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
import com.example.jbchretreatstore.bookstore.presentation.shared.SnackbarManager
import com.example.jbchretreatstore.bookstore.presentation.ui.checkout.CheckoutScreen
import com.example.jbchretreatstore.bookstore.presentation.ui.checkout.CheckoutViewModel
import com.example.jbchretreatstore.bookstore.presentation.ui.components.BottomNavigationBar
import com.example.jbchretreatstore.bookstore.presentation.ui.components.CustomFloatingActionButton
import com.example.jbchretreatstore.bookstore.presentation.ui.purchasehistory.PurchaseHistoryScreen
import com.example.jbchretreatstore.bookstore.presentation.ui.purchasehistory.PurchaseHistoryViewModel
import com.example.jbchretreatstore.bookstore.presentation.ui.shop.ShopScreen
import com.example.jbchretreatstore.bookstore.presentation.ui.shop.ShopViewModel
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.Dimensions
import jbchretreatstore.composeapp.generated.resources.Res
import jbchretreatstore.composeapp.generated.resources.app_logo_description
import jbchretreatstore.composeapp.generated.resources.ic_app_logo
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun BookStoreNavHost() {
    val navController = rememberNavController()
    val navigator = remember { BookStoreNavigator(navController) }
    val snackbarHostState = remember { SnackbarHostState() }

    // Inject shared managers
    val snackbarManager: SnackbarManager = koinInject()

    // Inject ViewModels at NavHost level for FAB state
    val shopViewModel: ShopViewModel = koinViewModel()
    val checkoutViewModel: CheckoutViewModel = koinViewModel()
    val purchaseHistoryViewModel: PurchaseHistoryViewModel = koinViewModel()

    val shopState by shopViewModel.uiState.collectAsStateWithLifecycle()
    val checkoutState by checkoutViewModel.uiState.collectAsStateWithLifecycle()

    // Derive current destination from the NavController
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = when (navBackStackEntry?.destination?.route) {
        BookStoreNavDestination.CheckoutScreen.route -> BookStoreNavDestination.CheckoutScreen
        BookStoreNavDestination.ReceiptScreen.route -> BookStoreNavDestination.ReceiptScreen
        else -> BookStoreNavDestination.ShopScreen
    }

    // Show snackbar when snackbarMessage is not null
    val snackbarMessage by snackbarManager.snackbarMessage.collectAsStateWithLifecycle()
    val snackbarText = snackbarMessage?.let { stringResource(it) }
    LaunchedEffect(snackbarText) {
        snackbarText?.let { message ->
            snackbarHostState.showSnackbar(
                message = message,
                withDismissAction = true
            )
            snackbarManager.dismissSnackbar()
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
                modifier = Modifier.padding(horizontal = Dimensions.spacing_m),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Dimensions.spacing_m)
            ) {
                val isOnShopScreen = currentDestination == BookStoreNavDestination.ShopScreen
                val isOnCheckoutScreen =
                    currentDestination == BookStoreNavDestination.CheckoutScreen
                val isOnReceiptScreen = currentDestination == BookStoreNavDestination.ReceiptScreen

                CustomFloatingActionButton(
                    hasItemsInCart = shopState.hasItemsInCart,
                    isOnShopScreen = isOnShopScreen,
                    isOnCheckoutScreen = isOnCheckoutScreen,
                    itemCount = shopState.cartItemCount,
                    totalPrice = checkoutState.totalPrice,
                    showShareButton = isOnReceiptScreen && purchaseHistoryViewModel.hasReceiptData(),
                    onCheckoutClick = {
                        navigator.navigateTo(BookStoreNavDestination.CheckoutScreen)
                    },
                    onCheckoutButtonClick = {
                        checkoutViewModel.showCheckoutDialog(true)
                    },
                    onAddItemClick = {
                        shopViewModel.showAddItemDialog(true)
                    },
                    onShareClick = {
                        purchaseHistoryViewModel.sharePurchaseHistory()
                    }
                )

                AnimatedVisibility(visible = !isOnCheckoutScreen) {
                    BottomNavigationBar(
                        currentDestination = currentDestination,
                        onNavigate = { destination ->
                            navigator.navigateTo(destination)
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
            startDestination = BookStoreNavDestination.ShopScreen.route
        ) {
            composable(BookStoreNavDestination.ShopScreen.route) {
                ShopScreen(
                    viewModel = shopViewModel
                )
            }
            composable(BookStoreNavDestination.CheckoutScreen.route) {
                CheckoutScreen(
                    viewModel = checkoutViewModel,
                    onNavigateBack = {
                        navigator.navigateTo(BookStoreNavDestination.ShopScreen)
                    },
                    onCheckoutSuccess = {
                        navigator.navigateTo(BookStoreNavDestination.ReceiptScreen)
                    }
                )
            }
            composable(BookStoreNavDestination.ReceiptScreen.route) {
                PurchaseHistoryScreen(
                    viewModel = purchaseHistoryViewModel,
                    onNavigateBack = {
                        navigator.navigateTo(BookStoreNavDestination.ShopScreen)
                    }
                )
            }
        }
    }
}