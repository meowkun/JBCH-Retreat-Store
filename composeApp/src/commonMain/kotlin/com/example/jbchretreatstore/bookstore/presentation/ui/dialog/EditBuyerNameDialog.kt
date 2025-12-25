package com.example.jbchretreatstore.bookstore.presentation.ui.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.example.jbchretreatstore.bookstore.domain.model.PaymentMethod
import com.example.jbchretreatstore.bookstore.presentation.ui.components.RadioButtonVerticalSelection
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.BookStoreTheme
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.Dimensions
import jbchretreatstore.composeapp.generated.resources.Res
import jbchretreatstore.composeapp.generated.resources.checkout_dialog_buyer_name_hint
import jbchretreatstore.composeapp.generated.resources.checkout_dialog_cancel_button
import jbchretreatstore.composeapp.generated.resources.close_dialog_description
import jbchretreatstore.composeapp.generated.resources.purchase_history_edit_buyer_name_title
import jbchretreatstore.composeapp.generated.resources.purchase_history_edit_save
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun EditBuyerNameDialog(
    currentBuyerName: String,
    currentPaymentMethod: PaymentMethod,
    onDismiss: () -> Unit,
    onSave: (newBuyerName: String, newPaymentMethod: PaymentMethod) -> Unit
) {
    var buyerName by remember { mutableStateOf(currentBuyerName) }
    var paymentMethod by remember { mutableStateOf(currentPaymentMethod) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(stringResource(Res.string.purchase_history_edit_buyer_name_title))
                IconButton(onClick = onDismiss) {
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
                    onValueChange = { buyerName = it },
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    label = { Text(stringResource(Res.string.checkout_dialog_buyer_name_hint)) }
                )

                RadioButtonVerticalSelection(
                    radioOptions = PaymentMethod.selectableOptions,
                    selectedOption = paymentMethod,
                    onOptionSelected = { paymentMethod = it }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onSave(buyerName.trim(), paymentMethod) },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(stringResource(Res.string.purchase_history_edit_save))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(stringResource(Res.string.checkout_dialog_cancel_button))
            }
        }
    )
}

@Preview
@Composable
fun EditBuyerNameDialogPreview() {
    BookStoreTheme {
        EditBuyerNameDialog(
            currentBuyerName = "John Doe",
            currentPaymentMethod = PaymentMethod.CASH,
            onDismiss = {},
            onSave = { _, _ -> }
        )
    }
}

