package com.example.jbchretreatstore.bookstore.presentation.itemlist.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.text.style.TextAlign
import com.example.jbchretreatstore.bookstore.domain.Item
import com.example.jbchretreatstore.core.presentation.LightBlue
import com.example.jbchretreatstore.core.presentation.UiConstants
import com.example.jbchretreatstore.core.presentation.UiConstants.divider_thickness
import com.example.jbchretreatstore.core.presentation.UiConstants.itemViewCardColorAlpha
import com.example.jbchretreatstore.core.presentation.UiConstants.itemViewCardColorElevation
import com.example.jbchretreatstore.core.presentation.UiConstants.spacer_weight
import jbchretreatstore.composeapp.generated.resources.Res
import jbchretreatstore.composeapp.generated.resources.clear_hint
import jbchretreatstore.composeapp.generated.resources.item_arrow_down_content_description
import jbchretreatstore.composeapp.generated.resources.item_arrow_up_content_description
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ItemView(
    item: Item,
    onClick: () -> Unit,
    modifier: Modifier = Modifier.background(White)
) {
    var expanded by remember { mutableStateOf(false) }
    ElevatedCard(
        elevation = CardDefaults.cardElevation(
            defaultElevation = itemViewCardColorElevation
        ),
        colors = CardDefaults.cardColors(
            containerColor = LightBlue.copy(alpha = itemViewCardColorAlpha)
        ),
        onClick = { expanded = !expanded },
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(UiConstants.spacing_m)
                .height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = item.iconVector ?: Icons.Default.BrokenImage,
                contentDescription = stringResource(Res.string.clear_hint),
                tint = MaterialTheme.colorScheme.onSurface,
            )
            VerticalDivider(
                modifier = Modifier.padding(horizontal = UiConstants.spacing_s),
                thickness = divider_thickness
            )
            ItemDescriptionView(item = item)
            Spacer(modifier = Modifier.weight(spacer_weight))
            Icon(
                imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = if (expanded)
                    stringResource(Res.string.item_arrow_up_content_description)
                else
                    stringResource(Res.string.item_arrow_down_content_description),
                tint = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@Composable
fun ItemDescriptionView(item: Item) {
    Column(
        modifier = Modifier,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = item.name,
            textAlign = TextAlign.Center,
        )
        Row(
            modifier = Modifier,
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "${item.options.optionKey}:",
                textAlign = TextAlign.Center,
            )
            FlowRow(
                modifier = Modifier
                    .padding(horizontal = UiConstants.spacing_xs)
            ) {
                item.options.optionValue.forEach { option ->
                    Text(
                        modifier = Modifier.padding(horizontal = UiConstants.spacing_xs),
                        text = option,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun ItemViewPreview() {
    ItemView(
        item = Item(
            name = "Bible",
            options = Item.Options(
                optionKey = "Language",
                optionValue = listOf("English", "French", "Spanish")
            )
        ),
        onClick = {},
    )
}