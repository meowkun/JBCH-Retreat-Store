package com.example.jbchretreatstore.bookstore.presentation.itemlist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.jbchretreatstore.bookstore.domain.Item
import com.example.jbchretreatstore.bookstore.presentation.itemlist.components.ItemSearchBar
import com.example.jbchretreatstore.core.presentation.DarkBlue
import com.example.jbchretreatstore.core.presentation.Spacing
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ItemListScreenRoot(
    viewModel: ItemListViewModel = koinViewModel(),
    onItemClick: (Item) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    ItemListScreen(
        state = state,
        onAction = { action ->
            when (action) {
                is ItemListAction.OnIteClick -> onItemClick(action.item)
                else -> Unit
            }
            viewModel.onAction(action)
        }
    )
}

@Composable
private fun ItemListScreen(
    state: ItemListState,
    onAction: (ItemListAction) -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
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
                .widthIn(max = Spacing.searchBarMaxWidth)
                .fillMaxWidth()
                .padding(Spacing.m)
        )
    }
}

@Preview
@Composable
fun ItemListScreenPreview() {
    ItemListScreen(
        state = ItemListState(),
        onAction = {}
    )
}