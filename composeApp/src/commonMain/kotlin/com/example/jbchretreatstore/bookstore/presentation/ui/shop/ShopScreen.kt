package com.example.jbchretreatstore.bookstore.presentation.ui.shop

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.jbchretreatstore.bookstore.domain.model.CheckoutItem
import com.example.jbchretreatstore.bookstore.domain.model.DisplayItem
import com.example.jbchretreatstore.bookstore.domain.model.ReceiptData
import com.example.jbchretreatstore.bookstore.presentation.BookStoreIntent
import com.example.jbchretreatstore.bookstore.presentation.BookStoreViewState
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.BookStoreTheme
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.Dimensions
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ShopScreen(
    state: BookStoreViewState,
    onUserIntent: (BookStoreIntent) -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
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
                        .padding(Dimensions.spacing_m),
                    displayItemList = state.searchedItemList,
                    onUserIntent = onUserIntent,
                    state = state
                )
            }

            // Bottom gradient overlay - transparent to white
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(Dimensions.gradient_overlay_height)
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            0.0f to Color.Transparent,
                            1.0f to Color.White
                        )
                    )
            )
        }
    }
}

@Preview
@Composable
fun ShopScreenPreview() {
    BookStoreTheme {
        ShopScreen(
            state = BookStoreViewState(
                displayItemList = listOf(
                    DisplayItem(
                        name = "Bible",
                        price = 40.00,
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
                    DisplayItem(
                        name = "T-shirt",
                        price = 15.00,
                        variants = listOf(
                            DisplayItem.Variant(
                                key = "Color",
                                valueList = listOf("Blue", "Black")
                            ),
                            DisplayItem.Variant(
                                key = "Size",
                                valueList = listOf("XS", "S", "M", "L", "XL", "XXL", "XXXL")
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