package com.example.jbchretreatstore.bookstore.presentation.ui.shop

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.jbchretreatstore.bookstore.presentation.ui.components.BottomGradientOverlay
import com.example.jbchretreatstore.bookstore.presentation.ui.dialog.AddItemDialog
import com.example.jbchretreatstore.bookstore.presentation.ui.dialog.RemoveItemDialog
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.Dimensions

@Composable
fun ShopScreen(
    viewModel: ShopViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

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
                    searchQuery = uiState.searchQuery,
                    onSearchQueryChange = { viewModel.onSearchQueryChange(it) }
                )
                ItemListView(
                    modifier = Modifier
                        .weight(1f)
                        .padding(Dimensions.spacing_m),
                    displayItemList = uiState.searchedItemList,
                    onAddToCart = { viewModel.onAddToCart(it) },
                    onDeleteItem = { viewModel.showRemoveItemDialog(true, it) },
                    onEditItem = { viewModel.showEditItemDialog(true, it) }
                )
            }

            // Bottom gradient overlay - transparent to white
            BottomGradientOverlay(modifier = Modifier.align(Alignment.BottomCenter))
        }
    }

    // Show add item dialog
    if (uiState.showAddItemDialog) {
        AddItemDialog(
            onDismiss = { viewModel.showAddItemDialog(false) },
            onAddItem = { newItem -> viewModel.onAddDisplayItem(newItem) }
        )
    }

    // Show remove item dialog
    if (uiState.showRemoveItemDialog && uiState.itemToRemove != null) {
        RemoveItemDialog(
            displayItem = uiState.itemToRemove!!,
            onDismiss = { viewModel.showRemoveItemDialog(false) },
            onConfirm = {
                viewModel.onDeleteDisplayItem(uiState.itemToRemove!!)
            }
        )
    }

    // Show edit item dialog
    if (uiState.showEditItemDialog && uiState.itemToEdit != null) {
        AddItemDialog(
            onDismiss = { viewModel.showEditItemDialog(false) },
            onAddItem = { updatedItem -> viewModel.onUpdateDisplayItem(updatedItem) },
            initialItem = uiState.itemToEdit
        )
    }
}

