package com.example.jbchretreatstore.bookstore.presentation.ui.checkout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
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
import com.example.jbchretreatstore.bookstore.presentation.ui.components.BottomGradientOverlay
import com.example.jbchretreatstore.bookstore.presentation.ui.components.TitleView
import com.example.jbchretreatstore.bookstore.presentation.ui.dialog.CheckoutDialog
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.BookStoreTheme
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.Dimensions
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.MediumBlue
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.Shapes
import jbchretreatstore.composeapp.generated.resources.Res
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
    val radioOptions = listOf(ZELLE, VENMO, CASH)
    val selectedOption = remember { mutableStateOf(radioOptions[2]) }
    if (state.currentCheckoutList.checkoutList.isEmpty()) {
        onUserIntent.invoke(BookStoreIntent.OnNavigate(BookStoreNavDestination.ShopScreen))
    }
    var checkoutStatus by remember { mutableStateOf(CheckoutStatus.PENDING) }
    Surface(
        modifier = Modifier
            .fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding(),

            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            TitleView(stringResource(Res.string.checkout_view_item_title))

            Box(modifier = Modifier.weight(1f)) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(Dimensions.spacing_m),
                    contentPadding = PaddingValues(bottom = Dimensions.gradient_overlay_height),
                ) {
                    items(
                        items = state.currentCheckoutList.checkoutList,
                        key = { "${it.id}_${it.variantsMap.hashCode()}" }) { item ->
                        CheckoutItemView(
                            checkoutItem = item,
                            onUserIntent = onUserIntent
                        )
                    }
                }
                BottomGradientOverlay(modifier = Modifier.align(Alignment.BottomCenter))
            }

            Surface(
                color = MediumBlue,
                shape = Shapes.topRounded,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .padding(Dimensions.spacing_m),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(Res.string.checkout_view_item_subtotal),
                            style = MaterialTheme.typography.headlineSmall,
                        )
                        Text(
                            text = stringResource(
                                Res.string.checkout_view_item_price,
                                state.currentCheckoutList.checkoutList.sumOf { it.totalPrice }
                                    .toString()
                            ),
                            style = MaterialTheme.typography.headlineSmall,
                        )
                    }

                    RadioButtonSingleSelection(
                        radioOptions = radioOptions,
                        selectedOption = selectedOption.value,
                    ) {
                        selectedOption.value = it
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        TextButton(
                            onClick = {
                                checkoutStatus = CheckoutStatus.SAVE_FOR_LATER
                                onUserIntent.invoke(
                                    BookStoreIntent.OnUpdateDialogVisibility(
                                        alertDialogType = AlertDialogType.CHECKOUT,
                                        isVisible = true
                                    )
                                )
                            }
                        ) {
                            Text(stringResource(Res.string.checkout_view_item_save_to_waitlist))
                        }
                        Button(
                            modifier = Modifier,
                            onClick = {
                                checkoutStatus = CheckoutStatus.CHECKED_OUT
                                onUserIntent.invoke(
                                    BookStoreIntent.OnUpdateDialogVisibility(
                                        alertDialogType = AlertDialogType.CHECKOUT,
                                        isVisible = true
                                    )
                                )
                            }
                        ) {
                            Text(stringResource(Res.string.checkout_view_item_checkout))
                        }
                    }
                }
            }
        }
    }
    if (state.displayCheckoutDialog) {
        CheckoutDialog(
            checkoutStatus = checkoutStatus,
            paymentMethod = selectedOption.value,
            onUserIntent = onUserIntent
        )
    }
}

@Composable
fun RadioButtonSingleSelection(
    radioOptions: List<PaymentMethod>,
    selectedOption: PaymentMethod,
    onOptionSelected: (PaymentMethod) -> Unit,
) {

    Column(Modifier.selectableGroup()) {
        radioOptions.forEach { paymentMethod ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = (paymentMethod == selectedOption),
                        onClick = { onOptionSelected.invoke(paymentMethod) },
                        role = Role.RadioButton
                    )
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = (paymentMethod == selectedOption),
                    onClick = {
                        onOptionSelected.invoke(paymentMethod)
                    }
                )
                Text(
                    text = paymentMethod.methodName,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        }
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
