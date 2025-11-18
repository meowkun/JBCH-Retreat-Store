package com.example.jbchretreatstore.bookstore.presentation.ui.itemlist

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import com.example.jbchretreatstore.bookstore.domain.model.CheckoutItem
import com.example.jbchretreatstore.bookstore.domain.model.DisplayItem
import com.example.jbchretreatstore.bookstore.presentation.BookStoreIntent
import com.example.jbchretreatstore.bookstore.presentation.BookStoreViewState
import com.example.jbchretreatstore.bookstore.presentation.model.AlertDialogType
import com.example.jbchretreatstore.core.presentation.SandYellow
import com.example.jbchretreatstore.core.presentation.UiConstants
import com.example.jbchretreatstore.core.presentation.UiConstants.itemViewCardColorElevation
import com.example.jbchretreatstore.core.presentation.UiConstants.spacing_m
import com.example.jbchretreatstore.core.presentation.White
import com.example.jbchretreatstore.core.presentation.toPriceFormatString
import jbchretreatstore.composeapp.generated.resources.Res
import jbchretreatstore.composeapp.generated.resources.add_to_cart
import jbchretreatstore.composeapp.generated.resources.item_option_menu_selection_hint
import jbchretreatstore.composeapp.generated.resources.item_price_per_unit
import jbchretreatstore.composeapp.generated.resources.quantity_label
import jbchretreatstore.composeapp.generated.resources.total_price_text
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemView(
    state: BookStoreViewState,
    displayItem: DisplayItem,
    modifier: Modifier = Modifier,
    onUserIntent: (BookStoreIntent) -> Unit
) {
    var expanded by remember { mutableStateOf(true) }
    var checkoutItem by remember {
        mutableStateOf(
            CheckoutItem(
                itemName = displayItem.name,
                optionsMap = displayItem.options.associate {
                    it.optionKey to it.optionValueList.first()
                }.toMutableMap(),
                totalPrice = displayItem.price
            )
        )
    }

    if (state.displayRemoveDisplayItemDialog) {
        RemoveItemDialog(
            displayItem = displayItem,
            onUserIntent = onUserIntent
        )
    }

    ElevatedCard(
        elevation = CardDefaults.cardElevation(
            defaultElevation = itemViewCardColorElevation
        ),
        modifier = modifier.padding(horizontal = spacing_m)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
                .background(color = SandYellow.copy(alpha = UiConstants.itemViewCardColorAlpha))
                .animateContentSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().clickable { expanded = !expanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                ItemDescriptionView(
                    displayItem = displayItem
                ) {
                    expanded = !expanded
                }
                IconButton(
                    onClick = { onUserIntent(BookStoreIntent.OnUpdateDialogVisibility(
                        AlertDialogType.REMOVE_ITEM, true))}
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null)
                }
            }
            if (expanded) {
                ItemExpandableView(
                    displayItem = displayItem,
                    checkoutItem = checkoutItem,
                    updateCartItem = {
                        checkoutItem = it
                    },
                    onUserIntent = onUserIntent
                )
            }
        }
    }
}

@Composable
fun ItemDescriptionView(
    displayItem: DisplayItem,
    onItemClicked: () -> Unit = {},
) {
    Column(
        modifier = Modifier.padding(all = spacing_m)
            .clickable { onItemClicked.invoke() }
    ) {
        Text(
            text = displayItem.name,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleMedium
        )

        displayItem.options.forEach { option ->
            ItemOptionDescription(option)
        }

        Text(
            text = stringResource(
                Res.string.item_price_per_unit,
                displayItem.price.toPriceFormatString()
            ),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@Composable
fun ItemOptionDescription(option: DisplayItem.Option) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "${option.optionKey}: ",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.labelLarge
        )
        FlowRow(
            verticalArrangement = Arrangement.Center
        ) {
            option.optionValueList.forEachIndexed { index, value ->
                Text(
                    text = if (index == option.optionValueList.lastIndex) value else "$value, ",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
fun ItemExpandableView(
    displayItem: DisplayItem,
    checkoutItem: CheckoutItem,
    updateCartItem: (CheckoutItem) -> Unit,
    onUserIntent: (BookStoreIntent) -> Unit,
) {
    Column(
        modifier = Modifier.background(color = White).padding(
            all = spacing_m
        )
    ) {
        displayItem.options.forEachIndexed { index, option ->
            ItemOptionMenu(
                option = option,
                checkoutItem = checkoutItem,
                updateCartItem = updateCartItem
            )
            Spacer(Modifier.height(spacing_m))
        }

        QuantityStepper(
            displayItem = displayItem,
            checkoutItem = checkoutItem
        ) {
            updateCartItem.invoke(it)
        }

        Spacer(Modifier.height(spacing_m))

        AddToCartView(
            checkoutItem = checkoutItem,
            onUserIntent = onUserIntent,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemOptionMenu(
    option: DisplayItem.Option,
    checkoutItem: CheckoutItem,
    modifier: Modifier = Modifier,
    updateCartItem: (CheckoutItem) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier.fillMaxWidth()
    ) {
        TextField(
            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryEditable, true).fillMaxWidth(),
            value = checkoutItem.optionsMap[option.optionKey] ?: option.optionValueList.first(),
            onValueChange = { },
            label = {
                Text(
                    stringResource(
                        Res.string.item_option_menu_selection_hint,
                        option.optionKey
                    )
                )
            },
            readOnly = true,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            }
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            option.optionValueList.forEach { value ->
                DropdownMenuItem(
                    text = { Text(value) },
                    onClick = {
                        val updatedOptions = checkoutItem.optionsMap.toMutableMap()
                        updatedOptions[option.optionKey] = value

                        updateCartItem.invoke(
                            checkoutItem.copy(optionsMap = updatedOptions.toMap())
                        )
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun QuantityStepper(
    displayItem: DisplayItem,
    checkoutItem: CheckoutItem,
    updateCartItem: (CheckoutItem) -> Unit
) {
    TextField(
        modifier = Modifier.fillMaxWidth(),
        value = checkoutItem.quantity.toString(),
        onValueChange = { },
        readOnly = true,
        label = { Text(text = stringResource(Res.string.quantity_label)) },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done
        ),
        trailingIcon = {
            Row {
                IconButton(
                    onClick = {
                        val newQuantity = checkoutItem.quantity + 1
                        updateCartItem.invoke(
                            checkoutItem.copy(
                                quantity = newQuantity,
                                totalPrice = displayItem.price * newQuantity
                            )
                        )
                    }
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                }

                IconButton(
                    onClick = {
                        if (checkoutItem.quantity <= 1) return@IconButton
                        val newQuantity = checkoutItem.quantity - 1
                        updateCartItem.invoke(
                            checkoutItem.copy(
                                quantity = newQuantity,
                                totalPrice = displayItem.price * newQuantity
                            )
                        )
                    }
                ) {
                    Icon(Icons.Filled.Remove, contentDescription = null)
                }
            }
        }
    )
}

@Composable
fun AddToCartView(
    checkoutItem: CheckoutItem,
    onUserIntent: (BookStoreIntent) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.weight(.4f),
            text = stringResource(
                Res.string.total_price_text,
                checkoutItem.totalPrice
            ),
            style = MaterialTheme.typography.titleMedium,
        )

        Button(
            modifier = Modifier.weight(.6f),
            onClick = {
                onUserIntent(BookStoreIntent.OnAddToCheckoutItem(checkoutItem))
            },
        ) {
            Icon(
                Icons.Filled.AddShoppingCart,
                contentDescription = stringResource(Res.string.add_to_cart), // Important for accessibility
                modifier = Modifier.padding(end = ButtonDefaults.IconSpacing)
            )
            Text(stringResource(Res.string.add_to_cart))
        }
    }
}

@Preview
@Composable
fun ItemViewPreview() {
    Box(
        modifier = Modifier.background(color = White)
    ) {
        ItemView(
            state = BookStoreViewState(),
            displayItem = DisplayItem(
                price = 40.00,
                name = "Bible",
                options = listOf(
                    DisplayItem.Option(
                        optionKey = "Language",
                        optionValueList = listOf("English", "French", "Spanish")
                    ),
                    DisplayItem.Option(
                        optionKey = "Version",
                        optionValueList = listOf("KJV", "NKJV", "NIV")
                    ),
                ),
                isInCart = false
            )
        ) {}
    }
}