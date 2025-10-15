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
import com.example.jbchretreatstore.bookstore.domain.model.DisplayItem
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
        state = state,
        onAction = { action ->
            when (action) {
                is ItemListAction.OnIteClick -> onItemClick(action.displayItem)
                is ItemListAction.OnAddNewItem -> onItemClick(action.newItem)
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
                    displayItemList = state.displayItemList,
                    onRemoveItem = { item ->
                        onAction(ItemListAction.onRemoveItem(item))
                    }
                )
            }
        }
        if (showDialog) {
            AddItemDialog(
                onDismiss = { showDialog = false },
                onConfirm = {
                    showDialog = false
                }
            ) { newItem ->
                onAction(ItemListAction.OnAddNewItem(newItem))
            }
        }
    }
}

@Preview
@Composable
fun ItemListScreenPreview() {
    ItemListScreen(
        state = ItemListState(
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
            )
        ),
        onAction = {}
    )
}