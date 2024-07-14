package es.jvbabi.vplanplus.feature.main_home.feature_search.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProfileIcon(
    modifier: Modifier = Modifier,
    name: String,
    isSyncing: Boolean,
    showNotificationDot: Boolean,
    progressIndicatorColor: Color = MaterialTheme.colorScheme.tertiaryContainer,
    foregroundColor: Color = MaterialTheme.colorScheme.onSecondary,
    backgroundColor: Color = MaterialTheme.colorScheme.secondary,
    onClicked: () -> Unit,
    onLongClicked: (() -> Unit)? = null
) {
    val error = MaterialTheme.colorScheme.error
    Box(
        modifier = modifier
            .height(40.dp)
            .width(40.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(backgroundColor)
            .combinedClickable(
                onClick = onClicked,
                onLongClick = onLongClicked
            )
        ,
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = name,
            style = (if (name.length > 2) MaterialTheme.typography.labelSmall else MaterialTheme.typography.labelLarge).copy(color = foregroundColor),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(2.dp),
        )
        val stroke by animateFloatAsState(
            targetValue = if (isSyncing) 2f else 0f, label = "Loading Animation",
            animationSpec = tween(durationMillis = 1000, easing = LinearEasing)
        )
        CircularProgressIndicator(
            modifier = Modifier.size(40.dp).alpha(stroke/2f),
            color = progressIndicatorColor,
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

@Composable
@Preview
private fun ProfileIconOverflowPreview() {
    ProfileIcon(name = "John Doe", isSyncing = true, showNotificationDot = true, onClicked = {})
}

@Composable
@Preview
private fun ProfileIconLongPreview() {
    ProfileIcon(name = "JG12", isSyncing = false, showNotificationDot = false, onClicked = {})
}

@Composable
@Preview
private fun ProfileIconShortPreview() {
    ProfileIcon(
        name = "7c",
        isSyncing = false,
        showNotificationDot = false,
        foregroundColor = MaterialTheme.colorScheme.onErrorContainer,
        backgroundColor = MaterialTheme.colorScheme.errorContainer,
        onClicked = {}
    )
}