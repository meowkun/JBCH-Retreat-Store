package com.example.jbchretreatstore.bookstore.presentation.ui.checkout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import com.example.jbchretreatstore.bookstore.domain.model.CheckoutItem
import com.example.jbchretreatstore.bookstore.domain.model.CheckoutStatus
import com.example.jbchretreatstore.bookstore.domain.model.PaymentMethod
import com.example.jbchretreatstore.bookstore.domain.model.PaymentMethod.CASH
import com.example.jbchretreatstore.bookstore.domain.model.PaymentMethod.VENMO
import com.example.jbchretreatstore.bookstore.domain.model.PaymentMethod.ZELLE
import com.example.jbchretreatstore.bookstore.domain.model.ReceiptData
import com.example.jbchretreatstore.bookstore.presentation.BookStoreIntent
import com.example.jbchretreatstore.bookstore.presentation.BookStoreViewState
import com.example.jbchretreatstore.bookstore.presentation.model.AlertDialogType
import com.example.jbchretreatstore.bookstore.presentation.navigation.BookStoreNavDestination
import com.example.jbchretreatstore.bookstore.presentation.ui.components.TitleView
import com.example.jbchretreatstore.bookstore.presentation.ui.dialog.CheckoutDialog
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.BookStoreTheme
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.Dimensions
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.LightBlue
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.Shapes
import com.example.jbchretreatstore.bookstore.presentation.utils.toCurrency
import jbchretreatstore.composeapp.generated.resources.Res
import jbchretreatstore.composeapp.generated.resources.cart_icon_description
import jbchretreatstore.composeapp.generated.resources.checkout_view_item_checkout
import jbchretreatstore.composeapp.generated.resources.checkout_view_item_price
import jbchretreatstore.composeapp.generated.resources.checkout_view_item_save_to_waitlist
import jbchretreatstore.composeapp.generated.resources.checkout_view_item_subtotal
import jbchretreatstore.composeapp.generated.resources.checkout_view_item_title
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun CheckoutScreen(
    state: BookStoreViewState,
    onUserIntent: (BookStoreIntent) -> Unit
) {
    // State management
    val radioOptions = listOf(ZELLE, VENMO, CASH)
    val selectedOption = remember { mutableStateOf(radioOptions[2]) }
    var checkoutStatus by remember { mutableStateOf(CheckoutStatus.PENDING) }

    // Navigate back if cart is empty - use LaunchedEffect to prevent composition issues
    LaunchedEffect(state.currentCheckoutList.checkoutList.isEmpty()) {
        if (state.currentCheckoutList.checkoutList.isEmpty()) {
            onUserIntent.invoke(BookStoreIntent.OnNavigate(BookStoreNavDestination.ShopScreen))
        }
    }

    Surface(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Title
            TitleView(stringResource(Res.string.checkout_view_item_title))

            // Checkout items list with gradient overlay
            LazyColumn(
                modifier = Modifier.fillMaxSize().weight(1f),
                verticalArrangement = Arrangement.spacedBy(Dimensions.spacing_m),
                contentPadding = PaddingValues(
                    top = Dimensions.spacing_s,
                    bottom = Dimensions.gradient_overlay_height
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

            // Bottom checkout panel
            CheckoutPanel(
                subtotal = state.currentCheckoutList.checkoutList.sumOf { it.totalPrice },
                radioOptions = radioOptions,
                selectedOption = selectedOption.value,
                onOptionSelected = { selectedOption.value = it },
                onSaveForLater = {
                    checkoutStatus = CheckoutStatus.SAVE_FOR_LATER
                    onUserIntent.invoke(
                        BookStoreIntent.OnUpdateDialogVisibility(
                            alertDialogType = AlertDialogType.CHECKOUT,
                            isVisible = true
                        )
                    )
                },
                onCheckout = {
                    checkoutStatus = CheckoutStatus.CHECKED_OUT
                    onUserIntent.invoke(
                        BookStoreIntent.OnUpdateDialogVisibility(
                            alertDialogType = AlertDialogType.CHECKOUT,
                            isVisible = true
                        )
                    )
                }
            )
        }
    }

    // Show checkout dialog
    if (state.displayCheckoutDialog) {
        CheckoutDialog(
            checkoutStatus = checkoutStatus,
            paymentMethod = selectedOption.value,
            onUserIntent = onUserIntent
        )
    }
}

@Composable
fun CheckoutPanel(
    subtotal: Double,
    radioOptions: List<PaymentMethod>,
    selectedOption: PaymentMethod,
    onOptionSelected: (PaymentMethod) -> Unit,
    onSaveForLater: () -> Unit,
    onCheckout: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = LightBlue,
        shape = Shapes.topRounded,
        shadowElevation = Dimensions.elevation_l,
        modifier = modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimensions.spacing_m),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Dimensions.spacing_s)
        ) {
            // Subtotal row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(Res.string.checkout_view_item_subtotal),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                )
                Text(
                    text = stringResource(
                        Res.string.checkout_view_item_price,
                        subtotal.toCurrency()
                    ),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.ExtraBold
                    ),
                )
            }

            // Payment method selection (horizontal)
            RadioButtonHorizontalSelection(
                radioOptions = radioOptions,
                selectedOption = selectedOption,
                onOptionSelected = onOptionSelected
            )

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onSaveForLater) {
                    Text(
                        text = stringResource(Res.string.checkout_view_item_save_to_waitlist),
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Button(onClick = onCheckout) {
                    Row(
                        modifier = Modifier.wrapContentSize(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Payments,
                            contentDescription = stringResource(Res.string.cart_icon_description),
                        )
                        Spacer(modifier = Modifier.width(Dimensions.spacing_xs))
                        Text(
                            text = stringResource(Res.string.checkout_view_item_checkout),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RadioButtonHorizontalSelection(
    radioOptions: List<PaymentMethod>,
    selectedOption: PaymentMethod,
    onOptionSelected: (PaymentMethod) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectableGroup(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        radioOptions.forEach { paymentMethod ->
            Row(
                modifier = Modifier
                    .selectable(
                        selected = paymentMethod == selectedOption,
                        onClick = { onOptionSelected(paymentMethod) },
                        role = Role.RadioButton
                    )
                    .padding(Dimensions.spacing_xs),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = paymentMethod == selectedOption,
                    onClick = { onOptionSelected(paymentMethod) }
                )
                Text(
                    text = paymentMethod.methodName,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CheckoutPanelPreview() {
    BookStoreTheme {
        CheckoutPanel(
            subtotal = 139.23,
            radioOptions = listOf(ZELLE, VENMO, CASH),
            selectedOption = CASH,
            onOptionSelected = {},
            onSaveForLater = {},
            onCheckout = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RadioButtonHorizontalSelectionPreview() {
    BookStoreTheme {
        RadioButtonHorizontalSelection(
            radioOptions = listOf(ZELLE, VENMO, CASH),
            selectedOption = VENMO,
            onOptionSelected = {}
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
