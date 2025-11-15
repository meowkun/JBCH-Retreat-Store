package com.example.jbchretreatstore.bookstore.presentation.ui.itemlist

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.example.jbchretreatstore.bookstore.domain.model.AlertDialogType
import com.example.jbchretreatstore.bookstore.domain.model.DisplayItem
import com.example.jbchretreatstore.bookstore.presentation.BookStoreIntent
import com.example.jbchretreatstore.core.presentation.UiConstants.spacing_m
import com.example.jbchretreatstore.core.presentation.UiConstants.spacing_s
import jbchretreatstore.composeapp.generated.resources.Res
import jbchretreatstore.composeapp.generated.resources.add_item_add
import jbchretreatstore.composeapp.generated.resources.add_item_cancel
import jbchretreatstore.composeapp.generated.resources.add_item_dialog_title
import jbchretreatstore.composeapp.generated.resources.add_item_name_label
import jbchretreatstore.composeapp.generated.resources.add_item_options_key
import jbchretreatstore.composeapp.generated.resources.add_item_options_label
import jbchretreatstore.composeapp.generated.resources.add_item_options_value
import jbchretreatstore.composeapp.generated.resources.add_item_price_label
import jbchretreatstore.composeapp.generated.resources.add_item_save
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
        title = { Text(stringResource(Res.string.add_item_dialog_title)) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                if (viewState.displayAddOptionView) {
                    viewState.AddNewOptionView {
                        viewState = it
                    }
                } else {
                    viewState.AddItemContent {
                        viewState = it
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (viewState.displayAddOptionView) {
                    val isOptionValid =
                        viewState.newItemOption.optionKey.isNotBlank() && viewState.newItemOption.optionValueList.isNotEmpty()
                    viewState = if (isOptionValid) {
                        viewState.copy(
                            displayAddOptionView = false,
                            showAddOptionError = false,
                            newItem = viewState.newItem.copy(
                                options = viewState.newItem.options + viewState.newItemOption
                            ),
                            newItemOption = DisplayItem.Option(),
                        )
                    } else {
                        viewState.copy(
                            showAddOptionError = true
                        )
                    }
                } else {
                    val isItemValid =
                        viewState.newItem.name.isNotBlank() && viewState.newItem.price > 0.0
                    if (isItemValid) {
                        onUserIntent(BookStoreIntent.OnAddDisplayItem(viewState.newItem))
                    } else {
                        viewState = viewState.copy(
                            showAddItemError = true
                        )
                    }
                }
            }) {
                Text(confirmButtonText)
            }
        },
        dismissButton = {
            TextButton(onClick = {
                if (viewState.displayAddOptionView) {
                    viewState = viewState.copy(
                        displayAddOptionView = false,
                        showAddOptionError = false,
                        newItemOption = DisplayItem.Option()
                    )
                } else {
                    onUserIntent.invoke(
                        BookStoreIntent.OnUpdateDialogVisibility(
                            AlertDialogType.ADD_ITEM,
                            false
                        )
                    )
                }
            }) {
                Text(stringResource(Res.string.add_item_cancel))
            }
        }
    )
}

@Composable
private fun AddItemState.AddItemContent(
    updateViewState: (AddItemState) -> Unit
) {
    var displayPrice by remember {
        if (newItem.price > 0.0) {
            mutableStateOf(newItem.price.toString())
        } else {
            mutableStateOf("")
        }
    }
    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = newItem.name,
            onValueChange = {
                updateViewState.invoke(
                    this@AddItemContent.copy(
                        newItem = newItem.copy(name = it),
                        showAddOptionError = if (showAddOptionError) false else showAddOptionError
                    )
                )
            },
            isError = showAddItemError && newItem.name.isBlank(),
            singleLine = true,
            label = { Text(stringResource(Res.string.add_item_name_label)) }
        )

        Spacer(Modifier.height(spacing_m))

        OutlinedTextField(
            value = displayPrice,
            onValueChange = {
                displayPrice = it
                updateViewState.invoke(
                    this@AddItemContent.copy(
                        newItem = newItem.copy(price = it.toDoubleOrNull() ?: 0.0),
                        showAddOptionError = if (showAddOptionError) false else showAddOptionError
                    )
                )
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            isError = showAddItemError && newItem.price <= 0.0,
            label = { Text(stringResource(Res.string.add_item_price_label)) }
        )

        Spacer(Modifier.height(spacing_s))

        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(Res.string.add_item_options_label),
                    style = MaterialTheme.typography.labelLarge
                )
                IconButton(onClick = {
                    updateViewState(
                        this@AddItemContent.copy(
                            displayAddOptionView = true
                        )
                    )
                }) {
                    Icon(Icons.Filled.Add, contentDescription = null)
                }
            }

            newItem.options.forEach { option ->
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
                                    options = newItem.options.filter { it != option }
                                )
                            )
                        )
                    }) {
                        Icon(Icons.Filled.Remove, contentDescription = null)
                    }
                }
            }
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
        OutlinedTextField(
            value = newItemOption.optionKey,
            onValueChange = {
                updateViewState.invoke(
                    this@AddNewOptionView.copy(
                        newItemOption = this@AddNewOptionView.newItemOption.copy(optionKey = it),
                        showAddOptionError = if (showAddItemError) false else showAddItemError
                    )
                )
            },
            enabled = isOptionKeyEnabled,
            isError = showAddOptionError,
            singleLine = true,
            label = { Text(stringResource(Res.string.add_item_options_key)) }
        )
        Spacer(Modifier.height(spacing_m))
        OutlinedTextField(
            value = optionValue,
            onValueChange = {
                optionValue = it
                showValueError = false
            },
            isError = showValueError,
            singleLine = true,
            label = { Text(stringResource(Res.string.add_item_options_value)) }
        )
        Spacer(Modifier.height(spacing_m))
        if (newItemOption.optionKey.isNotEmpty()) {
            ItemOptionDescription(
                DisplayItem.Option(
                    newItemOption.optionKey,
                    newItemOption.optionValueList
                )
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                if (newItemOption.optionValueList.isEmpty()) {
                    isOptionKeyEnabled = true
                    updateViewState.invoke(
                        this@AddNewOptionView.copy(
                            newItemOption = newItemOption.copy(
                                optionKey = ""
                            )
                        )
                    )
                } else {
                    updateViewState.invoke(
                        this@AddNewOptionView.copy(
                            newItemOption = newItemOption.copy(
                                optionValueList = newItemOption.optionValueList.dropLast(1)
                            )
                        )
                    )
                }
            }) {
                Icon(Icons.Filled.Remove, contentDescription = null)
            }
            IconButton(
                onClick = {
                    if (optionValue.isNotEmpty()) {
                        updateViewState.invoke(
                            this@AddNewOptionView.copy(
                                newItemOption = newItemOption.copy(
                                    optionValueList = newItemOption.optionValueList + optionValue
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
    AddItemDialog { }
}