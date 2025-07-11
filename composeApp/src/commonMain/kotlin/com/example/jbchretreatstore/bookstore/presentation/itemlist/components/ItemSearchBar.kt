package com.example.jbchretreatstore.bookstore.presentation.itemlist.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.example.jbchretreatstore.core.presentation.DarkBlue
import com.example.jbchretreatstore.core.presentation.DesertWhite
import com.example.jbchretreatstore.core.presentation.SandYellow
import jbchretreatstore.composeapp.generated.resources.Res
import jbchretreatstore.composeapp.generated.resources.clear_hint
import jbchretreatstore.composeapp.generated.resources.search_hint
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ItemSearchBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onItemSearch: () -> Unit,
    modifier: Modifier = Modifier,
) {
    CompositionLocalProvider(
        LocalTextSelectionColors provides TextSelectionColors(
            handleColor = SandYellow, backgroundColor = SandYellow.copy(alpha = .4f)
        )
    ) {
        OutlinedTextField(
            modifier = modifier.background(
                shape = RoundedCornerShape(percent = 100), color = DesertWhite
            ).minimumInteractiveComponentSize(),
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            shape = RoundedCornerShape(percent = 100),
            colors = OutlinedTextFieldDefaults.colors(
                cursorColor = DarkBlue,
                focusedBorderColor = SandYellow,
            ),
            placeholder = {
                Text(text = stringResource(Res.string.search_hint))
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = stringResource(Res.string.search_hint),
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = .67f)
                )
            },
            singleLine = true,
            keyboardActions = KeyboardActions(
                onSearch = {
                    onItemSearch()
                }),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text, imeAction = ImeAction.Search
            ),
            trailingIcon = {
                AnimatedVisibility(
                    visible = searchQuery.isNotBlank(),
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    IconButton(
                        onClick = {
                            onSearchQueryChange("")
                        }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(Res.string.clear_hint),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            })
    }
}


@Preview
@Composable
fun ItemSearchBarPreview() {
    ItemSearchBar(
        searchQuery = "Bible",
        onSearchQueryChange = {},
        onItemSearch = {},
        modifier = Modifier.fillMaxWidth().background(Color.White)
    )
}