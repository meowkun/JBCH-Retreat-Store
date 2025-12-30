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
import com.example.jbchretreatstore.bookstore.presentation.ui.bottomsheet.AddItemBottomSheet
import com.example.jbchretreatstore.bookstore.presentation.ui.components.BottomGradientOverlay
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
                    onEditItem = { onIntent(ShopIntent.ShowEditItemBottomSheet(true, it)) },
                    onReorderItems = { onIntent(ShopIntent.ReorderDisplayItems(it)) },
                    isReorderEnabled = uiState.isReorderEnabled
                )
            }

            // Bottom gradient overlay - transparent to white
            BottomGradientOverlay(modifier = Modifier.align(Alignment.BottomCenter))
        }
    }

    // Show add item bottom sheet
    if (uiState.showAddItemBottomSheet) {
        AddItemBottomSheet(
            onDismiss = { onIntent(ShopIntent.ShowAddItemBottomSheet(false)) },
            onAddItem = { newItem -> onIntent(ShopIntent.AddDisplayItem(newItem)) }
        )
    }

    // Show remove item dialog
    uiState.removeDialogData?.let { item ->
        RemoveItemDialog(
            displayItem = item,
            onDismiss = { onIntent(ShopIntent.ShowRemoveItemDialog(false)) },
            onConfirm = { onIntent(ShopIntent.DeleteDisplayItem(item)) }
        )
    }

    // Show edit item bottom sheet
    uiState.editBottomSheetData?.let { item ->
        AddItemBottomSheet(
            onDismiss = { onIntent(ShopIntent.ShowEditItemBottomSheet(false)) },
            onAddItem = { updatedItem -> onIntent(ShopIntent.UpdateDisplayItem(updatedItem)) },
            initialItem = item
        )
    }
}

