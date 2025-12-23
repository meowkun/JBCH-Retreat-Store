package com.example.jbchretreatstore.bookstore.presentation.ui.checkout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.jbchretreatstore.bookstore.domain.model.CheckoutItem
import com.example.jbchretreatstore.bookstore.domain.model.PaymentMethod
import com.example.jbchretreatstore.bookstore.presentation.ui.components.TitleView
import com.example.jbchretreatstore.bookstore.presentation.ui.dialog.CheckoutDialog
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.BookStoreTheme
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.Dimensions
import jbchretreatstore.composeapp.generated.resources.Res
import jbchretreatstore.composeapp.generated.resources.checkout_view_item_title
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun CheckoutScreen(
    viewModel: CheckoutViewModel,
    onNavigateBack: () -> Unit,
    onCheckoutSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val onIntent: (CheckoutIntent) -> Unit = viewModel::handleIntent

    // Navigate back if cart is empty (only on initial load, not after checkout)
    LaunchedEffect(uiState.checkoutItems.isEmpty(), uiState.checkoutSuccess) {
        if (uiState.checkoutItems.isEmpty() && !uiState.checkoutSuccess) {
            onNavigateBack()
        }
    }

    // Handle checkout success navigation
    LaunchedEffect(uiState.checkoutSuccess) {
        if (uiState.checkoutSuccess) {
            onIntent(CheckoutIntent.CheckoutSuccessHandled)
            onCheckoutSuccess()
        }
    }

    CheckoutScreenContent(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onRemoveItem = { onIntent(CheckoutIntent.RemoveFromCart(it)) },
        onPaymentMethodSelected = { onIntent(CheckoutIntent.SelectPaymentMethod(it)) },
        onDismissDialog = { onIntent(CheckoutIntent.ShowCheckoutDialog(false)) },
        onCheckout = { buyerName -> onIntent(CheckoutIntent.ProcessCheckout(buyerName)) }
    )
}

@Composable
private fun CheckoutScreenContent(
    uiState: CheckoutUiState,
    onNavigateBack: () -> Unit,
    onRemoveItem: (CheckoutItem) -> Unit,
    onPaymentMethodSelected: (PaymentMethod) -> Unit,
    onDismissDialog: () -> Unit,
    onCheckout: (String) -> Unit
) {
    Surface(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Title
            TitleView(
                title = stringResource(Res.string.checkout_view_item_title),
                onBackClick = onNavigateBack
            )

            // Checkout items list
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(Dimensions.spacing_m),
                contentPadding = PaddingValues(
                    top = Dimensions.spacing_s,
                    bottom = Dimensions.checkout_button_height + Dimensions.spacing_xxl
                ),
            ) {
                items(
                    items = uiState.checkoutItems,
                    key = { it.uniqueKey }
                ) { item ->
                    CheckoutItemView(
                        checkoutItem = item,
                        onRemoveItem = onRemoveItem
                    )
                }
            }
        }
    }

    // Show checkout dialog
    if (uiState.showCheckoutDialog) {
        CheckoutDialog(
            paymentMethod = uiState.selectedPaymentMethod,
            onPaymentMethodSelected = onPaymentMethodSelected,
            onDismiss = onDismissDialog,
            onCheckout = onCheckout
        )
    }
}

@Preview
@Composable
private fun CheckoutScreenPreview() {
    BookStoreTheme {
        CheckoutScreenContent(
            uiState = CheckoutUiState(
                checkoutItems = listOf(
                    CheckoutItem(
                        itemName = "Holy Bible - NIV",
                        quantity = 2,
                        totalPrice = 91.98,
                        variants = listOf(
                            CheckoutItem.Variant(
                                "Language",
                                listOf("English", "Chinese"),
                                "English"
                            ),
                            CheckoutItem.Variant(
                                "Cover",
                                listOf("Hardcover", "Paperback"),
                                "Hardcover"
                            )
                        )
                    ),
                    CheckoutItem(
                        itemName = "Christian T-Shirt",
                        quantity = 3,
                        totalPrice = 59.97,
                        variants = listOf(
                            CheckoutItem.Variant("Size", listOf("S", "M", "L", "XL"), "L"),
                            CheckoutItem.Variant("Color", listOf("White", "Black", "Navy"), "Navy")
                        )
                    ),
                    CheckoutItem(
                        itemName = "Devotional Journal",
                        quantity = 1,
                        totalPrice = 24.99,
                        variants = emptyList()
                    )
                ),
                totalPrice = 176.94,
                selectedPaymentMethod = PaymentMethod.CASH
            ),
            onNavigateBack = {},
            onRemoveItem = {},
            onPaymentMethodSelected = {},
            onDismissDialog = {},
            onCheckout = {}
        )
    }
}

@Preview
@Composable
private fun CheckoutScreenWithDialogPreview() {
    BookStoreTheme {
        CheckoutScreenContent(
            uiState = CheckoutUiState(
                checkoutItems = listOf(
                    CheckoutItem(
                        itemName = "Holy Bible - NIV",
                        quantity = 1,
                        totalPrice = 45.99,
                        variants = listOf(
                            CheckoutItem.Variant(
                                "Language",
                                listOf("English", "Chinese"),
                                "English"
                            )
                        )
                    )
                ),
                totalPrice = 45.99,
                selectedPaymentMethod = PaymentMethod.ZELLE,
                showCheckoutDialog = true
            ),
            onNavigateBack = {},
            onRemoveItem = {},
            onPaymentMethodSelected = {},
            onDismissDialog = {},
            onCheckout = {}
        )
    }
}

@Preview
@Composable
private fun CheckoutScreenEmptyPreview() {
    BookStoreTheme {
        CheckoutScreenContent(
            uiState = CheckoutUiState(
                checkoutItems = emptyList(),
                totalPrice = 0.0
            ),
            onNavigateBack = {},
            onRemoveItem = {},
            onPaymentMethodSelected = {},
            onDismissDialog = {},
            onCheckout = {}
        )
    }
}

