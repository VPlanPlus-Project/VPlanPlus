package es.jvbabi.vplanplus.ui.screens.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.DeveloperMode
import androidx.compose.material.icons.outlined.Newspaper
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.R
import java.util.UUID

@Composable
@Preview
fun MenuPreview() {
    Menu(
        profiles = listOf(
            MenuProfile(UUID.randomUUID(), "10a", "10a"),
            MenuProfile(UUID.randomUUID(), "208", "208"),
            MenuProfile(UUID.randomUUID(), "Mül", "Mül"),
        ),
        selectedProfile = MenuProfile(UUID.randomUUID(), "10a", "10a"),
        hasUnreadNews = true
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Menu(
    onCloseClicked: () -> Unit = {},
    onProfileClicked: (profileId: UUID) -> Unit = {},
    onProfileLongClicked: (profileId: UUID) -> Unit = {},
    profiles: List<MenuProfile>,
    selectedProfile: MenuProfile,
    onRefreshClicked: () -> Unit = {},
    onSettingsClicked: () -> Unit = {},
    onRepositoryClicked: () -> Unit = {},
    onManageProfilesClicked: () -> Unit = {},
    onNewsClicked: () -> Unit = {},
    onWebsiteClicked: () -> Unit = {},
    hasUnreadNews: Boolean,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f))
            .noRippleClickable { onCloseClicked() },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .clip(RoundedCornerShape(16.dp))
                .noRippleClickable(onClick = { })
                .background(MaterialTheme.colorScheme.surfaceContainer)
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    IconButton(onClick = { onCloseClicked() }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = null)
                    }
                    Text(
                        text = stringResource(id = R.string.app_name),
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth(),
                    )
                }

                Box(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(32.dp))
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    Box(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            Text(
                                modifier = Modifier.padding(vertical = 8.dp),
                                text = stringResource(id = R.string.home_menuProfileList),
                                style = MaterialTheme.typography.bodyLarge,
                            )
                            LazyRow {
                                items(profiles) { profile ->
                                    Box(
                                        modifier = Modifier
                                            .padding(end = 4.dp)
                                            .height(40.dp)
                                            .width(40.dp)
                                            .border(
                                                width = 1.dp,
                                                color = if (profile.profileId != selectedProfile.profileId) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primaryContainer,
                                                shape = RoundedCornerShape(20.dp)
                                            )
                                            .clip(RoundedCornerShape(20.dp))
                                            .background(color = if (profile.profileId != selectedProfile.profileId) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.tertiaryContainer)
                                            .combinedClickable(
                                                onClick = { onProfileClicked(profile.profileId) },
                                                onLongClick = { onProfileLongClicked(profile.profileId) }
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = if (profile.customName.length > 4) profile.name else profile.customName,
                                            color = MaterialTheme.colorScheme.onSecondary
                                        )
                                    }
                                }
                            }
                            TextButton(onClick = { onManageProfilesClicked() }, modifier = Modifier.padding(start = 0.dp)) {
                                Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, modifier = Modifier.padding(start = 0.dp))
                                Text(text = stringResource(id = R.string.home_menuManageProfiles))
                            }
                        }
                    }

                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
                ) {
                    Column {
                        ButtonRow(icon = Icons.Outlined.Newspaper, text = stringResource(id = R.string.home_menuNews), showNotificationDot = hasUnreadNews, onClick = { onNewsClicked() })
                        ButtonRow(Icons.Outlined.Refresh, text = stringResource(id = R.string.home_menuRefresh), onClick = { onRefreshClicked() })
                        ButtonRow(Icons.Outlined.Settings, stringResource(id = R.string.home_menuSettings), onClick = { onSettingsClicked() })
                        HorizontalDivider()
                        ButtonRow(Icons.Outlined.DeveloperMode, stringResource(id = R.string.home_menuRepository), onClick = { onRepositoryClicked() })
                        ButtonRow(icon = painterResource(id = R.drawable.vpp), text = stringResource(id = R.string.home_menuWebsite), onClick = { onWebsiteClicked() } )
                    }
                }
            }
        }
    }
}

@Composable
fun ButtonRow(icon: ImageVector, text: String, showNotificationDot: Boolean = false, onClick: () -> Unit = {}) {
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
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier
                .padding(start = 16.dp)
                .width(24.dp)
        )
        if (showNotificationDot) Box(modifier = Modifier
            .size(10.dp)
            .offset(x = (-6).dp, y = (-8).dp)
            .clip(RoundedCornerShape(5.dp))
            .background(MaterialTheme.colorScheme.error)
        )
        Text(text = text,
            modifier = Modifier.padding(start = 12.dp),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
fun ButtonRow(icon: Painter, text: String, showNotificationDot: Boolean = false, onClick: () -> Unit = {}) {
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
            painter = icon,
            contentDescription = text,
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier
                .padding(start = 16.dp)
                .width(24.dp)
        )
        if (showNotificationDot) Box(modifier = Modifier
            .size(10.dp)
            .offset(x = (-6).dp, y = (-8).dp)
            .clip(RoundedCornerShape(5.dp))
            .background(MaterialTheme.colorScheme.error)
        )
        Text(text = text,
            modifier = Modifier.padding(start = 12.dp),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
@Preview
fun ButtonRowPreview() {
    ButtonRow(Icons.Default.Settings, "Settings")
}

data class MenuProfile(
    val profileId: UUID,
    val name: String,
    val customName: String
)

inline fun Modifier.noRippleClickable(
    crossinline onClick: () -> Unit
): Modifier = composed {
    then(Modifier.clickable(
        indication = null,
        interactionSource = remember { MutableInteractionSource() }) {
        onClick()
    })
}