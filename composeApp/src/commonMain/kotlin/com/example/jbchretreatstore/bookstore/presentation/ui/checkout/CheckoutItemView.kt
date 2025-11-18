package com.example.jbchretreatstore.bookstore.presentation.ui.checkout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.jbchretreatstore.bookstore.domain.model.CheckoutItem
import com.example.jbchretreatstore.bookstore.presentation.BookStoreIntent
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.BookStoreTheme
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.Dimensions
import com.example.jbchretreatstore.bookstore.presentation.utils.toPriceFormatString
import jbchretreatstore.composeapp.generated.resources.Res
import jbchretreatstore.composeapp.generated.resources.checkout_view_item_price
import jbchretreatstore.composeapp.generated.resources.checkout_view_item_quantity
import jbchretreatstore.composeapp.generated.resources.checkout_view_item_remove
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun CheckoutItemView(
    checkoutItem: CheckoutItem,
    onUserIntent: (BookStoreIntent) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
            .padding(horizontal = Dimensions.spacing_m),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = checkoutItem.itemName,
                style = MaterialTheme.typography.titleLarge,
            )
            Text(
                text = stringResource(
                    Res.string.checkout_view_item_price,
                    checkoutItem.totalPrice.toPriceFormatString()
                ),
                style = MaterialTheme.typography.titleLarge,
            )
        }
        if (checkoutItem.optionsMap.isNotEmpty()) {
            Text(
                text = checkoutItem.optionsMap.entries.joinToString { "${it.key}: ${it.value}" },
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(
                    Res.string.checkout_view_item_quantity,
                    checkoutItem.quantity
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )

            TextButton(
                onClick = {
                    onUserIntent(BookStoreIntent.OnRemoveFromCheckoutItem(checkoutItem))
                }
            ) {
                Text(stringResource(Res.string.checkout_view_item_remove))
            }
        }

        HorizontalDivider()
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFFFFFFFF
)
@Composable
fun CheckoutItemViewPreview() {
    BookStoreTheme {
        CheckoutItemView(
            checkoutItem = CheckoutItem(
                itemName = "T-shirt",
                quantity = 2,
                totalPrice = 15.00,
                optionsMap = mutableMapOf("Size" to "M", "Color" to "Red")
            ),
        ) {}
    }
}