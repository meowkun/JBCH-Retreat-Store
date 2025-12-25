package com.example.jbchretreatstore.bookstore.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.BookStoreTheme
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.Dimensions
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.Primary
import jbchretreatstore.composeapp.generated.resources.Res
import jbchretreatstore.composeapp.generated.resources.cart_icon_description
import jbchretreatstore.composeapp.generated.resources.cart_item_count_format
import jbchretreatstore.composeapp.generated.resources.check_cart_button
import jbchretreatstore.composeapp.generated.resources.ic_cart
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun CheckCartButton(
    itemCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = Primary
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = Dimensions.elevation_l
        )
    ) {
        Row(
            modifier = Modifier.wrapContentWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_cart),
                contentDescription = stringResource(Res.string.cart_icon_description),
                modifier = Modifier.size(Dimensions.spacing_xxl)
            )
            Spacer(modifier = Modifier.width(Dimensions.spacing_xs))
            Text(
                style = MaterialTheme.typography.titleLarge,
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.ExtraBold)) {
                        append(stringResource(Res.string.check_cart_button))
                    }
                    append(stringResource(Res.string.cart_item_count_format, itemCount))
                }
            )
        }
    }
}

@Preview
@Composable
fun CheckoutButtonPreview() {
    BookStoreTheme {
        CheckCartButton(
            itemCount = 5,
            onClick = { }
        )
    }
}
