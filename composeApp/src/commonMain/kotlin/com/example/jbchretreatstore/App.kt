package com.example.jbchretreatstore

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.example.jbchretreatstore.bookstore.presentation.itemlist.ItemListScreenRoot
import com.example.jbchretreatstore.bookstore.presentation.itemlist.ItemListViewModel
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    ItemListScreenRoot(
        viewModel = remember { ItemListViewModel() },
        onItemClick = { item -> },
    )
}