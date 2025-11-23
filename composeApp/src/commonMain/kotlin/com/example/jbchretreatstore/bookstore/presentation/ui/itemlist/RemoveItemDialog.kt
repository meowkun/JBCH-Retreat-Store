package com.example.jbchretreatstore.bookstore.presentation.ui.itemlist

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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.example.jbchretreatstore.bookstore.domain.model.DisplayItem
import com.example.jbchretreatstore.bookstore.presentation.BookStoreIntent
import com.example.jbchretreatstore.bookstore.presentation.model.AlertDialogType
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.BookStoreTheme
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
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(stringResource(Res.string.remove_item_dialog_title))
                IconButton(
                    onClick = {
                        onUserIntent(
                            BookStoreIntent.OnUpdateDialogVisibility(
                                AlertDialogType.REMOVE_ITEM,
                                false
                            )
                        )
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close dialog"
                    )
                }
            }
        },
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
            TextButton(
                onClick = {
                    onUserIntent(BookStoreIntent.OnDeleteDisplayItem(displayItem))
                    onUserIntent(
                        BookStoreIntent.OnUpdateDialogVisibility(
                            AlertDialogType.REMOVE_ITEM,
                            false
                        )
                    )
                },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(stringResource(Res.string.remove_item_remove))
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onUserIntent(
                        BookStoreIntent.OnUpdateDialogVisibility(
                            AlertDialogType.REMOVE_ITEM,
                            false
                        )
                    )
                },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(stringResource(Res.string.remove_item_cancel))
            }
        }
    )
}

@Preview
@Composable
fun RemoveItemDialogPreview() {
    BookStoreTheme {
        RemoveItemDialog(
            displayItem = DisplayItem(name = "Sample Item")
        ) {}
    }
}
