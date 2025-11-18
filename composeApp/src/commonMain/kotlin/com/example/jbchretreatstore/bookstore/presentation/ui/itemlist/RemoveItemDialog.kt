package com.example.jbchretreatstore.bookstore.presentation.ui.itemlist

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextAlign
import com.example.jbchretreatstore.bookstore.domain.model.DisplayItem
import com.example.jbchretreatstore.bookstore.presentation.BookStoreIntent
import com.example.jbchretreatstore.bookstore.presentation.model.AlertDialogType
import jbchretreatstore.composeapp.generated.resources.Res
import jbchretreatstore.composeapp.generated.resources.remove_item_cancel
import jbchretreatstore.composeapp.generated.resources.remove_item_dialog_message
import jbchretreatstore.composeapp.generated.resources.remove_item_dialog_title
import jbchretreatstore.composeapp.generated.resources.remove_item_remove
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun RemoveItemDialog(
    displayItem: DisplayItem,
    onUserIntent: (BookStoreIntent) -> Unit
) {
    AlertDialog(
        onDismissRequest = { onUserIntent(BookStoreIntent.OnUpdateDialogVisibility(AlertDialogType.REMOVE_ITEM, false)) },
        title = { Text(stringResource(Res.string.remove_item_dialog_title)) },
        text = {
            Text(
                stringResource(
                    Res.string.remove_item_dialog_message,
                    displayItem.name
                ),
                textAlign = TextAlign.Center
            )
        },
        confirmButton = {
            TextButton(onClick = {  onUserIntent(BookStoreIntent.OnRemoveDisplayItem(displayItem)) }) {
                Text(stringResource(Res.string.remove_item_remove))
            }
        },
        dismissButton = {
            TextButton(onClick = { onUserIntent(BookStoreIntent.OnUpdateDialogVisibility(AlertDialogType.REMOVE_ITEM, false)) }) {
                Text(stringResource(Res.string.remove_item_cancel))
            }
        }
    )
}

@Preview
@Composable
fun RemoveItemDialogPreview() {
    RemoveItemDialog(
        displayItem = DisplayItem(name = "Sample Item")
    ) {}
}
