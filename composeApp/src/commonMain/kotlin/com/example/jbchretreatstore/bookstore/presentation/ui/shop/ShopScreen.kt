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
    val onIntent: (ShopIntent) -> Unit = viewModel::handleIntent

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
                    onSearchQueryChange = { onIntent(ShopIntent.UpdateSearchQuery(it)) }
                )
                ItemListView(
                    modifier = Modifier
                        .weight(1f)
                        .padding(Dimensions.spacing_m),
                    displayItemList = uiState.searchedItemList,
                    onAddToCart = { onIntent(ShopIntent.AddToCart(it)) },
                    onDeleteItem = { onIntent(ShopIntent.ShowRemoveItemDialog(true, it)) },
                    onEditItem = { onIntent(ShopIntent.ShowEditItemDialog(true, it)) }
                )
            }

            // Bottom gradient overlay - transparent to white
            BottomGradientOverlay(modifier = Modifier.align(Alignment.BottomCenter))
        }
    }

    // Show add item dialog
    if (uiState.showAddItemDialog) {
        AddItemDialog(
            onDismiss = { onIntent(ShopIntent.ShowAddItemDialog(false)) },
            onAddItem = { newItem -> onIntent(ShopIntent.AddDisplayItem(newItem)) }
        )
    }

    // Show remove item dialog
    if (uiState.showRemoveItemDialog && uiState.itemToRemove != null) {
        RemoveItemDialog(
            displayItem = uiState.itemToRemove!!,
            onDismiss = { onIntent(ShopIntent.ShowRemoveItemDialog(false)) },
            onConfirm = {
                onIntent(ShopIntent.DeleteDisplayItem(uiState.itemToRemove!!))
            }
        )
    }

    // Show edit item dialog
    if (uiState.showEditItemDialog && uiState.itemToEdit != null) {
        AddItemDialog(
            onDismiss = { onIntent(ShopIntent.ShowEditItemDialog(false)) },
            onAddItem = { updatedItem -> onIntent(ShopIntent.UpdateDisplayItem(updatedItem)) },
            initialItem = uiState.itemToEdit
        )
    }
}

