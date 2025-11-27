package com.example.jbchretreatstore.bookstore.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.BookStoreTheme
import jbchretreatstore.composeapp.generated.resources.Res
import jbchretreatstore.composeapp.generated.resources.close_dialog_description
import jbchretreatstore.composeapp.generated.resources.ic_back_arrow
import jbchretreatstore.composeapp.generated.resources.ic_close
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun DialogTitle(
    title: String,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
    showBackArrow: Boolean = false
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.ExtraBold
            )
        )
        IconButton(onClick = onClose) {
            Icon(
                painter = painterResource(
                    if (showBackArrow) Res.drawable.ic_back_arrow else Res.drawable.ic_close
                ),
                contentDescription = stringResource(Res.string.close_dialog_description)
            )
        }
    }
}

@Preview
@Composable
fun DialogTitleShortPreview() {
    BookStoreTheme {
        DialogTitle(
            title = "Edit",
            onClose = {}
        )
    }
}

@Preview
@Composable
fun DialogTitleWithBackArrowPreview() {
    BookStoreTheme {
        DialogTitle(
            title = "Add New Item",
            showBackArrow = true,
            onClose = {}
        )
    }
}
