package es.jvbabi.vplanplus.ui.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.R

@Composable
fun SearchBar(
    currentProfileName: String,
    onMenuOpened: () -> Unit,
    onSearchClicked: (Boolean) -> Unit,
    searchOpen: Boolean,
    searchValue: String,
    onSearchTyping: (String) -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(key1 = searchOpen, block = {
        if (searchOpen) focusRequester.requestFocus()
    })
    Box(
        modifier = Modifier
            .padding(8.dp)
            .clip(RoundedCornerShape(25.dp))
            .background(color = MaterialTheme.colorScheme.secondaryContainer)
            .fillMaxWidth()
            .height(50.dp)
    ) {
        if (!searchOpen) Row(
            modifier = Modifier
                .fillMaxSize()
                .clickable {
                    onSearchClicked(true)
                },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .height(20.dp),
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Text(
                    text = stringResource(id = R.string.home_search),
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
            Box(
                modifier = Modifier
                    .padding(end = 4.dp)
                    .height(40.dp)
                    .width(40.dp)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.secondary,
                        shape = RoundedCornerShape(20.dp)
                    )
                    .clip(RoundedCornerShape(20.dp))
                    .background(color = MaterialTheme.colorScheme.secondary)
                    .clickable(enabled = true) {
                        onMenuOpened()
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = currentProfileName,
                    color = MaterialTheme.colorScheme.onSecondary
                )
            }
        }
        else {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { onSearchClicked(false) }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                        contentDescription = stringResource(
                            id = R.string.back
                        )
                    )
                }
                Box {
                    BasicTextField(
                        value = searchValue,
                        onValueChange = { onSearchTyping(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .background(color = Color.Transparent)
                            .focusRequester(focusRequester)
                            .padding(end = 8.dp),
                        decorationBox = { innerTextField ->
                            Box(
                                Modifier
                                    .offset(x = (-4).dp)
                                    .fillMaxHeight()
                                    .padding(vertical = 16.dp)
                            ) {
                                if (searchValue.isEmpty()) {
                                    Text(
                                        stringResource(id = R.string.home_search),
                                        modifier = Modifier.offset(y = (-3).dp)
                                    )
                                }
                                innerTextField()
                            }
                        },
                    )

                }
            }
        }
    }
}

@Preview
@Composable
fun SearchBarPreview() {
    SearchBar(currentProfileName = "10a", {}, {}, false, "", {})
}

@Preview
@Composable
fun SearchBarOpenPreview() {
    SearchBar(currentProfileName = "10a", {}, {}, true, "", {})
}