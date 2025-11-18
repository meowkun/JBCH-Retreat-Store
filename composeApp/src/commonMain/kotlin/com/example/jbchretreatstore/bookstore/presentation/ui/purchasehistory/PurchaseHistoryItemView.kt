package com.example.jbchretreatstore.bookstore.presentation.ui.purchasehistory

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import com.example.jbchretreatstore.bookstore.domain.model.CheckoutItem
import com.example.jbchretreatstore.bookstore.domain.model.ReceiptData
import com.example.jbchretreatstore.bookstore.presentation.BookStoreIntent
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.BookStoreTheme
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.Dimensions
import com.example.jbchretreatstore.bookstore.presentation.utils.toFormattedDateString
import com.example.jbchretreatstore.bookstore.presentation.utils.toPriceFormatString
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

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimensions.spacing_m)
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
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = receipt.buyerName.ifBlank { "Unknown Buyer" },
                    style = MaterialTheme.typography.titleMedium,
                )
                // when collapsed we still want to show small summary below name
                if (!expanded) {
                    Text(
                        text = "${receipt.checkoutList.size} items",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Text(
                        text = receipt.dateTime.toFormattedDateString(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(
                        Res.string.checkout_view_item_price,
                        receipt.checkoutList.sumOf { it.totalPrice }.toPriceFormatString()
                    ),
                    style = MaterialTheme.typography.titleMedium,
                )
                Icon(
                    imageVector = Icons.Default.ExpandMore,
                    contentDescription = if (expanded) "Collapse" else "Expand",
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .rotate(rotation),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        if (expanded) {
            // show details when expanded
            HorizontalDivider()

            Text(
                text = receipt.dateTime.toString(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
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
                    style = MaterialTheme.typography.bodyLarge,
                )
                Text(
                    text = stringResource(
                        Res.string.checkout_view_item_price,
                        receipt.checkoutList.sumOf { it.totalPrice }.toPriceFormatString()
                    ),
                    style = MaterialTheme.typography.titleMedium,
                )
            }
        }

        // Bottom divider
        HorizontalDivider()
    }
}

@Composable
private fun PurchaseHistoryCheckoutItem(item: CheckoutItem) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = item.itemName,
            style = MaterialTheme.typography.bodyLarge,
        )
        Text(
            text = stringResource(
                Res.string.checkout_view_item_price,
                item.totalPrice.toPriceFormatString()
            ),
            style = MaterialTheme.typography.bodyLarge,
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
                    CheckoutItem(itemName = "Bible", totalPrice = 40.0),
                    CheckoutItem(itemName = "T-shirt", totalPrice = 15.0)
                )
            ),
            onUserIntent = {}
        )
    }
}
