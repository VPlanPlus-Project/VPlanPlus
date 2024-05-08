package es.jvbabi.vplanplus.feature.main_home.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.data.model.ProfileType
import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.feature.main_home.feature_search.ui.components.ProfileIcon
import es.jvbabi.vplanplus.ui.preview.ProfilePreview
import es.jvbabi.vplanplus.ui.preview.VppIdPreview
import java.time.ZonedDateTime

@Composable
fun Head(
    modifier: Modifier = Modifier,
    profile: Profile,
    currentTime: ZonedDateTime,
    isSyncing: Boolean,
    showNotificationDot: Boolean,
    onSearchClicked: () -> Unit = {},
    onProfileClicked: () -> Unit = {}
) {
    Column(modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column {
                Greeting(time = currentTime, name = profile.vppId?.name)
                Text(
                    text = stringResource(
                        id =
                        when (profile.type) {
                            ProfileType.STUDENT -> R.string.home_headSubtitleClass
                            ProfileType.TEACHER -> R.string.home_headSubtitleTeacher
                            ProfileType.ROOM -> R.string.home_headSubtitleRoom
                        },
                        profile.originalName
                    ),
                    style = MaterialTheme.typography.labelMedium
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(onClick = onSearchClicked) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = stringResource(id = R.string.home_search),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Box {
                    ProfileIcon(
                        name = profile.displayName,
                        isSyncing = isSyncing,
                        showNotificationDot = showNotificationDot,
                        onClicked = onProfileClicked
                    )
                }
            }
        }
        Spacer(
            modifier = Modifier
                .height(16.dp)
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(listOf(MaterialTheme.colorScheme.surface, MaterialTheme.colorScheme.surface.copy(alpha = 0f)))
                )
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun HeadPreview() {
    val profile = ProfilePreview.generateClassProfile(VppIdPreview.generateVppId(null))
    Head(
        currentTime = ZonedDateTime.now(),
        profile = profile,
        isSyncing = false,
        showNotificationDot = true
    )
}