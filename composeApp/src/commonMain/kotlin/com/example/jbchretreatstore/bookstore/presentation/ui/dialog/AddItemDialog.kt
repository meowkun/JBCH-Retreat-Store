package com.example.jbchretreatstore.bookstore.presentation.ui.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.example.jbchretreatstore.bookstore.domain.model.DisplayItem
import com.example.jbchretreatstore.bookstore.presentation.BookStoreIntent
import com.example.jbchretreatstore.bookstore.presentation.model.AlertDialogType
import com.example.jbchretreatstore.bookstore.presentation.ui.components.DialogTitle
import com.example.jbchretreatstore.bookstore.presentation.ui.components.LabeledTextField
import com.example.jbchretreatstore.bookstore.presentation.ui.components.VariantValueItem
import com.example.jbchretreatstore.bookstore.presentation.ui.shop.AddItemState
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.BookStoreTheme
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.DarkBlue
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.Dimensions
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.GrayBlue
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.MediumBlue
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.White
import com.example.jbchretreatstore.bookstore.presentation.utils.filterNumericInputWithMaxDecimals
import jbchretreatstore.composeapp.generated.resources.Res
import jbchretreatstore.composeapp.generated.resources.add_item_add_more_value
import jbchretreatstore.composeapp.generated.resources.add_item_add_variant
import jbchretreatstore.composeapp.generated.resources.add_item_back
import jbchretreatstore.composeapp.generated.resources.add_item_dialog_title
import jbchretreatstore.composeapp.generated.resources.add_item_name_label
import jbchretreatstore.composeapp.generated.resources.add_item_name_place_holder
import jbchretreatstore.composeapp.generated.resources.add_item_options_key
import jbchretreatstore.composeapp.generated.resources.add_item_options_key_placeholder
import jbchretreatstore.composeapp.generated.resources.add_item_options_label
import jbchretreatstore.composeapp.generated.resources.add_item_options_value
import jbchretreatstore.composeapp.generated.resources.add_item_options_value_placeholder
import jbchretreatstore.composeapp.generated.resources.add_item_price_label
import jbchretreatstore.composeapp.generated.resources.add_item_price_place_holder
import jbchretreatstore.composeapp.generated.resources.add_item_save
import jbchretreatstore.composeapp.generated.resources.ic_close
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Dialog for adding a new item to the store.
 * Supports adding basic item information (name, price) and optional variants.
 *
 * @param onUserIntent Callback for handling user intents
 */
@Composable
fun AddItemDialog(
    onUserIntent: (BookStoreIntent) -> Unit
) {
    var viewState by remember { mutableStateOf(AddItemState()) }

    AlertDialog(
        onDismissRequest = {
            onUserIntent(
                BookStoreIntent.OnUpdateDialogVisibility(
                    alertDialogType = AlertDialogType.ADD_ITEM,
                    isVisible = false
                )
            )
        },
        containerColor = White,
        title = {
            DialogTitle(
                title = stringResource(Res.string.add_item_dialog_title),
                showBackArrow = viewState.displayAddOptionView,
                onClose = {
                    if (viewState.displayAddOptionView) {
                        // Navigate back to AddItemContent
                        viewState = viewState.copy(
                            displayAddOptionView = false,
                            showAddOptionError = false,
                            showValueError = false,
                            newItemVariant = DisplayItem.Variant()
                        )
                    } else {
                        // Close the dialog
                        onUserIntent(
                            BookStoreIntent.OnUpdateDialogVisibility(
                                alertDialogType = AlertDialogType.ADD_ITEM,
                                isVisible = false
                            )
                        )
                    }
                }
            )
        },
        text = {
            if (viewState.displayAddOptionView) {
                viewState.AddNewVariantView(
                    updateViewState = { viewState = it }
                )
            } else {
                viewState.AddItemContent(
                    updateViewState = { viewState = it },
                    onUserIntent = onUserIntent
                )
            }
        },
        confirmButton = {},
        dismissButton = {}
    )
}

/**
 * Content view for adding a new item with basic information.
 * Displays name and price fields, existing variants list, and action buttons.
 */
@Composable
private fun AddItemState.AddItemContent(
    updateViewState: (AddItemState) -> Unit,
    onUserIntent: (BookStoreIntent) -> Unit
) {
    var displayPrice by remember {
        mutableStateOf(
            if (newItem.price > 0.0) newItem.price.toString() else ""
        )
    }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(scrollState)
            .imePadding()
    ) {
        // Name field
        LabeledTextField(
            value = newItem.name,
            onValueChange = { name ->
                updateViewState(
                    this@AddItemContent.copy(
                        newItem = newItem.copy(name = name),
                        showItemNameError = false
                    )
                )
            },
            label = stringResource(Res.string.add_item_name_label),
            placeholder = stringResource(Res.string.add_item_name_place_holder),
            isError = showItemNameError && newItem.name.isBlank()
        )

        Spacer(Modifier.height(Dimensions.spacing_m))

        // Price field
        LabeledTextField(
            value = displayPrice,
            onValueChange = { input ->
                input.filterNumericInputWithMaxDecimals()?.let { filtered ->
                    displayPrice = filtered
                    updateViewState(
                        this@AddItemContent.copy(
                            newItem = newItem.copy(
                                price = filtered.toDoubleOrNull() ?: 0.0
                            ),
                            showItemPriceError = false
                        )
                    )
                }
            },
            label = stringResource(Res.string.add_item_price_label),
            placeholder = stringResource(Res.string.add_item_price_place_holder),
            isError = showItemPriceError && newItem.price <= 0.0,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Done
            )
        )

        // Existing variants section
        if (newItem.variants.isNotEmpty()) {

            HorizontalDivider(modifier = Modifier.padding(vertical = Dimensions.spacing_m))

            Text(
                text = stringResource(Res.string.add_item_options_label),
                style = MaterialTheme.typography.labelLarge
            )

            Spacer(Modifier.height(Dimensions.spacing_s))

            newItem.variants.forEach { variant ->
                VariantDisplayItem(
                    variant = variant,
                    onRemove = {
                        updateViewState(
                            this@AddItemContent.copy(
                                newItem = newItem.copy(
                                    variants = newItem.variants.filter { it != variant }
                                )
                            )
                        )
                    }
                )
                Spacer(Modifier.height(Dimensions.spacing_s))
            }
        }

        Spacer(Modifier.height(Dimensions.spacing_m))

        // Add variant button
        Button(
            onClick = {
                updateViewState(
                    this@AddItemContent.copy(
                        displayAddOptionView = true
                    )
                )
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.primary
            ),
            shape = RoundedCornerShape(Dimensions.corner_radius_s)
        ) {
            Text(
                text = stringResource(Res.string.add_item_add_variant),
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.ExtraBold
                )
            )
        }

        Spacer(Modifier.height(Dimensions.spacing_s))

        // Save button
        Button(
            onClick = {
                val isNameValid = newItem.name.isNotBlank()
                val isPriceValid = newItem.price > 0.0

                if (isNameValid && isPriceValid) {
                    onUserIntent(BookStoreIntent.OnAddDisplayItem(newItem))
                } else {
                    updateViewState(
                        this@AddItemContent.copy(
                            showItemNameError = !isNameValid,
                            showItemPriceError = !isPriceValid
                        )
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            shape = RoundedCornerShape(Dimensions.corner_radius_s)
        ) {
            Text(
                text = stringResource(Res.string.add_item_save),
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.ExtraBold
                )
            )
        }
    }
}

/**
 * Content view for adding a new variant with key and values.
 * The variant key field is locked after adding the first value and unlocked when all values are removed.
 *
 * @param updateViewState Callback to update the parent state
 */
@Composable
fun AddItemState.AddNewVariantView(
    updateViewState: (AddItemState) -> Unit
) {
    var optionValue by remember { mutableStateOf("") }
    var isOptionKeyEnabled by remember { mutableStateOf(true) }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(scrollState)
            .imePadding()
    ) {
        // Variant key field (locks after first value is added)
        LabeledTextField(
            value = newItemVariant.key,
            onValueChange = { key ->
                updateViewState(
                    this@AddNewVariantView.copy(
                        newItemVariant = newItemVariant.copy(key = key),
                        showAddOptionError = false
                    )
                )
            },
            label = stringResource(Res.string.add_item_options_key),
            placeholder = stringResource(Res.string.add_item_options_key_placeholder),
            enabled = isOptionKeyEnabled,
            isError = showAddOptionError
        )

        HorizontalDivider(
            modifier = Modifier.padding(vertical = Dimensions.spacing_m),
            color = MaterialTheme.colorScheme.outline
        )

        // Display list of added variant values
        newItemVariant.valueList.forEach { value ->
            VariantValueItem(
                value = value,
                onRemove = {
                    val updatedList = newItemVariant.valueList.filter { it != value }
                    updateViewState(
                        this@AddNewVariantView.copy(
                            newItemVariant = newItemVariant.copy(
                                valueList = updatedList
                            )
                        )
                    )
                    // Unlock key field when all values are removed
                    if (updatedList.isEmpty()) {
                        isOptionKeyEnabled = true
                    }
                }
            )
            Spacer(Modifier.height(Dimensions.spacing_s))
        }

        // Variant value input field
        LabeledTextField(
            value = optionValue,
            onValueChange = { value ->
                optionValue = value
                updateViewState(
                    this@AddNewVariantView.copy(
                        showValueError = false
                    )
                )
            },
            label = stringResource(Res.string.add_item_options_value),
            placeholder = stringResource(Res.string.add_item_options_value_placeholder),
            isError = showValueError
        )

        Spacer(Modifier.height(Dimensions.spacing_m))

        // Add more variant value button
        Button(
            onClick = {
                // Validate variant key first
                if (newItemVariant.key.isBlank()) {
                    updateViewState(
                        this@AddNewVariantView.copy(
                            showAddOptionError = true
                        )
                    )
                    return@Button
                }

                // Validate variant value
                if (optionValue.isBlank()) {
                    updateViewState(
                        this@AddNewVariantView.copy(
                            showValueError = true
                        )
                    )
                    return@Button
                }

                // Check for duplicate values
                if (newItemVariant.valueList.contains(optionValue.trim())) {
                    updateViewState(
                        this@AddNewVariantView.copy(
                            showValueError = true
                        )
                    )
                    return@Button
                }

                // Add the value and lock the key field
                updateViewState(
                    this@AddNewVariantView.copy(
                        newItemVariant = newItemVariant.copy(
                            valueList = newItemVariant.valueList + optionValue.trim()
                        ),
                        showValueError = false
                    )
                )
                optionValue = ""
                isOptionKeyEnabled = false
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MediumBlue,
                contentColor = DarkBlue
            ),
            shape = RoundedCornerShape(Dimensions.corner_radius_s)
        ) {
            Text(
                text = stringResource(Res.string.add_item_add_more_value),
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.ExtraBold
                )
            )
        }

        Spacer(Modifier.height(Dimensions.spacing_s))

        // Back button (saves variant and returns to main view)
        Button(
            onClick = {
                val isOptionValid = newItemVariant.key.isNotBlank() &&
                        newItemVariant.valueList.isNotEmpty()

                if (isOptionValid) {
                    // Save variant and navigate back
                    updateViewState(
                        this@AddNewVariantView.copy(
                            displayAddOptionView = false,
                            showAddOptionError = false,
                            showValueError = false,
                            newItem = newItem.copy(
                                variants = newItem.variants + newItemVariant
                            ),
                            newItemVariant = DisplayItem.Variant()
                        )
                    )
                } else {
                    // Show error if variant is incomplete
                    updateViewState(
                        this@AddNewVariantView.copy(
                            showAddOptionError = newItemVariant.key.isBlank()
                        )
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            shape = RoundedCornerShape(Dimensions.corner_radius_s)
        ) {
            Text(
                text = stringResource(Res.string.add_item_back),
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.ExtraBold
                )
            )
        }
    }
}

/**
 * Displays a variant in a dropdown-style field with all values visible in the menu.
 * Shows the variant key in the field, with a dropdown menu to view all values.
 *
 * @param variant The variant to display
 * @param onRemove Callback when the remove button is clicked
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VariantDisplayItem(
    variant: DisplayItem.Variant,
    onRemove: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it },
            modifier = Modifier.weight(1f)
        ) {
            OutlinedTextField(
                value = variant.key,
                onValueChange = {},
                modifier = Modifier
                    .menuAnchor(
                        androidx.compose.material3.ExposedDropdownMenuAnchorType.PrimaryNotEditable,
                        true
                    )
                    .fillMaxWidth(),
                readOnly = true,
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(
                    focusedContainerColor = White,
                    unfocusedContainerColor = White,
                    focusedBorderColor = MaterialTheme.colorScheme.secondary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.secondary
                ),
                shape = RoundedCornerShape(Dimensions.corner_radius_percent_m)
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .background(MediumBlue)
                    .clip(RoundedCornerShape(Dimensions.corner_radius_m))
            ) {
                variant.valueList.forEach { value ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = value,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        },
                        onClick = {
                            expanded = false
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                    )
                }
            }
        }

        IconButton(onClick = onRemove) {
            Icon(
                painter = painterResource(Res.drawable.ic_close),
                contentDescription = null,
                tint = GrayBlue
            )
        }
    }
}

@Preview
@Composable
fun VariantDisplayItemPreview() {
    BookStoreTheme {
        VariantDisplayItem(
            variant = DisplayItem.Variant(
                key = "Language",
                valueList = listOf("English", "French", "Spanish")
            ),
            onRemove = {}
        )
    }
}

@Preview
@Composable
fun AddItemDialogPreview() {
    BookStoreTheme {
        AddItemDialog { }
    }
}