package com.example.jbchretreatstore.bookstore.presentation.checkout

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
import com.example.jbchretreatstore.bookstore.presentation.ui.checkout.CheckoutItemView
import com.example.jbchretreatstore.bookstore.presentation.ui.components.TitleView
import com.example.jbchretreatstore.bookstore.presentation.ui.dialog.CheckoutDialog
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.Dimensions
import jbchretreatstore.composeapp.generated.resources.Res
import jbchretreatstore.composeapp.generated.resources.checkout_view_item_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun CheckoutScreen(
    viewModel: CheckoutViewModel,
    onNavigateBack: () -> Unit,
    onCheckoutSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Navigate back if cart is empty
    LaunchedEffect(viewModel.isCartEmpty()) {
        if (viewModel.isCartEmpty()) {
            onNavigateBack()
        }
    }

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
                    key = { "${it.id}_${it.variantsMap.hashCode()}" }
                ) { item ->
                    CheckoutItemView(
                        checkoutItem = item,
                        onRemoveItem = { viewModel.onRemoveFromCart(it) }
                    )
                }
            }
        }
    }

    // Show checkout dialog
    if (uiState.showCheckoutDialog) {
        CheckoutDialog(
            paymentMethod = uiState.selectedPaymentMethod,
            onPaymentMethodSelected = { viewModel.onPaymentMethodSelected(it) },
            onDismiss = { viewModel.showCheckoutDialog(false) },
            onCheckout = { buyerName ->
                viewModel.processCheckout(buyerName, onCheckoutSuccess)
            }
        )
    }
}

