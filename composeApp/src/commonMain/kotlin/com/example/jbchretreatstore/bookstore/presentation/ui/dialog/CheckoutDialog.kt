package com.example.jbchretreatstore.bookstore.presentation.ui.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
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
import androidx.compose.ui.text.font.FontWeight
import com.example.jbchretreatstore.bookstore.domain.model.CheckoutStatus
import com.example.jbchretreatstore.bookstore.domain.model.PaymentMethod
import com.example.jbchretreatstore.bookstore.presentation.BookStoreIntent
import com.example.jbchretreatstore.bookstore.presentation.CheckoutState
import com.example.jbchretreatstore.bookstore.presentation.DialogVisibilityState
import com.example.jbchretreatstore.bookstore.presentation.model.AlertDialogType
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.BookStoreTheme
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.Dimensions
import jbchretreatstore.composeapp.generated.resources.Res
import jbchretreatstore.composeapp.generated.resources.checkout_dialog_buyer_name_hint
import jbchretreatstore.composeapp.generated.resources.checkout_dialog_buyer_name_title
import jbchretreatstore.composeapp.generated.resources.checkout_dialog_cancel_button
import jbchretreatstore.composeapp.generated.resources.checkout_dialog_checkout_button
import jbchretreatstore.composeapp.generated.resources.checkout_dialog_save_button
import jbchretreatstore.composeapp.generated.resources.close_dialog_description
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun CheckoutDialog(
    checkoutStatus: CheckoutStatus,
    paymentMethod: PaymentMethod,
    onPaymentMethodSelected: (PaymentMethod) -> Unit,
    onUserIntent: (BookStoreIntent) -> Unit
) {
    var showErrorState by remember { mutableStateOf(false) }
    val radioOptions = listOf(PaymentMethod.ZELLE, PaymentMethod.VENMO, PaymentMethod.CASH)
    val onDismissHandler = {
        onUserIntent.invoke(
            BookStoreIntent.OnUpdateDialogVisibility(
                dialogState = DialogVisibilityState(
                    alertDialogType = AlertDialogType.CHECKOUT,
                    isVisible = false
                )
            )
        )
    }
    var buyerName by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismissHandler,
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(stringResource(Res.string.checkout_dialog_buyer_name_title))
                IconButton(onClick = onDismissHandler) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(Res.string.close_dialog_description)
                    )
                }
            }
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(Dimensions.spacing_s)
            ) {
                OutlinedTextField(
                    value = buyerName,
                    onValueChange = {
                        buyerName = it
                        if (it.trim().isNotEmpty()) {
                            showErrorState = false
                        }
                    },
                    isError = showErrorState,
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    label = { Text(stringResource(Res.string.checkout_dialog_buyer_name_hint)) },
                    supportingText = if (showErrorState) {
                        { Text("Buyer name is required", color = MaterialTheme.colorScheme.error) }
                    } else null
                )

                // Payment method vertical selection
                RadioButtonVerticalSelection(
                    radioOptions = radioOptions,
                    selectedOption = paymentMethod,
                    onOptionSelected = onPaymentMethodSelected
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val trimmedName = buyerName.trim()
                    if (trimmedName.isEmpty()) {
                        showErrorState = true
                    } else {
                        onUserIntent.invoke(
                            BookStoreIntent.OnCheckout(
                                CheckoutState(
                                    buyerName = trimmedName,
                                    checkoutStatus = checkoutStatus,
                                    paymentMethod = paymentMethod
                                )
                            )
                        )
                    }
                },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    when (checkoutStatus) {
                        CheckoutStatus.CHECKED_OUT -> stringResource(Res.string.checkout_dialog_checkout_button)
                        CheckoutStatus.SAVE_FOR_LATER -> stringResource(Res.string.checkout_dialog_save_button)
                        else -> ""
                    }
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissHandler,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(stringResource(Res.string.checkout_dialog_cancel_button))
            }
        }
    )
}

@Composable
fun RadioButtonVerticalSelection(
    radioOptions: List<PaymentMethod>,
    selectedOption: PaymentMethod,
    onOptionSelected: (PaymentMethod) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .selectableGroup(),
        verticalArrangement = Arrangement.spacedBy(Dimensions.spacing_xs)
    ) {
        radioOptions.forEach { paymentMethod ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = paymentMethod == selectedOption,
                        onClick = { onOptionSelected(paymentMethod) },
                        role = Role.RadioButton
                    ),
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

@Preview
@Composable
fun CheckoutDialogPreview() {
    BookStoreTheme {
        CheckoutDialog(
            checkoutStatus = CheckoutStatus.CHECKED_OUT,
            paymentMethod = PaymentMethod.CASH,
            onPaymentMethodSelected = {}
        ) {}
    }
}