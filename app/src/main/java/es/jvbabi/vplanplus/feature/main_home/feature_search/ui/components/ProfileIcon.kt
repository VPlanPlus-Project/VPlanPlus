package es.jvbabi.vplanplus.feature.main_home.feature_search.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
fun ProfileIcon(modifier: Modifier = Modifier, name: String, isSyncing: Boolean, showNotificationDot: Boolean, onClicked: () -> Unit) {
    val error = MaterialTheme.colorScheme.error
    Box(
        modifier = modifier
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