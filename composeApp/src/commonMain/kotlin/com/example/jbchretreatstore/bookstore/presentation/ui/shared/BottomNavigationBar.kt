package com.example.jbchretreatstore.bookstore.presentation.ui.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import com.example.jbchretreatstore.bookstore.presentation.BookStoreIntent
import com.example.jbchretreatstore.bookstore.presentation.navigation.BookStoreNavDestination
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.BookStoreTheme
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.Dimensions
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.MediumBlue
import jbchretreatstore.composeapp.generated.resources.Res
import jbchretreatstore.composeapp.generated.resources.bottom_nav_receipt
import jbchretreatstore.composeapp.generated.resources.bottom_nav_shop
import jbchretreatstore.composeapp.generated.resources.ic_shop
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun BottomNavigationBar(
    currentDestination: BookStoreNavDestination,
    onUserIntent: (BookStoreIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    val cornerShape = RoundedCornerShape(
        Dimensions.corner_radius_full
    )

    Row(
        modifier = modifier
            .wrapContentSize()
            .shadow(
                elevation = Dimensions.elevation_l,
                shape = cornerShape
            )
            .clip(cornerShape)
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = cornerShape
            )
            .border(
                width = Dimensions.border_width_thin,
                color = MediumBlue,
                shape = cornerShape
            )
            .padding(
                horizontal = Dimensions.spacing_l,
                vertical = Dimensions.spacing_s
            ),
        horizontalArrangement = Arrangement.spacedBy(Dimensions.spacing_m),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CustomIconButton(
            painter = painterResource(Res.drawable.ic_shop),
            text = stringResource(Res.string.bottom_nav_shop),
            isSelected = currentDestination == BookStoreNavDestination.ItemListScreen,
            onClick = {
                onUserIntent(BookStoreIntent.OnNavigate(BookStoreNavDestination.ItemListScreen))
            }
        )

        CustomIconButton(
            icon = Icons.AutoMirrored.Default.ReceiptLong,
            text = stringResource(Res.string.bottom_nav_receipt),
            isSelected = currentDestination == BookStoreNavDestination.ReceiptScreen,
            onClick = {
                onUserIntent(BookStoreIntent.OnNavigate(BookStoreNavDestination.ReceiptScreen))
            }
        )
    }
}

@Preview
@Composable
fun BottomNavigationBarPreview() {
    BookStoreTheme {
        BottomNavigationBar(
            currentDestination = BookStoreNavDestination.ItemListScreen,
            onUserIntent = {}
        )
    }
}