package com.example.jbchretreatstore.bookstore.presentation.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shop
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.BookStoreTheme
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.Dimensions.spacing_l
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.Dimensions.spacing_s
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.Dimensions.spacing_xs
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.Primary
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.UnselectedIconColor
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun CustomIconButton(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    selectedColor: Color = Primary,
    unselectedColor: Color = UnselectedIconColor,
    iconSize: Dp = spacing_l,
    spacing: Dp = spacing_xs
) {
    CustomIconButtonContent(
        text = text,
        onClick = onClick,
        modifier = modifier,
        isSelected = isSelected,
        selectedColor = selectedColor,
        unselectedColor = unselectedColor,
        spacing = spacing
    ) { color ->
        Icon(
            imageVector = icon,
            contentDescription = text,
            tint = color,
            modifier = Modifier.size(iconSize)
        )
    }
}

@Composable
fun CustomIconButton(
    painter: Painter,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    selectedColor: Color = Primary,
    unselectedColor: Color = UnselectedIconColor,
    iconSize: Dp = spacing_l,
    spacing: Dp = spacing_xs
) {
    CustomIconButtonContent(
        text = text,
        onClick = onClick,
        modifier = modifier,
        isSelected = isSelected,
        selectedColor = selectedColor,
        unselectedColor = unselectedColor,
        spacing = spacing
    ) { color ->
        Icon(
            painter = painter,
            contentDescription = text,
            tint = color,
            modifier = Modifier.size(iconSize)
        )
    }
}

@Composable
private fun CustomIconButtonContent(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier,
    isSelected: Boolean,
    selectedColor: Color,
    unselectedColor: Color,
    spacing: Dp,
    iconContent: @Composable (Color) -> Unit
) {
    val color = if (isSelected) selectedColor else unselectedColor

    Column(
        modifier = modifier
            .clickable(
                onClick = onClick,
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true)
            )
            .padding(spacing_s),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        iconContent(color)

        Spacer(modifier = Modifier.height(spacing))

        Text(
            text = text,
            color = color,
            style = MaterialTheme.typography.labelSmall,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Preview
@Composable
fun CustomIconButtonWithLongTextPreview() {
    BookStoreTheme {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            CustomIconButton(
                icon = Icons.Default.Shop,
                text = "Shop",
                isSelected = true,
                onClick = { }
            )

            CustomIconButton(
                icon = Icons.Default.Settings,
                text = "Settings",
                isSelected = false,
                onClick = { }
            )

            CustomIconButton(
                icon = Icons.Default.Home,
                text = "Very Long Button Name",
                isSelected = false,
                onClick = { }
            )
        }
    }
}
