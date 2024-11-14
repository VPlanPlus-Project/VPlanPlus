package es.jvbabi.vplanplus.feature.main_home.feature_search.ui.components.menu

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Newspaper
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.R

@Composable
fun Buttons(
    modifier: Modifier = Modifier,
    hasUnreadNews: Boolean,
    onNewsClicked: () -> Unit,
    isSyncing: Boolean,
    onRefreshClicked: () -> Unit,
    onSettingsClicked: () -> Unit
) {
    Column(modifier) {
        ButtonRow(
            icon = Icons.Outlined.Newspaper,
            text = stringResource(id = R.string.home_menuNews),
            showNotificationDot = hasUnreadNews,
            onClick = { onNewsClicked() }
        )
        ButtonRow(
            Icons.Outlined.Refresh,
            text = stringResource(id = R.string.home_menuRefresh),
            subtext = if (isSyncing) stringResource(id = R.string.home_menuSyncing) else null,
            onClick = { onRefreshClicked() }
        )
        ButtonRow(
            Icons.Outlined.Settings,
            stringResource(id = R.string.home_menuSettings),
            onClick = { onSettingsClicked() }
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun ButtonsPreview() {
    Buttons(
        hasUnreadNews = true,
        onNewsClicked = {},
        isSyncing = true,
        onRefreshClicked = {},
        onSettingsClicked = {}
    )
}

@Composable
private fun ButtonRow(
    icon: ImageVector,
    text: String,
    subtext: String? = null,
    showNotificationDot: Boolean = false,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .height(50.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable {
                onClick()
            },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .padding(start = 16.dp)
                .width(24.dp)
        )
        if (showNotificationDot) Box(
            modifier = Modifier
                .size(10.dp)
                .offset(x = (-6).dp, y = (-8).dp)
                .clip(RoundedCornerShape(5.dp))
                .background(MaterialTheme.colorScheme.error)
        )

        Column(
            Modifier
                .padding(horizontal = 12.dp)
                .fillMaxWidth()) {
            Text(
                text = text,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            AnimatedVisibility(
                visible = subtext != null,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                var actualText by rememberSaveable { mutableStateOf("") }
                LaunchedEffect(key1 = subtext) {
                    if (subtext == null) return@LaunchedEffect
                    actualText = subtext
                }
                Text(
                    text = actualText,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}