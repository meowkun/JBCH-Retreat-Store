package com.example.jbchretreatstore.bookstore.presentation.ui.purchasehistory

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import com.example.jbchretreatstore.bookstore.domain.model.CheckoutItem
import com.example.jbchretreatstore.bookstore.presentation.ui.components.Stepper
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.BookStoreTheme
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.Dimensions
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.Dimensions.corner_radius_percent_l
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.MediumBlue
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.White
import jbchretreatstore.composeapp.generated.resources.Res
import jbchretreatstore.composeapp.generated.resources.ic_chevron_small
import jbchretreatstore.composeapp.generated.resources.item_option_menu_selection_hint
import jbchretreatstore.composeapp.generated.resources.purchase_history_edit_cancel
import jbchretreatstore.composeapp.generated.resources.purchase_history_edit_save
import jbchretreatstore.composeapp.generated.resources.purchase_history_edit_total
import jbchretreatstore.composeapp.generated.resources.quantity_label
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Bottom sheet for editing a purchase history item's variant and quantity
 *
 * @param purchaseHistoryItem The purchase history item to edit
 * @param onDismiss Callback when the bottom sheet is dismissed
 * @param onSave Callback when the user saves the changes with the updated item
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPurchaseHistoryItemBottomSheet(
    purchaseHistoryItem: CheckoutItem,
    onDismiss: () -> Unit,
    onSave: (CheckoutItem) -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = White
    ) {
        EditPurchaseHistoryItemContent(
            purchaseHistoryItem = purchaseHistoryItem,
            onDismiss = onDismiss,
            onSave = onSave
        )
    }
}

/**
 * Content for the edit purchase history item bottom sheet
 */
@Composable
fun EditPurchaseHistoryItemContent(
    purchaseHistoryItem: CheckoutItem,
    onDismiss: () -> Unit,
    onSave: (CheckoutItem) -> Unit
) {
    val unitPrice = purchaseHistoryItem.unitPrice
    var editedItem by remember { mutableStateOf(purchaseHistoryItem) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Dimensions.spacing_m)
            .padding(bottom = Dimensions.spacing_xl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimensions.spacing_m)
    ) {
        // Item name header
        Text(
            text = purchaseHistoryItem.itemName,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold
            )
        )

        Spacer(modifier = Modifier.height(Dimensions.spacing_s))

        // Variant dropdowns - now with all available options!
        editedItem.variants.forEach { variant ->
            EditPurchaseHistoryVariantDropdown(
                variantKey = variant.key,
                selectedValue = variant.selectedValue,
                availableValues = variant.valueList,
                onValueSelected = { newValue ->
                    val updatedVariants = editedItem.variants.map { v ->
                        if (v.key == variant.key) {
                            v.copy(selectedValue = newValue)
                        } else v
                    }
                    editedItem = editedItem.copy(variants = updatedVariants)
                }
            )
        }

        Spacer(modifier = Modifier.height(Dimensions.spacing_s))

        // Quantity stepper
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(Res.string.quantity_label),
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )

            Stepper(
                value = editedItem.quantity,
                onDecrement = { newQuantity ->
                    editedItem = editedItem.copy(
                        quantity = newQuantity,
                        totalPrice = unitPrice * newQuantity
                    )
                },
                onIncrement = { newQuantity ->
                    editedItem = editedItem.copy(
                        quantity = newQuantity,
                        totalPrice = unitPrice * newQuantity
                    )
                },
                minValue = 1
            )
        }

        // Total price display
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(Res.string.purchase_history_edit_total),
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )

            Text(
                text = editedItem.formattedTotalPrice,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        }

        Spacer(modifier = Modifier.height(Dimensions.spacing_m))

        // Save button
        Button(
            onClick = { onSave(editedItem) },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            shape = RoundedCornerShape(Dimensions.corner_radius_s)
        ) {
            Text(
                text = stringResource(Res.string.purchase_history_edit_save),
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        }

        // Cancel button
        OutlinedButton(
            onClick = onDismiss,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(Dimensions.corner_radius_s)
        ) {
            Text(
                text = stringResource(Res.string.purchase_history_edit_cancel),
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditPurchaseHistoryVariantDropdown(
    variantKey: String,
    selectedValue: String,
    availableValues: List<String>,
    onValueSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val rotationAngle = if (expanded) 180f else 0f

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            modifier = Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable, true)
                .fillMaxWidth(),
            value = selectedValue,
            onValueChange = { },
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Bold
            ),
            label = {
                Text(
                    stringResource(
                        Res.string.item_option_menu_selection_hint,
                        variantKey
                    )
                )
            },
            readOnly = true,
            shape = RoundedCornerShape(corner_radius_percent_l),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = White,
                unfocusedContainerColor = White,
                focusedIndicatorColor = MaterialTheme.colorScheme.secondary,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.secondary
            ),
            trailingIcon = {
                Image(
                    painter = painterResource(Res.drawable.ic_chevron_small),
                    contentDescription = null,
                    modifier = Modifier
                        .wrapContentSize()
                        .rotate(rotationAngle)
                )
            }
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .background(MediumBlue)
                .clip(RoundedCornerShape(Dimensions.corner_radius_m))
        ) {
            availableValues.forEach { value ->
                DropdownMenuItem(
                    text = {
                        Text(
                            value,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    },
                    onClick = {
                        onValueSelected(value)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Preview
@Composable
private fun EditPurchaseHistoryItemContentPreview() {
    BookStoreTheme {
        Surface {
            EditPurchaseHistoryItemContent(
                purchaseHistoryItem = CheckoutItem(
                    itemName = "Bible",
                    quantity = 2,
                    totalPrice = 80.0,
                    variants = listOf(
                        CheckoutItem.Variant(
                            key = "Language",
                            valueList = listOf("English", "Chinese", "Spanish"),
                            selectedValue = "English"
                        ),
                        CheckoutItem.Variant(
                            key = "Version",
                            valueList = listOf("NIV", "KJV", "ESV"),
                            selectedValue = "NIV"
                        )
                    )
                ),
                onDismiss = {},
                onSave = {}
            )
        }
    }
}

