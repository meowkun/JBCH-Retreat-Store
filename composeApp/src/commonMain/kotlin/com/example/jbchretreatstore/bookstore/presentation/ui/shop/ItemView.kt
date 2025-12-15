package com.example.jbchretreatstore.bookstore.presentation.ui.shop

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import com.example.jbchretreatstore.bookstore.domain.model.CheckoutItem
import com.example.jbchretreatstore.bookstore.domain.model.DisplayItem
import com.example.jbchretreatstore.bookstore.presentation.ui.components.Stepper
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.Black
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.BookStoreTheme
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.Dimensions
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.Dimensions.corner_radius_percent_l
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.MediumBlue
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.Shapes
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.White
import com.example.jbchretreatstore.bookstore.presentation.utils.toCurrency
import jbchretreatstore.composeapp.generated.resources.Res
import jbchretreatstore.composeapp.generated.resources.add_to_cart
import jbchretreatstore.composeapp.generated.resources.edit_item_button_description
import jbchretreatstore.composeapp.generated.resources.ic_chevron
import jbchretreatstore.composeapp.generated.resources.ic_chevron_small
import jbchretreatstore.composeapp.generated.resources.ic_trash_can
import jbchretreatstore.composeapp.generated.resources.item_option_menu_selection_hint
import jbchretreatstore.composeapp.generated.resources.item_price_per_unit
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * Saver for CheckoutItem to persist state across LazyColumn item disposal
 */
@OptIn(ExperimentalUuidApi::class)
private val CheckoutItemSaver: Saver<CheckoutItem, Any> = listSaver(
    save = { item ->
        listOf(
            item.id.toString(),
            item.itemName,
            item.quantity,
            item.variants.map { variant ->
                listOf(variant.key, variant.valueList, variant.selectedValue)
            },
            item.totalPrice
        )
    },
    restore = { list ->
        @Suppress("UNCHECKED_CAST")
        CheckoutItem(
            id = Uuid.parse(list[0] as String),
            itemName = list[1] as String,
            quantity = list[2] as Int,
            variants = (list[3] as List<List<Any>>).map { variantList ->
                CheckoutItem.Variant(
                    key = variantList[0] as String,
                    valueList = variantList[1] as List<String>,
                    selectedValue = variantList[2] as String
                )
            },
            totalPrice = list[4] as Double
        )
    }
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemView(
    displayItem: DisplayItem,
    modifier: Modifier = Modifier,
    onAddToCart: (CheckoutItem) -> Unit,
    onDeleteItem: (DisplayItem) -> Unit,
    onEditItem: (DisplayItem) -> Unit
) {
    var expanded by rememberSaveable { mutableStateOf(true) }
    var checkoutItem by rememberSaveable(
        stateSaver = CheckoutItemSaver
    ) {
        mutableStateOf(
            CheckoutItem(
                itemName = displayItem.name,
                variants = displayItem.variants.map { variant ->
                    CheckoutItem.Variant(
                        key = variant.key,
                        valueList = variant.valueList,
                        selectedValue = variant.valueList.firstOrNull() ?: ""
                    )
                },
                totalPrice = displayItem.price
            )
        )
    }


    ElevatedCard(
        shape = Shapes.itemCard,
        modifier = modifier
            .padding(horizontal = Dimensions.spacing_m)
            .border(
                width = Dimensions.border_width,
                color = MediumBlue,
                shape = Shapes.itemCard
            ).animateContentSize()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
                .background(color = MediumBlue),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            ItemDescriptionView(
                displayItem = displayItem,
                checkoutItem = checkoutItem,
                expanded = expanded
            ) {
                expanded = !expanded
            }

        }
        if (expanded) {
            HorizontalDivider(
                thickness = Dimensions.border_width,
                color = MediumBlue
            )
            ItemExpandableView(
                displayItem = displayItem,
                checkoutItem = checkoutItem,
                updateCartItem = {
                    checkoutItem = it
                },
                onAddToCart = onAddToCart,
                onDeleteClick = { onDeleteItem(displayItem) },
                onEditClick = { onEditItem(displayItem) }
            )
        }
    }
}

@Composable
fun ItemDescriptionView(
    displayItem: DisplayItem,
    checkoutItem: CheckoutItem,
    expanded: Boolean,
    onItemClicked: () -> Unit = {},
) {
    val rotationAngle by animateFloatAsState(
        targetValue = if (expanded) 0f else 180f
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onItemClicked.invoke()
            }
            .padding(all = Dimensions.spacing_m),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = displayItem.name,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.weight(1f)
        )

        PriceTag(displayItem.price)

        Image(
            painter = painterResource(Res.drawable.ic_chevron),
            contentDescription = null,
            modifier = Modifier
                .wrapContentSize()
                .padding(start = Dimensions.spacing_m)
                .rotate(rotationAngle)
        )
    }
}

@Composable
fun PriceTag(
    price: Double,
) {
    Box(
        modifier = Modifier
            .background(color = Black, shape = Shapes.priceTag)
    ) {
        Text(
            modifier = Modifier.padding(
                top = Dimensions.spacing_xs,
                bottom = Dimensions.spacing_xs,
                start = Dimensions.spacing_s,
                end = Dimensions.spacing_s
            ),
            text = stringResource(Res.string.item_price_per_unit, price.toCurrency()),
            style = MaterialTheme.typography.bodyMedium,
            color = White
        )
    }
}

@Composable
fun ItemExpandableView(
    displayItem: DisplayItem,
    checkoutItem: CheckoutItem,
    updateCartItem: (CheckoutItem) -> Unit,
    onAddToCart: (CheckoutItem) -> Unit,
    onDeleteClick: () -> Unit,
    onEditClick: () -> Unit
) {
    Column(
        modifier = Modifier.background(color = White).padding(
            all = Dimensions.spacing_m
        )
    ) {
        displayItem.variants.forEach { option ->
            ItemVariantMenu(
                variant = option,
                checkoutItem = checkoutItem,
                updateCartItem = updateCartItem
            )
            Spacer(Modifier.height(Dimensions.spacing_m))
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            QuantityStepper(
                displayItem = displayItem,
                checkoutItem = checkoutItem,
                updateCartItem = updateCartItem
            )

            IconButton(onClick = onEditClick) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = stringResource(Res.string.edit_item_button_description)
                )
            }
        }

        Spacer(Modifier.height(Dimensions.spacing_m))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onDeleteClick) {
                Image(
                    painter = painterResource(Res.drawable.ic_trash_can),
                    contentDescription = "Delete item"
                )
            }

            Button(
                shape = RoundedCornerShape(Dimensions.corner_radius_l),
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = Dimensions.button_height_l)
                    .padding(start = Dimensions.spacing_m),
                onClick = { onAddToCart(checkoutItem) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(stringResource(Res.string.add_to_cart))
                        }
                        append(" Total (${checkoutItem.totalPrice.toCurrency()})")
                    },
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemVariantMenu(
    variant: DisplayItem.Variant,
    checkoutItem: CheckoutItem,
    modifier: Modifier = Modifier,
    updateCartItem: (CheckoutItem) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val rotationAngle by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f
    )

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier.wrapContentSize()
    ) {
        OutlinedTextField(
            modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable, true)
                .wrapContentSize(),
            value = checkoutItem.variantsMap[variant.key] ?: variant.valueList.first(),
            onValueChange = { },
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Bold
            ),
            label = {
                Text(
                    stringResource(
                        Res.string.item_option_menu_selection_hint,
                        variant.key
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
            variant.valueList.forEach { value ->
                DropdownMenuItem(
                    text = {
                        Text(
                            value, style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    },
                    onClick = {
                        val updatedVariants = checkoutItem.variants.map { v ->
                            if (v.key == variant.key) {
                                v.copy(selectedValue = value)
                            } else v
                        }

                        updateCartItem.invoke(
                            checkoutItem.copy(variants = updatedVariants)
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
    Stepper(
        value = checkoutItem.quantity,
        onDecrement = { newQuantity ->
            updateCartItem.invoke(
                checkoutItem.copy(
                    quantity = newQuantity,
                    totalPrice = displayItem.price * newQuantity
                )
            )
        },
        onIncrement = { newQuantity ->
            updateCartItem.invoke(
                checkoutItem.copy(
                    quantity = newQuantity,
                    totalPrice = displayItem.price * newQuantity
                )
            )
        },
        minValue = 1
    )
}


@Preview(showBackground = true)
@Composable
fun PriceTagPreview() {
    BookStoreTheme {
        PriceTag(price = 45.99)
    }
}

@Preview(showBackground = true)
@Composable
fun ItemDescriptionViewPreview() {
    BookStoreTheme {
        ItemDescriptionView(
            displayItem = DisplayItem(
                name = "Holy Bible - NIV",
                price = 45.99,
                variants = listOf(
                    DisplayItem.Variant(
                        key = "Language",
                        valueList = listOf("English", "French", "Spanish")
                    )
                )
            ),
            checkoutItem = CheckoutItem(
                itemName = "Holy Bible - NIV",
                totalPrice = 45.99
            ),
            expanded = true,
            onItemClicked = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ItemVariantMenuPreview() {
    BookStoreTheme {
        ItemVariantMenu(
            variant = DisplayItem.Variant(
                key = "Size",
                valueList = listOf("S", "M", "L", "XL")
            ),
            checkoutItem = CheckoutItem(
                itemName = "T-Shirt",
                totalPrice = 25.00
            ),
            updateCartItem = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun QuantityStepperPreview() {
    BookStoreTheme {
        QuantityStepper(
            displayItem = DisplayItem(
                name = "Bible",
                price = 40.00
            ),
            checkoutItem = CheckoutItem(
                itemName = "Bible",
                quantity = 2,
                totalPrice = 80.00
            ),
            updateCartItem = {}
        )
    }
}

@Preview
@Composable
fun ItemViewPreview() {
    BookStoreTheme {
        Box(
            modifier = Modifier.background(color = White)
        ) {
            ItemView(
                displayItem = DisplayItem(
                    price = 40.00,
                    name = "Bible",
                    variants = listOf(
                        DisplayItem.Variant(
                            key = "Language",
                            valueList = listOf("English", "French", "Spanish")
                        ),
                        DisplayItem.Variant(
                            key = "Version",
                            valueList = listOf("KJV", "NKJV", "NIV")
                        ),
                    )
                ),
                onAddToCart = {},
                onDeleteItem = {},
                onEditItem = {}
            )
        }
    }
}