package es.jvbabi.vplanplus.feature.main_home.feature_search.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.outlined.Newspaper
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.MainActivity
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.ui.common.DOT
import es.jvbabi.vplanplus.ui.preview.ProfilePreview

@Composable
fun Menu(
    isVisible: Boolean,
    isSyncing: Boolean,
    onCloseMenu: () -> Unit = {},
    onProfileClicked: (profile: Profile) -> Unit = {},
    onProfileLongClicked: (profile: Profile) -> Unit = {},
    profiles: List<Profile>,
    selectedProfile: Profile,
    onRefreshClicked: () -> Unit = {},
    onSettingsClicked: () -> Unit = {},

    onRepositoryClicked: () -> Unit = {},
    onManageProfilesClicked: () -> Unit = {},
    onNewsClicked: () -> Unit = {},
    onPrivacyPolicyClicked: () -> Unit = {},
    hasUnreadNews: Boolean,
) {
    BackHandler(isVisible, onCloseMenu)
    AnimatedVisibility(visible = isVisible) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.7f))
                .noRippleClickable { onCloseMenu() },
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .clip(RoundedCornerShape(16.dp))
                    .noRippleClickable(onClick = { })
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        IconButton(onClick = { onCloseMenu() }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = null)
                        }
                        Row(
                            modifier = Modifier
                                .align(Alignment.Center),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = if (MainActivity.isAppInDarkMode.value) painterResource(id = R.drawable.vpp_logo_light) else painterResource(id = R.drawable.vpp_logo_dark),
                                contentDescription = stringResource(id = R.string.app_name),
                                modifier = Modifier
                                    .size(32.dp),
                            )
                            VerticalDivider(modifier = Modifier
                                .padding(8.dp)
                                .height(20.dp))
                            Text(
                                text = stringResource(id = R.string.app_name),
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier,
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
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
                                        ProfileIcon(
                                            modifier = Modifier.padding(end = 4.dp),
                                            name = profile.displayName,
                                            isSyncing = false,
                                            showNotificationDot = false,
                                            foregroundColor = if (profile.id != selectedProfile.id) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onTertiaryContainer,
                                            backgroundColor = if (profile.id != selectedProfile.id) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.tertiaryContainer,
                                            onClicked = { onProfileClicked(profile) },
                                            onLongClicked = { onProfileLongClicked(profile) }
                                        )
                                    }
                                }
                                TextButton(
                                    onClick = { onManageProfilesClicked() },
                                    modifier = Modifier
                                        .padding(start = 0.dp)
                                        .fillMaxWidth()
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ChevronRight,
                                        contentDescription = null,
                                        modifier = Modifier.padding(start = 0.dp)
                                    )
                                    Text(
                                        text = stringResource(id = R.string.home_menuManageProfiles),
                                        modifier = Modifier.fillMaxWidth()
                                    )
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
                            ButtonRow(
                                icon = Icons.Outlined.Newspaper,
                                text = stringResource(id = R.string.home_menuNews),
                                showNotificationDot = hasUnreadNews,
                                onClick = { onNewsClicked() })
                            ButtonRow(
                                Icons.Outlined.Refresh,
                                text = stringResource(id = R.string.home_menuRefresh),
                                subtext = if (isSyncing) stringResource(id = R.string.home_menuSyncing) else null,
                                onClick = { onRefreshClicked() })
                            ButtonRow(
                                Icons.Outlined.Settings,
                                stringResource(id = R.string.home_menuSettings),
                                onClick = { onSettingsClicked() })
                            Row(
                                modifier = Modifier
                                    .padding(top = 8.dp)
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = stringResource(id = R.string.home_menuPrivacy),
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Normal),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .weight(1f, true)
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { onPrivacyPolicyClicked() }
                                        .padding(8.dp),
                                )
                                Text(
                                    text = DOT,
                                )
                                Text(
                                    text = stringResource(id = R.string.home_menuRepository),
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Normal),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .weight(1f, true)
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { onRepositoryClicked() }
                                        .padding(8.dp),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ButtonRow(
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

@Composable
@Preview
private fun ButtonRowPreview() {
    ButtonRow(Icons.Default.Settings, "Settings", "Manage your preferences")
}

@Composable
@Preview
private fun MenuPreview() {
    val profile = ProfilePreview.generateClassProfile()
    Menu(isVisible = true, isSyncing = true, profiles = listOf(profile), selectedProfile = profile, hasUnreadNews = true)
}

inline fun Modifier.noRippleClickable(
    crossinline onClick: () -> Unit
): Modifier = composed {
    then(Modifier.clickable(
        indication = null,
        interactionSource = remember { MutableInteractionSource() }) {
        onClick()
    })
}