package com.example.jbchretreatstore.bookstore.presentation.ui.checkout

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.example.jbchretreatstore.bookstore.domain.model.CheckoutItem
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.BookStoreTheme
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.Dimensions
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.MediumBlue
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.Shapes
import com.example.jbchretreatstore.bookstore.presentation.utils.toCurrency
import jbchretreatstore.composeapp.generated.resources.Res
import jbchretreatstore.composeapp.generated.resources.checkout_view_item_quantity
import jbchretreatstore.composeapp.generated.resources.ic_trash_can
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun CheckoutItemView(
    checkoutItem: CheckoutItem,
    onRemoveItem: (CheckoutItem) -> Unit
) {
    OutlinedCard(
        shape = Shapes.itemCard,
        border = BorderStroke(Dimensions.border_width, MediumBlue),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimensions.spacing_m)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimensions.spacing_m),
            verticalArrangement = Arrangement.spacedBy(Dimensions.spacing_s)
        ) {
            // Item name and price row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = checkoutItem.itemName,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = checkoutItem.totalPrice.toCurrency(),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                )
            }

            // Variants display
            if (checkoutItem.variantsMap.isNotEmpty()) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(Dimensions.spacing_xxs)
                ) {
                    checkoutItem.variantsMap.forEach { (key, value) ->
                        Text(
                            text = "$key: $value",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            // Quantity and delete button row
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
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                )

                IconButton(onClick = { onRemoveItem(checkoutItem) }) {
                    Image(
                        painter = painterResource(Res.drawable.ic_trash_can),
                        contentDescription = "Delete item"
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CheckoutItemViewPreview() {
    BookStoreTheme {
        Column(
            modifier = Modifier.padding(vertical = Dimensions.spacing_m),
            verticalArrangement = Arrangement.spacedBy(Dimensions.spacing_m)
        ) {
            // Item with variants
            CheckoutItemView(
                checkoutItem = CheckoutItem(
                    itemName = "Christian T-Shirt - Faith",
                    quantity = 2,
                    totalPrice = 24.99,
                    variantsMap = mapOf(
                        "Size" to "Large",
                        "Color" to "Blue",
                        "Design" to "Cross"
                    )
                ),
                onRemoveItem = {}
            )

            // Simple item without variants
            CheckoutItemView(
                checkoutItem = CheckoutItem(
                    itemName = "Holy Bible - NIV",
                    quantity = 1,
                    totalPrice = 45.99,
                    variantsMap = emptyMap()
                ),
                onRemoveItem = {}
            )
        }
    }
}