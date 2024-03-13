package es.jvbabi.vplanplus.feature.home.feature_search.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

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
    showNotificationDot: Boolean,
    content: @Composable () -> Unit
) {

}

@Preview
@Composable
fun SearchBarPreview() {
    SearchBar(
        currentProfileName = "10a",
        onMenuOpened = {},
        onSearchActiveChange = {},
        searchOpen = false,
        searchValue = "",
        onSearchTyping = {},
        isSyncing = true,
        showNotificationDot = true,
        content = {}
    )
}

@Preview
@Composable
fun SearchBarOpenPreview() {
    SearchBar(
        currentProfileName = "10a",
        onMenuOpened = {},
        onSearchActiveChange = {},
        searchOpen = true,
        searchValue = "",
        onSearchTyping = {},
        isSyncing = false,
        showNotificationDot = false,
        content = {}
    )
}

@Composable
fun ProfileIcon(name: String, isSyncing: Boolean, showNotificationDot: Boolean, onClicked: () -> Unit) {
    val error = MaterialTheme.colorScheme.error
    Box(
        modifier = Modifier
            .padding(end = 4.dp)
            .height(40.dp)
            .width(40.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(color = MaterialTheme.colorScheme.secondary)
            .clickable(enabled = true) {
                onClicked()
            }
        ,
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

    if (showNotificationDot) {
        Box(modifier = Modifier
            .offset(x = 30.dp, y = (-0).dp)
            .size(12.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(color = error)
        )
    }
}

@Preview
@Composable
fun ProfileIconPreview() {
    ProfileIcon(name = "10a", isSyncing = true, showNotificationDot = true) {}
}