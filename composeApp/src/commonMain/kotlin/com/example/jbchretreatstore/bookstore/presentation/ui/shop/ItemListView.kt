package com.example.jbchretreatstore.bookstore.presentation.ui.shop

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import com.example.jbchretreatstore.bookstore.domain.model.DisplayItem
import com.example.jbchretreatstore.bookstore.presentation.BookStoreIntent
import com.example.jbchretreatstore.bookstore.presentation.BookStoreViewState
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.BookStoreTheme
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.Dimensions
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
@Composable
fun ItemListView(
    modifier: Modifier = Modifier.fillMaxWidth(),
    displayItemList: List<DisplayItem>,
    onUserIntent: (BookStoreIntent) -> Unit,
    scrollState: LazyListState = rememberLazyListState(),
    state: BookStoreViewState
) {
    LazyColumn(
        modifier = modifier,
        state = scrollState,
        verticalArrangement = Arrangement.spacedBy(Dimensions.item_spacing),
        contentPadding = PaddingValues(bottom = Dimensions.spacing_xl)
    ) {
        items(items = displayItemList, key = { it.id }) { item ->
            ItemView(
                state = state,
                displayItem = item,
                modifier = Modifier.fillParentMaxWidth(),
                onUserIntent = onUserIntent
            )
        }
    }
}


@Preview
@Composable
fun ItemListViewPreview() {
    BookStoreTheme {
        ItemListView(
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
            modifier = Modifier.background(White),
            onUserIntent = { },
            state = BookStoreViewState()
        )
    }
}