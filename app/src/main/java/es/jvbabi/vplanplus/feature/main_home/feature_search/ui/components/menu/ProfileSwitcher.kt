package es.jvbabi.vplanplus.feature.main_home.feature_search.ui.components.menu

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.feature.main_home.feature_search.ui.components.ProfileIcon
import es.jvbabi.vplanplus.ui.common.Spacer4Dp
import es.jvbabi.vplanplus.ui.preview.GroupPreview
import es.jvbabi.vplanplus.ui.preview.ProfilePreview
import es.jvbabi.vplanplus.ui.preview.RoomPreview
import es.jvbabi.vplanplus.ui.preview.SchoolPreview

@Composable
fun ProfileSwitcher(
    modifier: Modifier = Modifier,
    profiles: List<Profile>,
    selectedProfile: Profile,
    onOpenSettings: () -> Unit = {},
    onProfileClicked: (profile: Profile) -> Unit,
    onProfileLongClicked: (profile: Profile) -> Unit,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(id = R.string.home_menuProfileList),
            style = MaterialTheme.typography.bodyLarge,
        )
        Spacer4Dp()
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items(profiles) { profile ->
                ProfileIcon(
                    name = profile.displayName,
                    isSyncing = false,
                    showNotificationDot = false,
                    foregroundColor = if (profile.id != selectedProfile.id) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onTertiaryContainer,
                    backgroundColor = if (profile.id != selectedProfile.id) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.tertiaryContainer,
                    onClicked = { onProfileClicked(profile) },
                    onLongClicked = { onProfileLongClicked(profile) }
                )
            }
            item {
                FilledTonalIconButton(onOpenSettings) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = stringResource(R.string.home_menuManageProfiles),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun ProfileSwitcherPreview() {
    val school = SchoolPreview.generateRandomSchool()
    val group = GroupPreview.generateGroup(school)
    val profile1 = ProfilePreview.generateClassProfile(group)
    val profile2 = ProfilePreview.generateRoomProfile(RoomPreview.generateRoom(school))
    ProfileSwitcher(
        profiles = listOf(profile1, profile2),
        selectedProfile = profile1,
        onProfileClicked = {},
        onProfileLongClicked = {}
    )
}