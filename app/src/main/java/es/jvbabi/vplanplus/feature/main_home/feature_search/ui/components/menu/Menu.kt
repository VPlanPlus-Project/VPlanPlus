package es.jvbabi.vplanplus.feature.main_home.feature_search.ui.components.menu

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.ui.common.RowVerticalCenter
import es.jvbabi.vplanplus.ui.common.Spacer8Dp
import es.jvbabi.vplanplus.ui.common.noRippleClickable
import es.jvbabi.vplanplus.ui.preview.GroupPreview
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
    val localConfiguration = LocalConfiguration.current
    val isTablet = localConfiguration.screenWidthDp > 600
    val isPortrait = localConfiguration.orientation == Configuration.ORIENTATION_PORTRAIT

    AnimatedVisibility(visible = isVisible) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.7f))
                .noRippleClickable { onCloseMenu() },
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .then(
                        if (isTablet) {
                            Modifier.requiredSizeIn(
                                maxWidth = 600.dp,
                                maxHeight = 500.dp,
                                minWidth = 300.dp,
                                minHeight = 300.dp
                            )
                        } else {
                            if (isPortrait) Modifier.fillMaxWidth()
                            else Modifier.fillMaxHeight()
                        }
                    )
                    .padding(16.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainer)
            ) menu@{
                Spacer8Dp()
                Header(onCloseMenu)

                val profileSwitcher: @Composable (modifier: Modifier) -> Unit = { modifier ->
                    ProfileSwitcher(
                        modifier = modifier,
                        profiles = profiles,
                        selectedProfile = selectedProfile,
                        onProfileClicked = onProfileClicked,
                        onProfileLongClicked = onProfileLongClicked,
                        onOpenSettings = onManageProfilesClicked
                    )
                }

                val buttons: @Composable (modifier: Modifier) -> Unit = { modifier ->
                    Buttons(
                        modifier = modifier,
                        hasUnreadNews = hasUnreadNews,
                        onNewsClicked = onNewsClicked,
                        isSyncing = isSyncing,
                        onRefreshClicked = onRefreshClicked,
                        onSettingsClicked = onSettingsClicked
                    )
                }

                val links: @Composable (modifier: Modifier) -> Unit = { modifier ->
                    Links(
                        modifier = modifier,
                        onRepositoryClicked = onRepositoryClicked,
                        onPrivacyPolicyClicked = onPrivacyPolicyClicked
                    )
                }

                Box(Modifier.padding(16.dp)) {
                    if (isTablet || !isPortrait) {
                        RowVerticalCenter {
                            Column(Modifier.weight(1f, true)) {
                                profileSwitcher(Modifier.fillMaxWidth())
                                links(Modifier.fillMaxWidth())
                            }
                            buttons(Modifier.weight(1f, true))
                        }
                    } else {
                        Column {
                            profileSwitcher(Modifier.fillMaxWidth())
                            buttons(Modifier.fillMaxWidth())
                            links(Modifier.fillMaxWidth())
                        }
                    }
                }
            }
        }
    }
}

@Composable
@Preview
@PreviewScreenSizes
private fun MenuPreview() {
    val group = GroupPreview.generateGroup()
    val profile = ProfilePreview.generateClassProfile(group)
    Menu(
        isVisible = true,
        isSyncing = true,
        profiles = listOf(profile),
        selectedProfile = profile,
        hasUnreadNews = true
    )
}