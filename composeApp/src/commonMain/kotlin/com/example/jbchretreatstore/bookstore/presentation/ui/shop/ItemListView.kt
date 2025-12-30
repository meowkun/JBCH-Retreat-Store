package com.example.jbchretreatstore.bookstore.presentation.ui.shop

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import com.example.jbchretreatstore.bookstore.domain.model.CheckoutItem
import com.example.jbchretreatstore.bookstore.domain.model.DisplayItem
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.BookStoreTheme
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.Dimensions
import org.jetbrains.compose.ui.tooling.preview.Preview
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
@Composable
fun ItemListView(
    modifier: Modifier = Modifier.fillMaxWidth(),
    displayItemList: List<DisplayItem>,
    onAddToCart: (CheckoutItem) -> Unit,
    onDeleteItem: (DisplayItem) -> Unit,
    onEditItem: (DisplayItem) -> Unit,
    onReorderItems: (List<DisplayItem>) -> Unit,
    isReorderEnabled: Boolean = true
) {
    val hapticFeedback = LocalHapticFeedback.current
    val lazyListState = rememberLazyListState()

    // Local state to track reordered list during drag
    var localList by remember(displayItemList) { mutableStateOf(displayItemList) }

    val reorderableLazyListState = rememberReorderableLazyListState(lazyListState) { from, to ->
        if (isReorderEnabled) {
            localList = localList.toMutableList().apply {
                add(to.index, removeAt(from.index))
            }
            hapticFeedback.performHapticFeedback(HapticFeedbackType.SegmentFrequentTick)
        }
    }

    LazyColumn(
        modifier = modifier,
        state = lazyListState,
        verticalArrangement = Arrangement.SpaceBetween,
        contentPadding = PaddingValues(bottom = Dimensions.gradient_overlay_height)
    ) {
        items(items = localList, key = { it.uniqueKey }) { item ->
            ReorderableItem(
                reorderableLazyListState,
                key = item.uniqueKey,
                enabled = isReorderEnabled
            ) { isDragging ->
                val elevation by animateDpAsState(
                    if (isDragging) Dimensions.reorderable_item_drag_elevation else Dimensions.elevation_none
                )

                Surface(
                    shadowElevation = elevation,
                    shape = RoundedCornerShape(Dimensions.corner_radius_m)
                ) {
                    ItemView(
                        displayItem = item,
                        modifier = Modifier.fillParentMaxWidth(),
                        onAddToCart = onAddToCart,
                        onDeleteItem = onDeleteItem,
                        onEditItem = onEditItem,
                        dragHandleModifier = if (isReorderEnabled) {
                            Modifier.draggableHandle(
                                onDragStarted = {
                                    hapticFeedback.performHapticFeedback(
                                        HapticFeedbackType.GestureThresholdActivate
                                    )
                                },
                                onDragStopped = {
                                    hapticFeedback.performHapticFeedback(
                                        HapticFeedbackType.GestureEnd
                                    )
                                    // Only persist reorder when drag ends
                                    if (localList != displayItemList) {
                                        onReorderItems(localList)
                                    }
                                }
                            )
                        } else {
                            Modifier
                        }
                    )
                }
            }
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
                    variants = listOf(
                        DisplayItem.Variant(
                            key = "Language",
                            valueList = listOf("English", "French", "Spanish")
                        ),
                        DisplayItem.Variant(
                            key = "Version",
                            valueList = listOf("KJV", "NKJV", "NIV")
                        ),
                    )
                ),
                DisplayItem(
                    name = "T-shirt",
                    price = 15.00,
                    variants = listOf(
                        DisplayItem.Variant(
                            key = "Color",
                            valueList = listOf("Blue", "Black")
                        ),
                        DisplayItem.Variant(
                            key = "Size",
                            valueList = listOf("XS", "S", "M", "L", "XL", "XXL", "XXXL")
                        ),
                    )
                )
            ),
            modifier = Modifier.background(White),
            onAddToCart = {},
            onDeleteItem = {},
            onEditItem = {},
            onReorderItems = {}
        )
    }
}