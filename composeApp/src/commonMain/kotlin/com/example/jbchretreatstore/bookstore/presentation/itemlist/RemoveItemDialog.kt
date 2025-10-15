package com.example.jbchretreatstore.bookstore.presentation.itemlist

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextAlign
import jbchretreatstore.composeapp.generated.resources.Res
import jbchretreatstore.composeapp.generated.resources.remove_item_cancel
import jbchretreatstore.composeapp.generated.resources.remove_item_dialog_message
import jbchretreatstore.composeapp.generated.resources.remove_item_dialog_title
import jbchretreatstore.composeapp.generated.resources.remove_item_remove
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun RemoveItemDialog(
    itemName: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(Res.string.remove_item_dialog_title)) },
        text = {
            Text(
                stringResource(
                    Res.string.remove_item_dialog_message,
                    itemName
                ),
                textAlign = TextAlign.Center
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(stringResource(Res.string.remove_item_remove))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(Res.string.remove_item_cancel))
            }
        }
    )
}

@Preview
@Composable
fun RemoveItemDialogPreview() {
    RemoveItemDialog(
        itemName = "Sample Item",
        onDismiss = {},
        onConfirm = {}
    )
}
