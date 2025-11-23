package com.example.jbchretreatstore.bookstore.presentation.ui.checkout

import androidx.compose.foundation.layout.Arrangement
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
                        contentDescription = "Close dialog"
                    )
                }
            }
        },
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
            TextButton(
                onClick = {
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

@Preview
@Composable
fun CheckoutDialogPreview() {
    BookStoreTheme {
        CheckoutDialog(
            checkoutStatus = CheckoutStatus.CHECKED_OUT
        ) {}
    }
}