package es.jvbabi.vplanplus.ui.screens.home.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    currentProfileName: String,
    onMenuOpened: () -> Unit,
    onSearchActiveChange: (Boolean) -> Unit,
    searchOpen: Boolean,
    searchValue: String,
    onSearchTyping: (String) -> Unit,
    isSyncing: Boolean,
    content: @Composable () -> Unit
) {
    SearchBar(
        query = searchValue,
        onQueryChange = { onSearchTyping(it) },
        onSearch = { onSearchTyping(it) },
        active = searchOpen,
        modifier = Modifier.fillMaxWidth(),
        onActiveChange = { onSearchActiveChange(it) },
        leadingIcon = {
            IconButton(
                onClick = { onSearchActiveChange(!searchOpen) },
            ) {
                Icon(
                    imageVector = if (searchOpen) Icons.AutoMirrored.Default.ArrowBack else Icons.Default.Search,
                    contentDescription = stringResource(id = R.string.back),
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        },
        trailingIcon = {
            AnimatedVisibility(
                visible = !searchOpen,
                enter = fadeIn(animationSpec = TweenSpec(200)),
                exit = fadeOut(animationSpec = TweenSpec(200))
            ) {
                ProfileIcon(name = currentProfileName, isSyncing = isSyncing) {
                    onMenuOpened()
                }
            }
        },
        placeholder = { Text(stringResource(id = R.string.home_search)) },
    ) {
        content()
    }
}

@Preview
@Composable
fun SearchBarPreview() {
    SearchBar(currentProfileName = "10a", {}, {}, false, "", {}, true, {})
}

@Preview
@Composable
fun SearchBarOpenPreview() {
    SearchBar(currentProfileName = "10a", {}, {}, true, "", {}, false, {})
}

@Composable
fun ProfileIcon(name: String, isSyncing: Boolean, onClicked: () -> Unit) {
    Box(
        modifier = Modifier
            .padding(end = 4.dp)
            .height(40.dp)
            .width(40.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(color = MaterialTheme.colorScheme.secondary)
            .clickable(enabled = true) {
                onClicked()
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = name,
            color = MaterialTheme.colorScheme.onSecondary
        )
        val stroke by animateFloatAsState(
            targetValue = if (isSyncing) 2f else 0f, label = "Loading Animation",
            animationSpec = tween(durationMillis = 1000, easing = LinearEasing)
        )
        CircularProgressIndicator(
            modifier = Modifier.size(40.dp),
            color = MaterialTheme.colorScheme.tertiaryContainer,
            strokeWidth = stroke.dp
        )
    }
}

@Preview
@Composable
fun ProfileIconPreview() {
    ProfileIcon("10a", true) {}
}