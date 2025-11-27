package com.example.jbchretreatstore.bookstore.presentation.ui.dialog

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.example.jbchretreatstore.bookstore.domain.model.DisplayItem
import com.example.jbchretreatstore.bookstore.presentation.BookStoreIntent
import com.example.jbchretreatstore.bookstore.presentation.model.AlertDialogType
import com.example.jbchretreatstore.bookstore.presentation.ui.shop.AddItemState
import com.example.jbchretreatstore.bookstore.presentation.ui.shop.ItemOptionDescription
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.BookStoreTheme
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.Dimensions
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.Dimensions.corner_radius_percent_m
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.LabelGray
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.White
import com.example.jbchretreatstore.bookstore.presentation.utils.filterNumericInputWithMaxDecimals
import jbchretreatstore.composeapp.generated.resources.Res
import jbchretreatstore.composeapp.generated.resources.add_item_add
import jbchretreatstore.composeapp.generated.resources.add_item_dialog_title
import jbchretreatstore.composeapp.generated.resources.add_item_name_place_holder
import jbchretreatstore.composeapp.generated.resources.add_item_options_key
import jbchretreatstore.composeapp.generated.resources.add_item_options_label
import jbchretreatstore.composeapp.generated.resources.add_item_options_value
import jbchretreatstore.composeapp.generated.resources.add_item_price_place_holder
import jbchretreatstore.composeapp.generated.resources.add_item_save
import jbchretreatstore.composeapp.generated.resources.ic_close
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun AddItemDialog(
    onUserIntent: (BookStoreIntent) -> Unit
) {
    var viewState by remember { mutableStateOf(AddItemState()) }
    val confirmButtonText = if (viewState.displayAddOptionView) {
        stringResource(Res.string.add_item_save)
    } else {
        stringResource(Res.string.add_item_add)
    }
    AlertDialog(
        onDismissRequest = {
            onUserIntent.invoke(
                BookStoreIntent.OnUpdateDialogVisibility(
                    AlertDialogType.ADD_ITEM,
                    false
                )
            )
        },
        containerColor = White,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(stringResource(Res.string.add_item_dialog_title))
                IconButton(
                    onClick = {
                        onUserIntent(
                            BookStoreIntent.OnUpdateDialogVisibility(
                                AlertDialogType.ADD_ITEM,
                                false
                            )
                        )
                    }
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_close),
                        contentDescription = "Close dialog"
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                if (viewState.displayAddOptionView) {
                    viewState.AddNewOptionView {
                        viewState = it
                    }
                } else {
                    viewState.AddItemContent(
                        updateViewState = { viewState = it },
                        onUserIntent = onUserIntent
                    )
                }
            }
        },
        confirmButton = {
            if (viewState.displayAddOptionView) {
                TextButton(
                    onClick = {
                        val isOptionValid =
                            viewState.newItemVariant.key.isNotBlank() && viewState.newItemVariant.valueList.isNotEmpty()
                        if (isOptionValid) {
                            viewState = viewState.copy(
                                displayAddOptionView = false,
                                showAddOptionError = false,
                                newItem = viewState.newItem.copy(
                                    variants = viewState.newItem.variants + viewState.newItemVariant
                                ),
                                newItemVariant = DisplayItem.Variant(),
                            )
                        } else {
                            viewState = viewState.copy(
                                showAddOptionError = true
                            )
                        }
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(confirmButtonText)
                }
            }
        },
        dismissButton = {
            if (viewState.displayAddOptionView) {
                TextButton(
                    onClick = {
                        viewState = viewState.copy(
                            displayAddOptionView = false,
                            showAddOptionError = false,
                            newItemVariant = DisplayItem.Variant()
                        )
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Cancel")
                }
            }
        }
    )
}

@Composable
private fun AddItemState.AddItemContent(
    updateViewState: (AddItemState) -> Unit,
    onUserIntent: (BookStoreIntent) -> Unit
) {
    var displayPrice by remember {
        if (newItem.price > 0.0) {
            mutableStateOf(newItem.price.toString())
        } else {
            mutableStateOf("")
        }
    }
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            modifier = Modifier.padding(start = Dimensions.spacing_m),
            text = "Name",
            style = MaterialTheme.typography.titleSmall,
            color = LabelGray
        )

        Spacer(Modifier.height(Dimensions.spacing_xs))

        OutlinedTextField(
            value = newItem.name,
            onValueChange = {
                updateViewState.invoke(
                    this@AddItemContent.copy(
                        newItem = newItem.copy(name = it),
                        showItemNameError = false
                    )
                )
            },
            isError = showItemNameError && newItem.name.isBlank(),
            singleLine = true,
            placeholder = {
                Text(
                    stringResource(Res.string.add_item_name_place_holder),
                    color = LabelGray
                )
            },
            shape = RoundedCornerShape(corner_radius_percent_m),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = White,
                unfocusedContainerColor = White,
                focusedIndicatorColor = MaterialTheme.colorScheme.secondary,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.secondary,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            )
        )

        Spacer(Modifier.height(Dimensions.spacing_m))

        Text(
            modifier = Modifier.padding(start = Dimensions.spacing_m),
            text = "Price",
            style = MaterialTheme.typography.titleSmall,
            color = LabelGray
        )

        Spacer(Modifier.height(Dimensions.spacing_xs))

        OutlinedTextField(
            value = displayPrice,
            onValueChange = {
                it.filterNumericInputWithMaxDecimals()?.let { filtered ->
                    displayPrice = filtered
                    updateViewState.invoke(
                        this@AddItemContent.copy(
                            newItem = newItem.copy(price = filtered.toDoubleOrNull() ?: 0.0),
                            showItemPriceError = false
                        )
                    )
                }
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Done
            ),
            isError = showItemPriceError && newItem.price <= 0.0,
            shape = RoundedCornerShape(corner_radius_percent_m),
            placeholder = {
                Text(
                    stringResource(Res.string.add_item_price_place_holder),
                    color = LabelGray
                )
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = White,
                unfocusedContainerColor = White,
                focusedIndicatorColor = MaterialTheme.colorScheme.secondary,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.secondary,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            )
        )

        Spacer(Modifier.height(Dimensions.spacing_m))

        if (newItem.variants.isNotEmpty()) {
            Text(
                text = stringResource(Res.string.add_item_options_label),
                style = MaterialTheme.typography.labelLarge
            )
        }

        newItem.variants.forEach { option ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ItemOptionDescription(option)
                IconButton(onClick = {
                    updateViewState(
                        this@AddItemContent.copy(
                            newItem = newItem.copy(
                                variants = newItem.variants.filter { it != option }
                            )
                        )
                    )
                }) {
                    Icon(Icons.Filled.Remove, contentDescription = null)
                }
            }
        }

        Spacer(Modifier.height(Dimensions.spacing_m))

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
            Text("Add variant")
        }

        Spacer(Modifier.height(Dimensions.spacing_s))

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
            Text("Save")
        }
    }
}

@Composable
fun AddItemState.AddNewOptionView(
    updateViewState: (AddItemState) -> Unit
) {
    var optionValue by remember { mutableStateOf("") }
    var showValueError by remember { mutableStateOf(false) }
    var isOptionKeyEnabled by remember { mutableStateOf(true) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            modifier = Modifier.padding(start = Dimensions.spacing_m),
            text = stringResource(Res.string.add_item_options_key),
            style = MaterialTheme.typography.titleSmall,
            color = LabelGray
        )

        Spacer(Modifier.height(Dimensions.spacing_s))

        OutlinedTextField(
            value = newItemVariant.key,
            onValueChange = {
                updateViewState(
                    this@AddNewOptionView.copy(
                        newItemVariant = this@AddNewOptionView.newItemVariant.copy(key = it),
                        showAddOptionError = false
                    )
                )
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = isOptionKeyEnabled,
            isError = showAddOptionError,
            singleLine = true,
            shape = RoundedCornerShape(corner_radius_percent_m),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = White,
                unfocusedContainerColor = White,
                focusedIndicatorColor = MaterialTheme.colorScheme.secondary,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.secondary,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            )
        )

        Spacer(Modifier.height(Dimensions.spacing_m))

        Text(
            modifier = Modifier.padding(start = Dimensions.spacing_m),
            text = stringResource(Res.string.add_item_options_value),
            style = MaterialTheme.typography.titleSmall,
            color = LabelGray
        )

        Spacer(Modifier.height(Dimensions.spacing_s))

        OutlinedTextField(
            value = optionValue,
            onValueChange = {
                optionValue = it
                showValueError = false
            },
            modifier = Modifier.fillMaxWidth(),
            isError = showValueError,
            singleLine = true,
            shape = RoundedCornerShape(corner_radius_percent_m),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = White,
                unfocusedContainerColor = White,
                focusedIndicatorColor = MaterialTheme.colorScheme.secondary,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.secondary,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            )
        )
        Spacer(Modifier.height(Dimensions.spacing_m))
        if (newItemVariant.key.isNotEmpty()) {
            ItemOptionDescription(
                DisplayItem.Variant(
                    newItemVariant.key,
                    newItemVariant.valueList
                )
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    if (newItemVariant.valueList.isEmpty()) {
                        isOptionKeyEnabled = true
                        updateViewState(
                            this@AddNewOptionView.copy(
                                newItemVariant = newItemVariant.copy(key = "")
                            )
                        )
                    } else {
                        updateViewState(
                            this@AddNewOptionView.copy(
                                newItemVariant = newItemVariant.copy(
                                    valueList = newItemVariant.valueList.dropLast(1)
                                )
                            )
                        )
                    }
                },
                enabled = newItemVariant.key.isNotEmpty()
            ) {
                Icon(Icons.Filled.Remove, contentDescription = null)
            }
            IconButton(
                onClick = {
                    if (optionValue.isNotEmpty()) {
                        updateViewState.invoke(
                            this@AddNewOptionView.copy(
                                newItemVariant = newItemVariant.copy(
                                    valueList = newItemVariant.valueList + optionValue
                                )
                            )
                        )
                        optionValue = ""
                        isOptionKeyEnabled = false
                    } else {
                        showValueError = false
                    }
                }) {
                Icon(Icons.Filled.Add, contentDescription = null)
            }
        }
    }
}

@Preview
@Composable
fun AddItemDialogPreview() {
    BookStoreTheme {
        AddItemDialog { }
    }
}