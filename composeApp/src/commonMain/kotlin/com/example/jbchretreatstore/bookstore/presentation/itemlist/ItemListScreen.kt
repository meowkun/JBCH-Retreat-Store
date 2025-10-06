package com.example.jbchretreatstore.bookstore.presentation.itemlist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.jbchretreatstore.bookstore.domain.DisplayItem
import com.example.jbchretreatstore.bookstore.presentation.itemlist.components.AddItemDialog
import com.example.jbchretreatstore.bookstore.presentation.itemlist.components.ItemListView
import com.example.jbchretreatstore.bookstore.presentation.itemlist.components.ItemSearchBar
import com.example.jbchretreatstore.core.presentation.DarkBlue
import com.example.jbchretreatstore.core.presentation.DesertWhite
import com.example.jbchretreatstore.core.presentation.UiConstants.itemListContainerRoundShape
import com.example.jbchretreatstore.core.presentation.UiConstants.max_width
import com.example.jbchretreatstore.core.presentation.UiConstants.spacing_m
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ItemListScreenRoot(
    viewModel: ItemListViewModel = koinViewModel(),
    onItemClick: (DisplayItem) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    ItemListScreen(
        state = state.copy(
            searchResults = listOf(
                DisplayItem(
                    id = 0,
                    name = "Bible",
                    price = 40.00,
                    options = listOf(
                        DisplayItem.Option(
                            optionKey = "Language",
                            optionValue = listOf("English", "French", "Spanish")
                        ),
                        DisplayItem.Option(
                            optionKey = "Version",
                            optionValue = listOf("KJV", "NKJV", "NIV")
                        ),
                    )
                ),
                DisplayItem(
                    id = 1,
                    name = "T-shirt",
                    price = 15.00,
                    options = listOf(
                        DisplayItem.Option(
                            optionKey = "Color",
                            optionValue = listOf("Blue", "Black")
                        ),
                        DisplayItem.Option(
                            optionKey = "Size",
                            optionValue = listOf("XS", "S", "M", "L", "XL", "XXL", "XXXL")
                        ),
                    )
                )
            )
        ),
        onAction = { action ->
            when (action) {
                is ItemListAction.OnIteClick -> onItemClick(action.displayItem)
                else -> Unit
            }
            viewModel.onItemListAction(action)
        }
    )
}

@Composable
private fun ItemListScreen(
    state: ItemListState,
    onAction: (ItemListAction) -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    var showDialog by remember { mutableStateOf(false) }
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        },
    ) {
        if (showDialog) {
            AddItemDialog(
                onDismiss = { showDialog = false },
                onConfirm = { showDialog = false }
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkBlue)
                .statusBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ItemSearchBar(
                searchQuery = state.searchQuery,
                onSearchQueryChange = { query ->
                    onAction(ItemListAction.OnSearchQueryChange(query))
                },
                onItemSearch = {
                    keyboardController?.hide()
                },
                modifier = Modifier
                    .widthIn(max = max_width)
                    .fillMaxWidth()
                    .padding(spacing_m)
            )

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
                    displayItemList = state.searchResults
                )
            }
        }
    }
}

@Preview
@Composable
fun ItemListScreenPreview() {
    ItemListScreen(
        state = ItemListState(
            searchResults = listOf(
                DisplayItem(
                    id = 0,
                    name = "Bible",
                    price = 40.00,
                    options = listOf(
                        DisplayItem.Option(
                            optionKey = "Language",
                            optionValue = listOf("English", "French", "Spanish")
                        ),
                        DisplayItem.Option(
                            optionKey = "Version",
                            optionValue = listOf("KJV", "NKJV", "NIV")
                        ),
                    )
                ),
                DisplayItem(
                    id = 1,
                    name = "T-shirt",
                    price = 15.00,
                    options = listOf(
                        DisplayItem.Option(
                            optionKey = "Color",
                            optionValue = listOf("Blue", "Black")
                        ),
                        DisplayItem.Option(
                            optionKey = "Size",
                            optionValue = listOf("XS", "S", "M", "L", "XL", "XXL", "XXXL")
                        ),
                    )
                )
            )
        ),
        onAction = {}
    )
}