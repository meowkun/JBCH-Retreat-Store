package com.example.jbchretreatstore.bookstore.presentation.ui.purchasehistory

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import com.example.jbchretreatstore.bookstore.presentation.BookStoreIntent
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.BookStoreTheme
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.Dimensions
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.MediumBlue
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.Shapes
import com.example.jbchretreatstore.bookstore.presentation.utils.toCurrency
import com.example.jbchretreatstore.bookstore.presentation.utils.toFormattedDateString
import jbchretreatstore.composeapp.generated.resources.Res
import jbchretreatstore.composeapp.generated.resources.checkout_view_item_price
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun PurchaseHistoryItemView(
    receipt: ReceiptData,
    onUserIntent: (BookStoreIntent) -> Unit = {}
) {
    var expanded by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(if (expanded) 180f else 0f)

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
            // Header: buyer name and summary (clickable to expand)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded },
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
                    // when collapsed we still want to show small summary below name
                    if (!expanded) {
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
                        contentDescription = if (expanded) "Collapse" else "Expand",
                        modifier = Modifier
                            .padding(Dimensions.spacing_s)
                            .rotate(rotation),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            if (expanded) {
                // show details when expanded
                HorizontalDivider()

                Text(
                    text = receipt.dateTime.toFormattedDateString(),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                )

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

                // Footer: payment method
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
        }
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
fun PurchaseHistoryItemViewPreview() {
    BookStoreTheme {
        PurchaseHistoryItemView(
            receipt = ReceiptData(
                buyerName = "Isaac",
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
                        variantsMap = mapOf("Size" to "L", "Color" to "Blue")
                    )
                )
            ),
            onUserIntent = {}
        )
    }
}
