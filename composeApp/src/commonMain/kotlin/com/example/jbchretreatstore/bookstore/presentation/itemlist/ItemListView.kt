package com.example.jbchretreatstore.bookstore.presentation.itemlist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import com.example.jbchretreatstore.bookstore.domain.model.DisplayItem
import com.example.jbchretreatstore.core.presentation.UiConstants.spacing_m
import com.example.jbchretreatstore.core.presentation.UiConstants.spacing_xl
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ItemListView(
    modifier: Modifier = Modifier.fillMaxWidth()
        .padding(vertical = spacing_m),
    displayItemList: List<DisplayItem>,
    scrollState: LazyListState = rememberLazyListState()
) {
    LazyColumn(
        modifier = modifier,
        state = scrollState,
        verticalArrangement = Arrangement.spacedBy(spacing_m),
        contentPadding = PaddingValues(bottom = spacing_xl)
    ) {
        items(items = displayItemList, key = { it.id }) { item ->
            ItemView(
                displayItem = item,
                modifier = Modifier.fillParentMaxWidth()
                    .padding(horizontal = spacing_m),
            )
        }
    }
}

@Preview
@Composable
fun ItemListViewPreview() {
    ItemListView(
        displayItemList = listOf(
            DisplayItem(
                id = 0,
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
                id = 1,
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
        modifier = Modifier.background(White)
    )
}