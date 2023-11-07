package es.jvbabi.vplanplus.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.DeveloperMode
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Settings
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.R

@Composable
@Preview
fun MenuPreview() {
    Menu(
        profiles = listOf(
            MenuProfile(0, "10a"),
            MenuProfile(1, "208"),
            MenuProfile(2, "Mül"),
        ),
        selectedProfile = MenuProfile(0, "10a")
    )
}

@Composable
fun Menu(
    onCloseClicked: () -> Unit = {},
    onProfileClicked: (profileId: Long) -> Unit = {},
    profiles: List<MenuProfile>,
    selectedProfile: MenuProfile,
    onRefreshClicked: () -> Unit = {},
    onSettingsClicked: () -> Unit = {},
    onRepositoryClicked: () -> Unit = {},
    onManageProfilesClicked: () -> Unit = {},
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f))
            .noRippleClickable(enabled = true) { onCloseClicked() },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .clip(RoundedCornerShape(16.dp))
                .noRippleClickable(enabled = true, onClick = { })
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
                                                color = if (profile.id != selectedProfile.id) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primaryContainer,
                                                shape = RoundedCornerShape(20.dp)
                                            )
                                            .clip(RoundedCornerShape(20.dp))
                                            .background(color = if (profile.id != selectedProfile.id) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.tertiaryContainer)
                                            .clickable { onProfileClicked(profile.id) },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = profile.name,
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
                        ButtonRow(Icons.Outlined.Refresh, text = "Refresh", onClick = { onRefreshClicked() })
                        ButtonRow(Icons.Outlined.Settings, stringResource(id = R.string.home_menuSettings), onClick = { onSettingsClicked() })
                        ButtonRow(Icons.Outlined.DeveloperMode, stringResource(id = R.string.home_menuRepository), onClick = { onRepositoryClicked() })
                    }
                }
            }
        }
    }
}

@Composable
fun ButtonRow(icon: ImageVector, text: String, onClick: () -> Unit = {}) {
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
    val id: Long,
    val name: String,
)

// https://www.droidcon.com/2023/02/16/remove-ripple-effect-from-clickable-and-toggleable-widget-in-jetpack-compose/
inline fun Modifier.noRippleClickable(
    enabled: Boolean = false,
    crossinline onClick: () -> Unit,
): Modifier = composed {
    clickable(
        indication = null,
        enabled = enabled,
        interactionSource = remember { MutableInteractionSource() }) {
        onClick()
    }
}