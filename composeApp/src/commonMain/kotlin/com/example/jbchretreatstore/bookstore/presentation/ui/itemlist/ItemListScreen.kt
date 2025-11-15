package com.example.jbchretreatstore.bookstore.presentation.ui.itemlist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.jbchretreatstore.bookstore.domain.model.CheckoutItem
import com.example.jbchretreatstore.bookstore.domain.model.DisplayItem
import com.example.jbchretreatstore.bookstore.domain.model.ReceiptData
import com.example.jbchretreatstore.bookstore.presentation.BookStoreIntent
import com.example.jbchretreatstore.bookstore.presentation.BookStoreViewState
import com.example.jbchretreatstore.core.presentation.DarkBlue
import com.example.jbchretreatstore.core.presentation.DesertWhite
import com.example.jbchretreatstore.core.presentation.UiConstants.itemListContainerRoundShape
import com.example.jbchretreatstore.core.presentation.UiConstants.spacing_m
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ItemListScreen(
    state: BookStoreViewState,
    onUserIntent: (BookStoreIntent) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBlue),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ItemSearchBar(
                modifier = Modifier
                    .weight(1f)
                    .padding(spacing_m),
                searchQuery = state.searchQuery,
                onUserIntent = onUserIntent
            )

            ViewShoppingCartIconButton(
                modifier = Modifier.padding(end = spacing_m),
                checkoutList = state.currentCheckoutList.checkoutList,
                onUserIntent = onUserIntent
            )
        }

        Surface(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            color = DesertWhite,
            shape = RoundedCornerShape(
                topStart = itemListContainerRoundShape,
                topEnd = itemListContainerRoundShape
            )
        ) {
            ItemListView(
                modifier = Modifier.padding(
                    vertical = spacing_m
                ),
                displayItemList = state.searchedItemList,
                onUserIntent = onUserIntent,
                state = state
            )
        }
    }
}

@Preview
@Composable
fun ItemListScreenPreview() {
    ItemListScreen(
        state = BookStoreViewState(
            displayItemList = listOf(
                DisplayItem(
                    name = "Bible",
                    price = 40.00,
                    options = listOf(
                        DisplayItem.Option(
                            optionKey = "Language",
                            optionValueList = listOf("English", "French", "Spanish")
                        ),
                        DisplayItem.Option(
                            optionKey = "Version",
                            optionValueList = listOf("KJV", "NKJV", "NIV")
                        ),
                    )
                ),
                DisplayItem(
                    name = "T-shirt",
                    price = 15.00,
                    options = listOf(
                        DisplayItem.Option(
                            optionKey = "Color",
                            optionValueList = listOf("Blue", "Black")
                        ),
                        DisplayItem.Option(
                            optionKey = "Size",
                            optionValueList = listOf("XS", "S", "M", "L", "XL", "XXL", "XXXL")
                        ),
                    )
                )
            ),
            currentCheckoutList = ReceiptData(
                checkoutList = listOf(
                    CheckoutItem(
                        itemName = "Bible",
                        totalPrice = 40.00,
                    ),
                    CheckoutItem(
                        itemName = "T-shirt",
                        totalPrice = 15.00,
                    )
                )
            )
        ),
        onUserIntent = {},
    )
}