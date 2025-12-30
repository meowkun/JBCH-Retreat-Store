package com.example.jbchretreatstore.bookstore.presentation.ui.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.BookStoreTheme
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.Dimensions
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.White
import jbchretreatstore.composeapp.generated.resources.Res
import jbchretreatstore.composeapp.generated.resources.purchase_history_remove_cancel
import jbchretreatstore.composeapp.generated.resources.purchase_history_remove_confirm
import jbchretreatstore.composeapp.generated.resources.purchase_history_remove_message
import jbchretreatstore.composeapp.generated.resources.purchase_history_remove_title
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Dialog for confirming removal of a purchase record
 *
 * @param onDismiss Callback when the dialog is dismissed
 * @param onConfirm Callback when the user confirms the removal
 */
@Composable
fun RemoveConfirmationDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = White,
        title = null,
        text = {
            RemoveConfirmationContent(
                onDismiss = onDismiss,
                onConfirm = onConfirm
            )
        },
        confirmButton = {},
        dismissButton = {}
    )
}

/**
 * Content for the remove confirmation dialog
 */
@Composable
fun RemoveConfirmationContent(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Dimensions.spacing_s),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimensions.spacing_m)
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(bottom = Dimensions.spacing_s)
        )

        Text(
            text = stringResource(Res.string.purchase_history_remove_title),
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold
            ),
            textAlign = TextAlign.Center
        )

        Text(
            text = stringResource(Res.string.purchase_history_remove_message),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )

        Spacer(modifier = Modifier.height(Dimensions.spacing_m))

        // Confirm button
        Button(
            onClick = onConfirm,
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = Dimensions.spacing_m),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error
            ),
            shape = RoundedCornerShape(Dimensions.corner_radius_s)
        ) {
            Text(
                text = stringResource(Res.string.purchase_history_remove_confirm),
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        }

        // Cancel button
        OutlinedButton(
            onClick = onDismiss,
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = Dimensions.spacing_m),
            shape = RoundedCornerShape(Dimensions.corner_radius_s)
        ) {
            Text(
                text = stringResource(Res.string.purchase_history_remove_cancel),
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

@Preview
@Composable
private fun RemoveConfirmationContentPreview() {
    BookStoreTheme {
        Surface {
            RemoveConfirmationContent(
                onDismiss = {},
                onConfirm = {}
            )
        }
    }
}

