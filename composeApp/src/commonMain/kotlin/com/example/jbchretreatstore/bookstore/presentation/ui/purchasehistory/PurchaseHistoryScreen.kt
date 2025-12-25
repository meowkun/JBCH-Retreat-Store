package com.example.jbchretreatstore.bookstore.presentation.ui.purchasehistory

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.jbchretreatstore.bookstore.domain.model.CheckoutItem
import com.example.jbchretreatstore.bookstore.domain.model.ReceiptData
import com.example.jbchretreatstore.bookstore.presentation.ui.components.TitleView
import com.example.jbchretreatstore.bookstore.presentation.ui.dialog.EditBuyerNameDialog
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.BookStoreTheme
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.DarkBlue
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.Dimensions
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.Secondary
import com.example.jbchretreatstore.bookstore.presentation.utils.toFormattedDateString
import jbchretreatstore.composeapp.generated.resources.Res
import jbchretreatstore.composeapp.generated.resources.checkout_view_item_total_price
import jbchretreatstore.composeapp.generated.resources.purchase_history_view_title
import kotlinx.coroutines.delay
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun PurchaseHistoryScreen(
    viewModel: PurchaseHistoryViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val onIntent: (PurchaseHistoryIntent) -> Unit = viewModel::handleIntent
    val listState = rememberLazyListState()
    val groupedReceipts = uiState.groupedReceipts

    // Track if user is scrolling
    var isScrolling by remember { mutableStateOf(false) }
    var showDateIndicator by remember { mutableStateOf(false) }

    // Detect scroll state
    val isListScrolling by remember {
        derivedStateOf { listState.isScrollInProgress }
    }

    // Show date indicator when scrolling
    LaunchedEffect(isListScrolling) {
        if (isListScrolling) {
            showDateIndicator = true
            isScrolling = true
        } else {
            isScrolling = false
            delay(500)
            if (!isScrolling) {
                showDateIndicator = false
            }
        }
    }

    // Get current visible date
    val currentVisibleDate by remember {
        derivedStateOf {
            if (groupedReceipts.isEmpty()) return@derivedStateOf null

            val firstVisibleIndex = listState.firstVisibleItemIndex
            var currentIndex = 0

            for ((date, receipts) in groupedReceipts) {
                if (firstVisibleIndex <= currentIndex) {
                    return@derivedStateOf date
                }
                currentIndex++
                currentIndex += receipts.size
            }

            groupedReceipts.lastOrNull()?.first
        }
    }

    Surface(modifier = Modifier.fillMaxWidth()) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                TitleView(
                    title = stringResource(Res.string.purchase_history_view_title),
                    onBackClick = onNavigateBack
                )

                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .padding(top = Dimensions.spacing_m)
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(Dimensions.spacing_m),
                    contentPadding = PaddingValues(bottom = Dimensions.gradient_overlay_height)
                ) {
                    groupedReceipts.forEach { (date, receipts) ->
                        item(key = date.headerKey) {
                            DateHeader(date = date)
                        }

                        items(
                            count = receipts.size,
                            key = { index -> receipts[index].uniqueKey }
                        ) { index ->
                            PurchaseHistoryItemView(
                                receipt = receipts[index],
                                onRemoveClick = { receipt ->
                                    onIntent(
                                        PurchaseHistoryIntent.ShowRemoveBottomSheet(
                                            true,
                                            receipt
                                        )
                                    )
                                },
                                onEditClick = { receipt, purchaseHistoryItem ->
                                    onIntent(
                                        PurchaseHistoryIntent.ShowEditBottomSheet(
                                        true,
                                        receipt,
                                        purchaseHistoryItem
                                        )
                                    )
                                },
                                onEditBuyerNameClick = { receipt ->
                                    onIntent(
                                        PurchaseHistoryIntent.ShowEditBuyerNameDialog(
                                            true,
                                            receipt
                                        )
                                    )
                                }
                            )
                        }
                    }
                }

                HorizontalDivider()
                Text(
                    modifier = Modifier.fillMaxWidth().padding(Dimensions.spacing_m),
                    textAlign = TextAlign.End,
                    text = stringResource(
                        Res.string.checkout_view_item_total_price,
                        uiState.formattedTotalAmount
                    ),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold
                    )
                )
            }

            // Date slider indicator on the right
            AnimatedVisibility(
                visible = showDateIndicator && currentVisibleDate != null,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(
                        bottom = Dimensions.date_slider_height,
                        end = Dimensions.spacing_m
                    )
            ) {
                currentVisibleDate?.let { date ->
                    DateSliderIndicator(date = date)
                }
            }
        }
    }

    // Remove confirmation bottom sheet
    uiState.receiptToRemove?.let { receipt ->
        if (uiState.showRemoveBottomSheet) {
            RemoveConfirmationBottomSheet(
                onDismiss = { onIntent(PurchaseHistoryIntent.ShowRemoveBottomSheet(false)) },
                onConfirm = { onIntent(PurchaseHistoryIntent.RemoveReceipt(receipt)) }
            )
        }
    }

    // Edit purchase history item bottom sheet
    uiState.editBottomSheetData?.let { (receipt, item) ->
        EditPurchaseHistoryItemBottomSheet(
            purchaseHistoryItem = item,
            onDismiss = { onIntent(PurchaseHistoryIntent.ShowEditBottomSheet(false)) },
            onSave = { updatedItem ->
                onIntent(
                    PurchaseHistoryIntent.UpdateCheckoutItem(
                        receipt = receipt,
                        originalItem = item,
                        updatedItem = updatedItem
                    )
                )
            }
        )
    }

    // Edit buyer name dialog
    uiState.editBuyerNameDialogData?.let { receipt ->
        EditBuyerNameDialog(
            currentBuyerName = receipt.buyerName,
            onDismiss = { onIntent(PurchaseHistoryIntent.ShowEditBuyerNameDialog(false)) },
            onSave = { newBuyerName ->
                onIntent(
                    PurchaseHistoryIntent.UpdateBuyerName(
                        receipt,
                        newBuyerName
                    )
                )
            }
        )
    }
}

@Composable
private fun DateHeader(date: LocalDate) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .padding(horizontal = Dimensions.spacing_s),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        HorizontalDivider(modifier = Modifier.weight(1f))
        Text(
            modifier = Modifier.padding(horizontal = Dimensions.spacing_s),
            text = date.toFormattedDateString(),
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        HorizontalDivider(modifier = Modifier.weight(1f))
    }
}

@Composable
private fun DateSliderIndicator(date: LocalDate) {
    Surface(
        modifier = Modifier.wrapContentSize(),
        shape = RoundedCornerShape(Dimensions.corner_radius_s),
        color = Secondary,
        shadowElevation = Dimensions.elevation_m
    ) {
        Text(
            text = date.toFormattedDateString(),
            modifier = Modifier.padding(
                horizontal = Dimensions.spacing_m,
                vertical = Dimensions.spacing_s
            ),
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            color = DarkBlue
        )
    }
}

@Preview
@Composable
private fun DateHeaderPreview() {
    BookStoreTheme {
        DateHeader(
            date = LocalDate(2025, 12, 7)
        )
    }
}

@Preview
@Composable
private fun DateSliderIndicatorPreview() {
    BookStoreTheme {
        DateSliderIndicator(
            date = LocalDate(2025, 12, 7)
        )
    }
}

@Preview
@Composable
private fun PurchaseHistoryItemViewPreview() {
    BookStoreTheme {
        PurchaseHistoryItemView(
            receipt = ReceiptData(
                buyerName = "John Doe",
                checkoutList = listOf(
                    CheckoutItem(
                        itemName = "Bible",
                        totalPrice = 40.0,
                        quantity = 2,
                        variants = listOf(
                            CheckoutItem.Variant(
                                "Language",
                                listOf("English", "Chinese"),
                                "English"
                            ),
                            CheckoutItem.Variant("Version", listOf("NIV", "KJV"), "NIV")
                        )
                    ),
                    CheckoutItem(
                        itemName = "T-shirt",
                        totalPrice = 15.0,
                        quantity = 1,
                        variants = listOf(
                            CheckoutItem.Variant("Size", listOf("S", "M", "L", "XL"), "L"),
                            CheckoutItem.Variant("Color", listOf("Red", "Blue", "Green"), "Blue")
                        )
                    )
                )
            )
        )
    }
}
