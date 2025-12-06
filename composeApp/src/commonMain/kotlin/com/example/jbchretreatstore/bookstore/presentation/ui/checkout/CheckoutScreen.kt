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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.jbchretreatstore.bookstore.domain.model.CheckoutItem
import com.example.jbchretreatstore.bookstore.domain.model.CheckoutStatus
import com.example.jbchretreatstore.bookstore.domain.model.PaymentMethod.CASH
import com.example.jbchretreatstore.bookstore.domain.model.ReceiptData
import com.example.jbchretreatstore.bookstore.presentation.BookStoreIntent
import com.example.jbchretreatstore.bookstore.presentation.BookStoreViewState
import com.example.jbchretreatstore.bookstore.presentation.navigation.BookStoreNavDestination
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
    state: BookStoreViewState,
    onUserIntent: (BookStoreIntent) -> Unit
) {
    // State management
    val selectedOption = remember { mutableStateOf(CASH) }
    var checkoutStatus by remember { mutableStateOf(CheckoutStatus.CHECKED_OUT) }

    // Navigate back if cart is empty - use LaunchedEffect to prevent composition issues
    LaunchedEffect(state.currentCheckoutList.checkoutList.isEmpty()) {
        if (state.currentCheckoutList.checkoutList.isEmpty()) {
            onUserIntent.invoke(BookStoreIntent.OnNavigate(BookStoreNavDestination.ShopScreen))
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
                onBackClick = {
                    onUserIntent.invoke(BookStoreIntent.OnNavigate(BookStoreNavDestination.ShopScreen))
                }
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
                    items = state.currentCheckoutList.checkoutList,
                    key = { "${it.id}_${it.variantsMap.hashCode()}" }
                ) { item ->
                    CheckoutItemView(
                        checkoutItem = item,
                        onUserIntent = onUserIntent
                    )
                }
            }
        }
    }

    // Show checkout dialog
    if (state.displayCheckoutDialog) {
        CheckoutDialog(
            checkoutStatus = checkoutStatus,
            paymentMethod = selectedOption.value,
            onPaymentMethodSelected = { selectedOption.value = it },
            onUserIntent = onUserIntent
        )
    }
}


@Preview(showBackground = true)
@Composable
fun CheckoutScreenPreview() {
    BookStoreTheme {
        CheckoutScreen(
            state = BookStoreViewState(
                currentCheckoutList = ReceiptData(
                    checkoutList = listOf(
                        CheckoutItem(
                            itemName = "Holy Bible - NIV",
                            totalPrice = 45.99,
                            variantsMap = mapOf("Language" to "English", "Version" to "NIV")
                        ),
                        CheckoutItem(
                            itemName = "Christian T-Shirt - Faith",
                            totalPrice = 24.99,
                            variantsMap = mapOf("Size" to "L", "Color" to "Blue")
                        ),
                        CheckoutItem(
                            itemName = "Devotional Journal",
                            totalPrice = 18.50,
                            variantsMap = mapOf("Cover" to "Leather", "Pages" to "200")
                        ),
                        CheckoutItem(
                            itemName = "Children's Bible Stories",
                            totalPrice = 32.00,
                            variantsMap = mapOf("Age Group" to "5-8 years", "Format" to "Hardcover")
                        ),
                        CheckoutItem(
                            itemName = "Worship CD Collection",
                            totalPrice = 29.99,
                            variantsMap = mapOf("Genre" to "Contemporary", "Artist" to "Various")
                        ),
                        CheckoutItem(
                            itemName = "Prayer Book",
                            totalPrice = 16.75,
                            variantsMap = mapOf("Type" to "Daily Prayers", "Language" to "English")
                        )
                    )
                )
            ),
            onUserIntent = {}
        )
    }
}
