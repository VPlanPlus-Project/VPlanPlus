package es.jvbabi.vplanplus.ui.screens.settings.profile.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.domain.model.School
import es.jvbabi.vplanplus.ui.preview.Profile as PreviewProfile
import es.jvbabi.vplanplus.ui.preview.School as PreviewSchool

@Composable
fun SchoolCard(
    school: School,
    profiles: List<Profile>,
    onAddProfileClicked: () -> Unit,
    onProfileClicked: (Profile) -> Unit,
    onDeleteRequest: () -> Unit,
    onShareRequest: () -> Unit,
) {
    var menuExpanded by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(),
    ) {
        Column {
            Row(
                modifier = Modifier
                    .padding(start = 16.dp, end = 8.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .padding(top = 16.dp, bottom = 8.dp, end = 16.dp),
                ) {
                    Text(
                        text = school.name,
                        style = MaterialTheme.typography.titleLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(text = school.username, style = MaterialTheme.typography.labelSmall)
                }
                Box {
                    IconButton(onClick = { menuExpanded = !menuExpanded }) {
                        Icon(imageVector = Icons.Default.MoreVert, contentDescription = null)
                    }
                    DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                        DropdownMenuItem(
                            text = { Text(text = stringResource(id = R.string.settings_profileDeleteSchool)) },
                            onClick = { menuExpanded = false; onDeleteRequest() },
                            leadingIcon = {
                                Icon(imageVector = Icons.Default.Delete, contentDescription = null)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(text = stringResource(id = R.string.settings_profileShareSchool)) },
                            onClick = { menuExpanded = false; onShareRequest() },
                            leadingIcon = {
                                Icon(imageVector = Icons.Default.Share, contentDescription = null)
                            }
                        )
                    }
                }
            }
            LazyRow(
                modifier = Modifier
                    .padding(start = 16.dp, top = 8.dp, bottom = 16.dp)
                    .fillMaxWidth()
            ) {
                items(
                    profiles.sortedBy { it.type.ordinal.toString() + it.displayName }
                ) { profile ->
                    ProfileCard(
                        type = profile.type,
                        name = profile.displayName,
                        onClick = { onProfileClicked(profile) }
                    )
                }
                item {
                    ProfileCard(
                        type = null,
                        name = "+",
                        onClick = onAddProfileClicked
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun SchoolCardPreview() {
    val school = PreviewSchool.generateRandomSchools(1).first()
    SchoolCard(
        school = school,
        profiles = listOf(
            PreviewProfile.generateRoomProfile(),
            PreviewProfile.generateClassProfile(),
        ),
        onAddProfileClicked = {},
        onProfileClicked = {},
        onShareRequest = {},
        onDeleteRequest = {},
    )
}