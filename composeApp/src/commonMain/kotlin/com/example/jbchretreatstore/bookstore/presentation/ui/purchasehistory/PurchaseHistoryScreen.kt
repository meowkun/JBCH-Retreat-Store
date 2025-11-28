package com.example.jbchretreatstore.bookstore.presentation.ui.purchasehistory

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.jbchretreatstore.bookstore.domain.model.CheckoutItem
import com.example.jbchretreatstore.bookstore.domain.model.CheckoutStatus
import com.example.jbchretreatstore.bookstore.domain.model.ReceiptData
import com.example.jbchretreatstore.bookstore.presentation.BookStoreIntent
import com.example.jbchretreatstore.bookstore.presentation.BookStoreViewState
import com.example.jbchretreatstore.bookstore.presentation.ui.components.TitleView
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.BookStoreTheme
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.Dimensions
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.LightBlue
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.Shapes
import jbchretreatstore.composeapp.generated.resources.Res
import jbchretreatstore.composeapp.generated.resources.purchase_history_view_title
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun PurchaseHistoryScreen(
    state: BookStoreViewState,
    onUserIntent: (BookStoreIntent) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth().statusBarsPadding(),
        color = LightBlue,
        shape = Shapes.topRounded
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            TitleView(stringResource(Res.string.purchase_history_view_title))
            LazyColumn(
                modifier = Modifier.padding(top = Dimensions.spacing_m).weight(1f),
                verticalArrangement = Arrangement.spacedBy(Dimensions.spacing_m),
            ) {
                items(
                    items = state.purchasedHistory,
                    key = { "${it.id}_${it.dateTime}" }) { item ->
                    PurchaseHistoryItemView(
                        receipt = item,
                        onUserIntent = onUserIntent
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun PurchaseHistoryScreenPreview() {
    BookStoreTheme {
        PurchaseHistoryScreen(
            state = BookStoreViewState(
            receiptList = listOf(
                ReceiptData(
                    buyerName = "Isaac",
                    checkoutList = listOf(
                        CheckoutItem(itemName = "Bible", totalPrice = 40.0),
                        CheckoutItem(itemName = "T-shirt", totalPrice = 15.0)
                    ),
                    checkoutStatus = CheckoutStatus.CHECKED_OUT
                )
            )
        ),
            onUserIntent = {}
        )
    }
}