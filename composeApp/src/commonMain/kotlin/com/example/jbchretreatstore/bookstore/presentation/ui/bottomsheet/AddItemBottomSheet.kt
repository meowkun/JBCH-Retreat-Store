package com.example.jbchretreatstore.bookstore.presentation.ui.bottomsheet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DragHandle
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
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.example.jbchretreatstore.bookstore.domain.model.DisplayItem
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
import jbchretreatstore.composeapp.generated.resources.Res
import jbchretreatstore.composeapp.generated.resources.add_item_add_more_value
import jbchretreatstore.composeapp.generated.resources.add_item_add_variant
import jbchretreatstore.composeapp.generated.resources.add_item_duplicate_variant_key_error
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
import jbchretreatstore.composeapp.generated.resources.reorderable_drag_handle_description
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

/**
 * Bottom sheet for adding or editing an item in the store.
 * Supports adding/editing basic item information (name, price) and optional variants.
 *
 * @param onDismiss Callback when bottom sheet is dismissed
 * @param onAddItem Callback when item is added or updated
 * @param initialItem Optional item to edit. If null, bottom sheet is in add mode.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddItemBottomSheet(
    onDismiss: () -> Unit,
    onAddItem: (DisplayItem) -> Unit,
    initialItem: DisplayItem? = null
) {
    var viewState by remember {
        mutableStateOf(AddItemState.fromItem(initialItem))
    }

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { it != SheetValue.Hidden } // Prevent swipe to dismiss
    )
    val scope = rememberCoroutineScope()

    // Helper to properly hide sheet before calling onDismiss
    val hideSheet: () -> Unit = {
        scope.launch {
            sheetState.hide()
        }.invokeOnCompletion {
            onDismiss()
        }
    }

    ModalBottomSheet(
        onDismissRequest = {}, // Prevent dismiss on outside tap - use close button instead
        sheetState = sheetState,
        containerColor = White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimensions.spacing_m)
                .padding(bottom = Dimensions.spacing_xl)
        ) {
            DialogTitle(
                title = stringResource(viewState.dialogTitleRes),
                showBackArrow = viewState.displayAddOptionView,
                onClose = {
                    if (viewState.displayAddOptionView) {
                        viewState = viewState.navigateBack()
                    } else {
                        hideSheet()
                    }
                }
            )

            Spacer(Modifier.height(Dimensions.spacing_m))

            if (viewState.displayAddOptionView) {
                viewState.AddNewVariantView(
                    updateViewState = { viewState = it }
                )
            } else {
                viewState.AddItemContent(
                    updateViewState = { viewState = it },
                    onAddItem = { item ->
                        scope.launch {
                            sheetState.hide()
                        }.invokeOnCompletion {
                            onAddItem(item)
                        }
                    }
                )
            }
        }
    }
}

/**
 * Content view for adding or editing an item with basic information.
 * Displays name and price fields, existing variants list, and action buttons.
 *
 * @param updateViewState Callback to update the parent state
 * @param onAddItem Callback when save/update button is clicked
 */
@Composable
private fun AddItemState.AddItemContent(
    updateViewState: (AddItemState) -> Unit,
    onAddItem: (DisplayItem) -> Unit
) {
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
            onValueChange = { name -> updateViewState(updateName(name)) },
            label = stringResource(Res.string.add_item_name_label),
            placeholder = stringResource(Res.string.add_item_name_place_holder),
            isError = showNameFieldError
        )

        Spacer(Modifier.height(Dimensions.spacing_m))

        // Price field
        LabeledTextField(
            value = displayPrice,
            onValueChange = { input -> updatePrice(input)?.let { updateViewState(it) } },
            label = stringResource(Res.string.add_item_price_label),
            placeholder = stringResource(Res.string.add_item_price_place_holder),
            isError = showPriceFieldError,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Done
            )
        )

        if (hasVariants) {

            HorizontalDivider(modifier = Modifier.padding(vertical = Dimensions.spacing_m))

            Text(
                text = stringResource(Res.string.add_item_options_label),
                style = MaterialTheme.typography.labelLarge
            )

            Spacer(Modifier.height(Dimensions.spacing_s))

            ReorderableVariantsList(
                variants = newItem.variants,
                onReorder = { updateViewState(reorderVariants(it)) },
                onRemoveVariant = { updateViewState(removeVariant(it)) }
            )
        }

        Spacer(Modifier.height(Dimensions.spacing_m))

        // Add variant button
        Button(
            onClick = { updateViewState(showAddOptionView()) },
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

        // Save/Update button
        Button(
            onClick = {
                val (validItem, newState) = validateItem()
                if (validItem != null) {
                    onAddItem(validItem)
                } else {
                    updateViewState(newState)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            shape = RoundedCornerShape(Dimensions.corner_radius_s)
        ) {
            Text(
                text = stringResource(saveButtonTextRes),
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
            onValueChange = { key -> updateViewState(updateVariantKey(key)) },
            label = stringResource(Res.string.add_item_options_key),
            placeholder = stringResource(Res.string.add_item_options_key_placeholder),
            enabled = isOptionKeyEnabled,
            isError = showVariantKeyFieldError,
            errorMessage = if (showDuplicateKeyError) {
                stringResource(Res.string.add_item_duplicate_variant_key_error)
            } else ""
        )

        HorizontalDivider(
            modifier = Modifier.padding(vertical = Dimensions.spacing_m),
            color = MaterialTheme.colorScheme.outline
        )

        // Display reorderable list of added variant values
        ReorderableVariantValuesList(
            values = newItemVariant.valueList,
            onReorder = { updateViewState(reorderVariantValues(it)) },
            onRemoveValue = { updateViewState(removeVariantValue(it)) }
        )

        // Variant value input field
        LabeledTextField(
            value = optionValue,
            onValueChange = { value -> updateViewState(updateOptionValue(value)) },
            label = stringResource(Res.string.add_item_options_value),
            placeholder = stringResource(Res.string.add_item_options_value_placeholder),
            isError = showValueError
        )

        Spacer(Modifier.height(Dimensions.spacing_m))

        // Add more variant value button
        Button(
            onClick = { updateViewState(addVariantValue()) },
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

        // Back button (saves variant if valid and returns to main view)
        Button(
            onClick = { updateViewState(saveVariantAndNavigateBack()) },
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
 * A reorderable list of variants with drag-and-drop functionality.
 * Displays variants in a LazyColumn that supports reordering via drag handles.
 *
 * @param variants The list of variants to display
 * @param onReorder Callback when the list is reordered
 * @param onRemoveVariant Callback when a variant is removed
 */
@Composable
fun ReorderableVariantsList(
    variants: List<DisplayItem.Variant>,
    onReorder: (List<DisplayItem.Variant>) -> Unit,
    onRemoveVariant: (DisplayItem.Variant) -> Unit
) {
    val hapticFeedback = LocalHapticFeedback.current
    val lazyListState = rememberLazyListState()
    val reorderableLazyListState = rememberReorderableLazyListState(lazyListState) { from, to ->
        val mutable = variants.toMutableList()
        val moved = mutable.removeAt(from.index)
        mutable.add(to.index, moved)
        onReorder(mutable)
        hapticFeedback.performHapticFeedback(HapticFeedbackType.SegmentFrequentTick)
    }

    val listHeight = (Dimensions.reorderable_item_height * variants.size)
        .coerceAtMost(Dimensions.reorderable_list_max_height)

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .height(listHeight),
        state = lazyListState,
        verticalArrangement = Arrangement.spacedBy(Dimensions.reorderable_list_item_spacing),
    ) {
        items(variants, key = { it.id }) { variant ->
            ReorderableItem(
                reorderableLazyListState,
                key = variant.id
            ) { isDragging ->
                val elevation =
                    if (isDragging) Dimensions.reorderable_item_drag_elevation else Dimensions.elevation_none

                Surface(
                    shadowElevation = elevation,
                    shape = RoundedCornerShape(Dimensions.corner_radius_percent_m)
                ) {
                    Row {
                        IconButton(
                            modifier = Modifier.draggableHandle(
                                onDragStarted = {
                                    hapticFeedback.performHapticFeedback(
                                        HapticFeedbackType.GestureThresholdActivate
                                    )
                                },
                                onDragStopped = {
                                    hapticFeedback.performHapticFeedback(
                                        HapticFeedbackType.GestureEnd
                                    )
                                },
                            ),
                            onClick = {},
                        ) {
                            Icon(
                                Icons.Rounded.DragHandle,
                                contentDescription = stringResource(Res.string.reorderable_drag_handle_description)
                            )
                        }

                        VariantDisplayItem(
                            variant = variant,
                            onRemove = { onRemoveVariant(variant) }
                        )
                    }
                }
            }
        }
    }
}

/**
 * A reorderable list of variant values with drag-and-drop functionality.
 * Displays values in a LazyColumn that supports reordering via drag handles.
 *
 * @param values The list of variant values to display
 * @param onReorder Callback when the list is reordered
 * @param onRemoveValue Callback when a value is removed
 */
@Composable
fun ReorderableVariantValuesList(
    values: List<String>,
    onReorder: (List<String>) -> Unit,
    onRemoveValue: (String) -> Unit
) {
    if (values.isEmpty()) return

    val hapticFeedback = LocalHapticFeedback.current
    val lazyListState = rememberLazyListState()
    val reorderableLazyListState = rememberReorderableLazyListState(lazyListState) { from, to ->
        val mutable = values.toMutableList()
        val moved = mutable.removeAt(from.index)
        mutable.add(to.index, moved)
        onReorder(mutable)
        hapticFeedback.performHapticFeedback(HapticFeedbackType.SegmentFrequentTick)
    }

    val listHeight = (Dimensions.reorderable_item_height * values.size)
        .coerceAtMost(Dimensions.reorderable_list_max_height)

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .height(listHeight),
        state = lazyListState,
        verticalArrangement = Arrangement.spacedBy(Dimensions.reorderable_list_item_spacing),
    ) {
        items(values, key = { it }) { value ->
            ReorderableItem(
                reorderableLazyListState,
                key = value
            ) { isDragging ->
                val elevation =
                    if (isDragging) Dimensions.reorderable_item_drag_elevation else Dimensions.elevation_none

                Surface(
                    shadowElevation = elevation,
                    shape = RoundedCornerShape(Dimensions.corner_radius_percent_m)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            modifier = Modifier.draggableHandle(
                                onDragStarted = {
                                    hapticFeedback.performHapticFeedback(
                                        HapticFeedbackType.GestureThresholdActivate
                                    )
                                },
                                onDragStopped = {
                                    hapticFeedback.performHapticFeedback(
                                        HapticFeedbackType.GestureEnd
                                    )
                                },
                            ),
                            onClick = {},
                        ) {
                            Icon(
                                Icons.Rounded.DragHandle,
                                contentDescription = stringResource(Res.string.reorderable_drag_handle_description)
                            )
                        }

                        VariantValueItem(
                            value = value,
                            onRemove = { onRemoveValue(value) }
                        )
                    }
                }
            }
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
fun ReorderableVariantsListPreview() {
    BookStoreTheme {
        ReorderableVariantsList(
            variants = listOf(
                DisplayItem.Variant(
                    key = "Language",
                    valueList = listOf("English", "French", "Spanish")
                ),
                DisplayItem.Variant(
                    key = "Size",
                    valueList = listOf("Small", "Medium", "Large")
                ),
                DisplayItem.Variant(
                    key = "Color",
                    valueList = listOf("Red", "Blue", "Green")
                )
            ),
            onReorder = {},
            onRemoveVariant = {}
        )
    }
}

@Preview
@Composable
fun ReorderableVariantValuesListPreview() {
    BookStoreTheme {
        ReorderableVariantValuesList(
            values = listOf("English", "French", "Spanish", "German"),
            onReorder = {},
            onRemoveValue = {}
        )
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
fun AddItemBottomSheetPreview() {
    BookStoreTheme {
        AddItemBottomSheet(
            onDismiss = {},
            onAddItem = {}
        )
    }
}

@Preview
@Composable
fun EditItemBottomSheetPreview() {
    BookStoreTheme {
        AddItemBottomSheet(
            onDismiss = {},
            onAddItem = {},
            initialItem = DisplayItem(
                name = "The Bible",
                price = 29.99,
                variants = listOf(
                    DisplayItem.Variant(
                        key = "Language",
                        valueList = listOf("English", "French", "Spanish")
                    ),
                    DisplayItem.Variant(
                        key = "Cover",
                        valueList = listOf("Hardcover", "Paperback")
                    )
                )
            )
        )
    }
}

