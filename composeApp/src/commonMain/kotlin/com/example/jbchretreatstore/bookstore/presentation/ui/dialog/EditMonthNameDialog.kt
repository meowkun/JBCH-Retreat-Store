package com.example.jbchretreatstore.bookstore.presentation.ui.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.example.jbchretreatstore.bookstore.presentation.ui.components.DialogTitle
import com.example.jbchretreatstore.bookstore.presentation.ui.purchasehistory.YearMonth
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.BookStoreTheme
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.Dimensions
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.White
import jbchretreatstore.composeapp.generated.resources.Res
import jbchretreatstore.composeapp.generated.resources.purchase_history_edit_month_name_title
import jbchretreatstore.composeapp.generated.resources.purchase_history_edit_save
import jbchretreatstore.composeapp.generated.resources.purchase_history_month_name_default
import jbchretreatstore.composeapp.generated.resources.purchase_history_month_name_hint
import kotlinx.datetime.Month
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun EditMonthNameDialog(
    yearMonth: YearMonth,
    currentName: String?,
    onDismiss: () -> Unit,
    onSave: (newName: String?) -> Unit
) {
    val defaultName =
        "${yearMonth.month.name.lowercase().replaceFirstChar { it.uppercase() }} ${yearMonth.year}"
    var monthName by remember { mutableStateOf(currentName ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = White,
        title = {
            DialogTitle(
                title = stringResource(Res.string.purchase_history_edit_month_name_title),
                onClose = onDismiss
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(Dimensions.spacing_s)
            ) {
                Text(
                    text = stringResource(
                        Res.string.purchase_history_month_name_default,
                        defaultName
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                OutlinedTextField(
                    value = monthName,
                    onValueChange = { monthName = it },
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    label = { Text(stringResource(Res.string.purchase_history_month_name_hint)) },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(Dimensions.spacing_m))

                // Save button
                Button(
                    onClick = {
                        val finalName = monthName.trim().ifBlank { null }
                        onSave(finalName)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(Dimensions.corner_radius_s)
                ) {
                    Text(
                        text = stringResource(Res.string.purchase_history_edit_save),
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.ExtraBold
                        )
                    )
                }
            }
        },
        confirmButton = {},
        dismissButton = {}
    )
}

@Preview
@Composable
fun EditMonthNameDialogPreview() {
    BookStoreTheme {
        EditMonthNameDialog(
            yearMonth = YearMonth(2025, Month.DECEMBER),
            currentName = null,
            onDismiss = {},
            onSave = {}
        )
    }
}

@Preview
@Composable
fun EditMonthNameDialogWithCustomNamePreview() {
    BookStoreTheme {
        EditMonthNameDialog(
            yearMonth = YearMonth(2025, Month.DECEMBER),
            currentName = "Christmas Sales",
            onDismiss = {},
            onSave = {}
        )
    }
}
