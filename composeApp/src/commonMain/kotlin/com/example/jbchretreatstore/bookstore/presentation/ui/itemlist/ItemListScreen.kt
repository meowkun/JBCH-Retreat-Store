package com.example.jbchretreatstore.bookstore.presentation.ui.itemlist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.jbchretreatstore.bookstore.domain.model.CheckoutItem
import com.example.jbchretreatstore.bookstore.domain.model.DisplayItem
import com.example.jbchretreatstore.bookstore.presentation.viewmodel.BookStoreIntent
import com.example.jbchretreatstore.bookstore.presentation.viewmodel.BookStoreViewState
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
    var showDialog by remember { mutableStateOf(false) }
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
            }
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkBlue)
                .statusBarsPadding(),
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

                ViewCartIconButton(
                    modifier = Modifier.padding(end = spacing_m),
                    cartList = state.cartList,
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
                    onUserIntent = onUserIntent
                )
            }
        }
        if (showDialog) {
            AddItemDialog(
                onDismiss = { showDialog = false },
                onConfirm = {
                    showDialog = false
                },
                onUserIntent = onUserIntent
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
            cartList = listOf(
                CheckoutItem(
                    name = "Bible",
                    totalPrice = 40.00,
                ),
                CheckoutItem(
                    name = "T-shirt",
                    totalPrice = 15.00,
                )
            )
        ),
        onUserIntent = {},
    )
}