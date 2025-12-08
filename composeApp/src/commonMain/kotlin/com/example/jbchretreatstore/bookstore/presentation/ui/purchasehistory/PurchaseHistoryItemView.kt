package com.example.jbchretreatstore.bookstore.presentation.ui.purchasehistory

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import com.example.jbchretreatstore.bookstore.domain.model.CheckoutItem
import com.example.jbchretreatstore.bookstore.domain.model.ReceiptData
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.BookStoreTheme
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.Dimensions
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.MediumBlue
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.Shapes
import com.example.jbchretreatstore.bookstore.presentation.utils.toCurrency
import com.example.jbchretreatstore.bookstore.presentation.utils.toFormattedDateString
import jbchretreatstore.composeapp.generated.resources.Res
import jbchretreatstore.composeapp.generated.resources.checkout_view_item_price
import jbchretreatstore.composeapp.generated.resources.ic_trash_can
import jbchretreatstore.composeapp.generated.resources.purchase_history_remove_button_description
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PurchaseHistoryItemView(
    receipt: ReceiptData,
    onRemoveClick: (ReceiptData) -> Unit = {}
) {
    var expanded by remember { mutableStateOf(false) }

    OutlinedCard(
        shape = Shapes.itemCard,
        border = BorderStroke(Dimensions.border_width, MediumBlue),
        modifier = Modifier.fillMaxWidth().padding(horizontal = Dimensions.spacing_m)
            .animateContentSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimensions.spacing_m),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(Dimensions.spacing_m)
        ) {
            if (expanded) {
                PurchaseHistoryExpandedView(
                    receipt = receipt,
                    onCollapseClick = { expanded = false }
                )
            } else {
                PurchaseHistoryCollapsedView(
                    receipt = receipt,
                    onExpandClick = { expanded = true },
                    onRemoveClick = { onRemoveClick(receipt) }
                )
            }
        }
    }
}

/**
 * Collapsed view showing buyer name, item count, date and total price
 */
@Composable
private fun PurchaseHistoryCollapsedView(
    receipt: ReceiptData,
    onExpandClick: () -> Unit,
    onRemoveClick: () -> Unit
) {
    val rotation by animateFloatAsState(0f)

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onExpandClick() },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column {
                Text(
                    text = receipt.buyerName.ifBlank { "Unknown Buyer" },
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                )
                Spacer(modifier = Modifier.height(Dimensions.spacing_s))
                Text(
                    text = "${receipt.checkoutList.size} items",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                )
                Spacer(modifier = Modifier.height(Dimensions.spacing_s))
                Text(
                    text = receipt.dateTime.toFormattedDateString(),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = receipt.checkoutList.sumOf { it.totalPrice }.toCurrency(),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                )
                Icon(
                    imageVector = Icons.Default.ExpandMore,
                    contentDescription = "Expand",
                    modifier = Modifier
                        .padding(Dimensions.spacing_s)
                        .rotate(rotation),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        // Remove button at bottom right
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(onClick = onRemoveClick) {
                Image(
                    painter = painterResource(Res.drawable.ic_trash_can),
                    contentDescription = stringResource(Res.string.purchase_history_remove_button_description)
                )
            }
        }
    }
}

/**
 * Expanded view showing full receipt details with all items
 */
@Composable
private fun PurchaseHistoryExpandedView(
    receipt: ReceiptData,
    onCollapseClick: () -> Unit
) {
    val rotation by animateFloatAsState(180f)

    // Header: buyer name and total (clickable to collapse)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCollapseClick() },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = receipt.buyerName.ifBlank { "Unknown Buyer" },
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold
            ),
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = receipt.checkoutList.sumOf { it.totalPrice }.toCurrency(),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
            )
            Icon(
                imageVector = Icons.Default.ExpandMore,
                contentDescription = "Collapse",
                modifier = Modifier
                    .padding(Dimensions.spacing_s)
                    .rotate(rotation),
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }

    HorizontalDivider()

    // Date
    Text(
        text = receipt.dateTime.toFormattedDateString(),
        style = MaterialTheme.typography.bodyMedium.copy(
            fontWeight = FontWeight.Bold
        ),
    )

    // Items list
    if (receipt.checkoutList.isEmpty()) {
        Text(
            text = "No items",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    } else {
        receipt.checkoutList.forEach { item ->
            PurchaseHistoryCheckoutItem(item)
        }
    }

    HorizontalDivider()

    // Footer: payment method and total
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = receipt.paymentMethod.methodName,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Bold
            ),
        )
        Text(
            text = stringResource(
                Res.string.checkout_view_item_price,
                receipt.checkoutList.sumOf { it.totalPrice }.toCurrency()
            ),
            style = MaterialTheme.typography.titleMedium,
        )
    }
}

@Composable
private fun PurchaseHistoryCheckoutItem(item: CheckoutItem) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(Dimensions.spacing_xs)
        ) {
            Text(
                text = item.itemName,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
            )

            // Display variants if they exist
            if (item.variantsMap.isNotEmpty()) {
                item.variantsMap.forEach { (key, value) ->
                    Text(
                        text = "$key: $value",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }

            Text(
                text = "Quantity: ${item.quantity}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }

        Text(
            text = stringResource(
                Res.string.checkout_view_item_price,
                item.totalPrice.toCurrency()
            ),
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Bold
            ),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PurchaseHistoryCollapsedViewPreview() {
    BookStoreTheme {
        OutlinedCard(
            shape = Shapes.itemCard,
            border = BorderStroke(Dimensions.border_width, MediumBlue),
            modifier = Modifier.fillMaxWidth().padding(Dimensions.spacing_m)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(Dimensions.spacing_m)
            ) {
                PurchaseHistoryCollapsedView(
                    receipt = ReceiptData(
                        buyerName = "John Doe",
                        checkoutList = listOf(
                            CheckoutItem(
                                itemName = "Bible",
                                totalPrice = 40.0,
                                quantity = 2,
                                variantsMap = mapOf("Language" to "English")
                            ),
                            CheckoutItem(
                                itemName = "T-shirt",
                                totalPrice = 15.0,
                                quantity = 1,
                                variantsMap = mapOf("Size" to "L")
                            )
                        )
                    ),
                    onExpandClick = {},
                    onRemoveClick = {}
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PurchaseHistoryExpandedViewPreview() {
    BookStoreTheme {
        OutlinedCard(
            shape = Shapes.itemCard,
            border = BorderStroke(Dimensions.border_width, MediumBlue),
            modifier = Modifier.fillMaxWidth().padding(Dimensions.spacing_m)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(Dimensions.spacing_m),
                verticalArrangement = Arrangement.spacedBy(Dimensions.spacing_m)
            ) {
                PurchaseHistoryExpandedView(
                    receipt = ReceiptData(
                        buyerName = "John Doe",
                        checkoutList = listOf(
                            CheckoutItem(
                                itemName = "Bible",
                                totalPrice = 40.0,
                                quantity = 2,
                                variantsMap = mapOf("Language" to "English", "Version" to "NIV")
                            ),
                            CheckoutItem(
                                itemName = "T-shirt",
                                totalPrice = 15.0,
                                quantity = 1,
                                variantsMap = mapOf("Size" to "L", "Color" to "Blue")
                            )
                        )
                    ),
                    onCollapseClick = {}
                )
            }
        }
    }
}
