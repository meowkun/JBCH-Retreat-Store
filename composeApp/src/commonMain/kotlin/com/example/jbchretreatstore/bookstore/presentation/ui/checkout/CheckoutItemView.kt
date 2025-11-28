package com.example.jbchretreatstore.bookstore.presentation.ui.checkout

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import com.example.jbchretreatstore.bookstore.presentation.BookStoreIntent
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.BookStoreTheme
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.Dimensions
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.MediumBlue
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.Shapes
import com.example.jbchretreatstore.bookstore.presentation.utils.toCurrency
import jbchretreatstore.composeapp.generated.resources.Res
import jbchretreatstore.composeapp.generated.resources.checkout_view_item_price
import jbchretreatstore.composeapp.generated.resources.checkout_view_item_quantity
import jbchretreatstore.composeapp.generated.resources.ic_trash_can
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun CheckoutItemView(
    checkoutItem: CheckoutItem,
    onUserIntent: (BookStoreIntent) -> Unit
) {
    OutlinedCard(
        shape = Shapes.itemCard,
        border = BorderStroke(Dimensions.border_width, MediumBlue),
        modifier = Modifier.fillMaxWidth().padding(horizontal = Dimensions.spacing_m)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
                .padding(Dimensions.spacing_m),
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
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                )
                Text(
                    text = stringResource(
                        Res.string.checkout_view_item_price,
                        checkoutItem.totalPrice.toCurrency()
                    ),
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                )
            }
            if (checkoutItem.variantsMap.isNotEmpty()) {
                Spacer(Modifier.height(Dimensions.spacing_m))
                Text(
                    text = checkoutItem.variantsMap.entries.joinToString { "${it.key}: ${it.value}" },
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
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
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                )

                IconButton(
                    onClick = {
                        onUserIntent(BookStoreIntent.OnRemoveFromCheckoutItem(checkoutItem))
                    }
                ) {
                    Image(
                        painter = painterResource(Res.drawable.ic_trash_can),
                        contentDescription = "Delete item"
                    )
                }
            }
        }
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
                variantsMap = mutableMapOf("Size" to "M", "Color" to "Red")
            ),
        ) {}
    }
}