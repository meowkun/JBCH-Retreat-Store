package com.example.jbchretreatstore.bookstore.presentation.ui.itemlist

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.jbchretreatstore.bookstore.domain.model.CheckoutItem
import com.example.jbchretreatstore.bookstore.domain.model.DisplayItem
import com.example.jbchretreatstore.bookstore.domain.model.ReceiptData
import com.example.jbchretreatstore.bookstore.presentation.BookStoreIntent
import com.example.jbchretreatstore.bookstore.presentation.BookStoreViewState
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.BookStoreTheme
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.Dimensions
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ItemListScreen(
    state: BookStoreViewState,
    onUserIntent: (BookStoreIntent) -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,

            ) {
            ItemSearchBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimensions.spacing_m),
                searchQuery = state.searchQuery,
                onUserIntent = onUserIntent
            )
            ItemListView(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = Dimensions.spacing_m),
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
    BookStoreTheme {
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
}