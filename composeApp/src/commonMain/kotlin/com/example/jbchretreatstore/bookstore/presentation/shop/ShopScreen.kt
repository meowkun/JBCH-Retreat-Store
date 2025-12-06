package com.example.jbchretreatstore.bookstore.presentation.shop

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
import com.example.jbchretreatstore.bookstore.presentation.ui.shop.ItemListView
import com.example.jbchretreatstore.bookstore.presentation.ui.shop.ItemSearchBar
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
                    onDeleteItem = { viewModel.onDeleteDisplayItem(it) }
                )
            }

            // Bottom gradient overlay - transparent to white
            BottomGradientOverlay(modifier = Modifier.align(Alignment.BottomCenter))
        }
    }
}

