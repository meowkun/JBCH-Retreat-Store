package com.example.jbchretreatstore.bookstore.presentation.ui.checkout

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.jbchretreatstore.bookstore.domain.model.CheckoutStatus
import com.example.jbchretreatstore.bookstore.presentation.BookStoreIntent
import com.example.jbchretreatstore.bookstore.presentation.model.AlertDialogType
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.BookStoreTheme
import jbchretreatstore.composeapp.generated.resources.Res
import jbchretreatstore.composeapp.generated.resources.checkout_dialog_buyer_name_hint
import jbchretreatstore.composeapp.generated.resources.checkout_dialog_buyer_name_title
import jbchretreatstore.composeapp.generated.resources.checkout_dialog_cancel_button
import jbchretreatstore.composeapp.generated.resources.checkout_dialog_checkout_button
import jbchretreatstore.composeapp.generated.resources.checkout_dialog_save_button
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun CheckoutDialog(
    checkoutStatus: CheckoutStatus,
    onUserIntent: (BookStoreIntent) -> Unit
) {
    var showErrorState by remember { mutableStateOf(false) }
    val onDismissHandler = {
        onUserIntent.invoke(
            BookStoreIntent.OnUpdateDialogVisibility(
                alertDialogType = AlertDialogType.CHECKOUT,
                isVisible = false
            )
        )
    }
    var buyerName by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismissHandler,
        title = { Text(stringResource(Res.string.checkout_dialog_buyer_name_title)) },
        text = {
            OutlinedTextField(
                value = buyerName,
                onValueChange = {
                    buyerName = it
                    if (it.isNotEmpty()) {
                        showErrorState = false
                    }
                },
                isError = showErrorState,
                singleLine = true,
                label = { Text(stringResource(Res.string.checkout_dialog_buyer_name_hint)) }
            )
        },
        confirmButton = {
            TextButton(onClick = {
                if (buyerName.isEmpty()) {
                    showErrorState = true
                } else {
                    onUserIntent.invoke(
                        BookStoreIntent.OnCheckout(
                            buyerName = buyerName,
                            checkoutStatus = checkoutStatus
                        )
                    )
                }
            }) {
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
            TextButton(onClick = onDismissHandler) {
                Text(stringResource(Res.string.checkout_dialog_cancel_button))
            }
        }
    )
}

@Preview
@Composable
fun CheckoutDialogPreview() {
    BookStoreTheme {
        CheckoutDialog(
            checkoutStatus = CheckoutStatus.CHECKED_OUT
        ) {}
    }
}