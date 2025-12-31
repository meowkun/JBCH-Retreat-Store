package com.example.jbchretreatstore.bookstore.presentation.ui.purchasehistory

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.jbchretreatstore.bookstore.domain.model.CheckoutItem
import com.example.jbchretreatstore.bookstore.domain.model.ReceiptData
import com.example.jbchretreatstore.bookstore.presentation.ui.bottomsheet.EditPurchaseHistoryItemBottomSheet
import com.example.jbchretreatstore.bookstore.presentation.ui.components.TitleView
import com.example.jbchretreatstore.bookstore.presentation.ui.dialog.EditBuyerNameDialog
import com.example.jbchretreatstore.bookstore.presentation.ui.dialog.EditMonthNameDialog
import com.example.jbchretreatstore.bookstore.presentation.ui.dialog.RemoveConfirmationDialog
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.BookStoreTheme
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.DarkBlue
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.Dimensions
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.MediumBlue
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.Secondary
import com.example.jbchretreatstore.bookstore.presentation.utils.toFormattedDateString
import jbchretreatstore.composeapp.generated.resources.Res
import jbchretreatstore.composeapp.generated.resources.checkout_view_item_total_price
import jbchretreatstore.composeapp.generated.resources.collapse_description
import jbchretreatstore.composeapp.generated.resources.expand_description
import jbchretreatstore.composeapp.generated.resources.purchase_history_edit_item_description
import jbchretreatstore.composeapp.generated.resources.purchase_history_month_group_receipts
import jbchretreatstore.composeapp.generated.resources.purchase_history_view_title
import kotlinx.coroutines.delay
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
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
    val groupedByMonth = uiState.groupedByMonth

    // Track if user is scrolling
    var isScrolling by remember { mutableStateOf(false) }
    var showMonthIndicator by remember { mutableStateOf(false) }

    // Detect scroll state
    val isListScrolling by remember {
        derivedStateOf { listState.isScrollInProgress }
    }

    // Show month indicator when scrolling
    LaunchedEffect(isListScrolling) {
        if (isListScrolling) {
            showMonthIndicator = true
            isScrolling = true
        } else {
            isScrolling = false
            delay(500)
            if (!isScrolling) {
                showMonthIndicator = false
            }
        }
    }

    // Get current visible date and month
    val currentVisibleInfo by remember {
        derivedStateOf {
            if (groupedByMonth.isEmpty()) return@derivedStateOf null

            val firstVisibleIndex = listState.firstVisibleItemIndex
            var currentIndex = 0

            for (monthGroup in groupedByMonth) {
                if (firstVisibleIndex <= currentIndex) {
                    return@derivedStateOf VisibleInfo(
                        monthGroup,
                        monthGroup.groupedByDate.firstOrNull()?.date
                    )
                }
                currentIndex++ // month header
                if (monthGroup.isExpanded) {
                    for (dateGroup in monthGroup.groupedByDate) {
                        if (firstVisibleIndex <= currentIndex) {
                            return@derivedStateOf VisibleInfo(monthGroup, dateGroup.date)
                        }
                        currentIndex++ // date header
                        currentIndex += dateGroup.receipts.size
                    }
                }
            }

            groupedByMonth.lastOrNull()?.let { lastMonth ->
                VisibleInfo(lastMonth, lastMonth.groupedByDate.lastOrNull()?.date)
            }
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
                    groupedByMonth.forEach { monthGroup ->
                        item(key = monthGroup.headerKey) {
                            MonthGroupHeader(
                                monthGroup = monthGroup,
                                onToggleExpand = {
                                    onIntent(PurchaseHistoryIntent.ToggleMonthExpanded(monthGroup.yearMonth))
                                },
                                onEditName = {
                                    onIntent(
                                        PurchaseHistoryIntent.ShowEditMonthNameDialog(
                                            true,
                                            monthGroup.yearMonth
                                        )
                                    )
                                }
                            )
                        }

                        if (monthGroup.isExpanded) {
                            monthGroup.groupedByDate.forEach { dateGroup ->
                                // Date header within month
                                item(key = "${monthGroup.yearMonth.key}_${dateGroup.date.headerKey}") {
                                    DateHeader(date = dateGroup.date)
                                }

                                // Receipts for this date
                                items(
                                    count = dateGroup.receipts.size,
                                    key = { index -> dateGroup.receipts[index].uniqueKey }
                                ) { index ->
                                    PurchaseHistoryItemView(
                                        receipt = dateGroup.receipts[index],
                                        onRemoveClick = { receipt ->
                                            onIntent(
                                                PurchaseHistoryIntent.ShowRemoveDialog(
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
            if (showMonthIndicator && currentVisibleInfo != null) {
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(
                            bottom = Dimensions.date_slider_height,
                            end = Dimensions.spacing_m
                        )
                ) {
                    currentVisibleInfo?.let { info ->
                        info.currentDate?.let { date ->
                            DateSliderIndicator(date = date)
                        } ?: MonthSliderIndicator(monthGroup = info.monthGroup)
                    }
                }
            }
        }
    }

    // Remove confirmation dialog
    uiState.receiptToRemove?.let { receipt ->
        if (uiState.showRemoveDialog) {
            RemoveConfirmationDialog(
                onDismiss = { onIntent(PurchaseHistoryIntent.ShowRemoveDialog(false)) },
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
            currentPaymentMethod = receipt.paymentMethod,
            onDismiss = { onIntent(PurchaseHistoryIntent.ShowEditBuyerNameDialog(false)) },
            onSave = { newBuyerName, newPaymentMethod ->
                onIntent(
                    PurchaseHistoryIntent.UpdateBuyerName(
                        receipt = receipt,
                        newBuyerName = newBuyerName,
                        newPaymentMethod = newPaymentMethod
                    )
                )
            }
        )
    }

    // Edit month name dialog
    uiState.editMonthNameDialogData?.let { (yearMonth, currentName) ->
        EditMonthNameDialog(
            yearMonth = yearMonth,
            currentName = currentName,
            onDismiss = { onIntent(PurchaseHistoryIntent.ShowEditMonthNameDialog(false)) },
            onSave = { newName ->
                onIntent(PurchaseHistoryIntent.UpdateMonthName(yearMonth, newName))
            }
        )
    }
}

/**
 * Data class to track currently visible month and date for the slider indicator
 */
private data class VisibleInfo(
    val monthGroup: MonthGroup,
    val currentDate: LocalDate?
)

/**
 * Month group header with expand/collapse and edit name functionality
 */
@Composable
private fun MonthGroupHeader(
    monthGroup: MonthGroup,
    onToggleExpand: () -> Unit,
    onEditName: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimensions.spacing_m),
        shape = RoundedCornerShape(Dimensions.corner_radius_m),
        color = MediumBlue
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onToggleExpand() }
                .padding(horizontal = Dimensions.spacing_m, vertical = Dimensions.spacing_s),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.ExpandMore,
                    contentDescription = if (monthGroup.isExpanded) {
                        stringResource(Res.string.collapse_description)
                    } else {
                        stringResource(Res.string.expand_description)
                    },
                    modifier = Modifier.rotate(if (monthGroup.isExpanded) 180f else 0f),
                    tint = DarkBlue
                )

                Spacer(modifier = Modifier.width(Dimensions.spacing_s))

                Column {
                    Text(
                        text = monthGroup.displayName,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = DarkBlue
                    )
                    Text(
                        text = stringResource(
                            Res.string.purchase_history_month_group_receipts,
                            monthGroup.receiptCount
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = DarkBlue.copy(alpha = 0.7f)
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = monthGroup.formattedTotalPrice,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = DarkBlue
                )

                IconButton(onClick = onEditName) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = stringResource(Res.string.purchase_history_edit_item_description),
                        tint = DarkBlue
                    )
                }
            }
        }
    }
}

/**
 * Month slider indicator shown when scrolling
 */
@Composable
private fun MonthSliderIndicator(monthGroup: MonthGroup) {
    Surface(
        modifier = Modifier.wrapContentSize(),
        shape = RoundedCornerShape(Dimensions.corner_radius_s),
        color = Secondary,
        shadowElevation = Dimensions.elevation_m
    ) {
        Text(
            text = monthGroup.displayName,
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
private fun MonthGroupHeaderExpandedPreview() {
    BookStoreTheme {
        MonthGroupHeader(
            monthGroup = MonthGroup(
                yearMonth = YearMonth(2025, Month.DECEMBER),
                receipts = listOf(
                    ReceiptData(buyerName = "John"),
                    ReceiptData(buyerName = "Jane")
                ),
                isExpanded = true
            ),
            onToggleExpand = {},
            onEditName = {}
        )
    }
}

@Preview
@Composable
private fun MonthGroupHeaderCollapsedPreview() {
    BookStoreTheme {
        MonthGroupHeader(
            monthGroup = MonthGroup(
                yearMonth = YearMonth(2025, Month.DECEMBER),
                customName = "Christmas Sales",
                receipts = listOf(
                    ReceiptData(buyerName = "John"),
                    ReceiptData(buyerName = "Jane")
                ),
                isExpanded = false
            ),
            onToggleExpand = {},
            onEditName = {}
        )
    }
}

@Preview
@Composable
private fun MonthSliderIndicatorPreview() {
    BookStoreTheme {
        MonthSliderIndicator(
            monthGroup = MonthGroup(
                yearMonth = YearMonth(2025, Month.DECEMBER),
                receipts = emptyList()
            )
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
                        unitPrice = 40.0,
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
                        unitPrice = 15.0,
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
