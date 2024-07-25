package es.jvbabi.vplanplus.ui.screens.id_link

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.model.VppId
import es.jvbabi.vplanplus.ui.preview.PreviewFunction
import es.jvbabi.vplanplus.ui.preview.ProfilePreview
import es.jvbabi.vplanplus.ui.preview.ProfilePreview.toActiveVppId
import es.jvbabi.vplanplus.ui.screens.Screen
import es.jvbabi.vplanplus.ui.screens.id_link.components.AutoConnectToProfile
import es.jvbabi.vplanplus.ui.screens.id_link.components.LinkNoProfiles
import es.jvbabi.vplanplus.ui.preview.GroupPreview as PreviewClasses
import es.jvbabi.vplanplus.ui.preview.SchoolPreview as PreviewSchool

@Composable
fun VppIdLinkScreen(
    navHostController: NavHostController,
    token: String?,
    vppIdLinkViewModel: VppIdLinkViewModel = hiltViewModel()
) {
    val state = vppIdLinkViewModel.state

    LaunchedEffect(token) {
        vppIdLinkViewModel.init(token)
    }

    VppIdLinkScreenContent(
        state = state,
        onOk = {
            vppIdLinkViewModel.onProceed()
            navHostController.navigate(Screen.HomeScreen.route) {
                popUpTo(0)
            }
        },
        onRetry = {
            vppIdLinkViewModel.init(token)
        },
        onToggleProfile = vppIdLinkViewModel::onToggleProfileState,
    )
}

@Composable
private fun VppIdLinkScreenContent(
    onOk: () -> Unit = {},
    onRetry: () -> Unit = {},
    onToggleProfile: (profile: ClassProfile) -> Unit = {},
    state: VppIdLinkState
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface),
        contentAlignment = Alignment.Center
    ) {
        if (state.isLoading) CircularProgressIndicator()
        else if (state.error || state.vppId?.group == null) {
            Column {
                Text(text = "Error")
                Button(onClick = onRetry) {
                    Text(text = "Retry/Erneut versuchen")
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp)
                )
                Text(
                    text = stringResource(id = R.string.vppidlink_welcome, state.vppId.name),
                    style = MaterialTheme.typography.headlineMedium,
                )
                Text(
                    text = stringResource(
                        id = R.string.vppidlink_message,
                        state.vppId.group.school.name,
                        state.vppId.group.name
                    ),
                    textAlign = TextAlign.Center
                )
                if (state.selectedProfileFoundAtStart == true) {
                    AutoConnectToProfile(
                        modifier = Modifier.padding(vertical = 8.dp),
                        profileName = state.selectedProfiles.toList().first { it.second }.first.displayName
                    )
                } else if (state.selectedProfiles.isEmpty()) {
                    LinkNoProfiles(
                        modifier = Modifier.padding(vertical = 8.dp),
                        className = state.vppId.group.name
                    )
                } else {
                    Column(Modifier.padding(vertical = 8.dp)) {
                        Text(text = stringResource(id = R.string.vppIdLink_selectAProfile), style = MaterialTheme.typography.labelLarge)
                        state.selectedProfiles.forEach { (profile, selected) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { onToggleProfile(profile) },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(checked = selected, onCheckedChange = { onToggleProfile(profile) })
                                Text(
                                    text = profile.displayName,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    }
                }
                Button(enabled = state.selectedProfiles.any { it.value } || state.selectedProfiles.isEmpty(), onClick = { onOk() }) {
                    Text(text = stringResource(id = android.R.string.ok))
                }
            }
        }
    }
}

@OptIn(PreviewFunction::class)
@Preview
@Composable
private fun VppIdLinkScreenAutoLinkPreview() {
    val school = PreviewSchool.generateRandomSchools(1).first()
    val group = PreviewClasses.generateGroup(school)
    val vppId = VppId(
        id = 1,
        name = "Maria Musterfrau",
        schoolId = school.id,
        school = school,
        groupName = group.name,
        group = group,
        email = "maria.musterfrau@email.com"
    ).toActiveVppId()
    VppIdLinkScreenContent(
        state = VppIdLinkState(
            isLoading = false,
            selectedProfileFoundAtStart = true,
            selectedProfiles = mapOf(
                ProfilePreview.generateClassProfile(group, vppId) to true
            ),
            vppId = vppId,
        )
    )
}

@OptIn(PreviewFunction::class)
@Preview
@Composable
private fun VppIdLinkScreenPreview() {
    val school = PreviewSchool.generateRandomSchools(1).first()
    val group = PreviewClasses.generateGroup(school)
    val group2 = PreviewClasses.generateGroup(school)
    val vppId = VppId(
        id = 1,
        name = "Maria Musterfrau",
        schoolId = school.id,
        school = school,
        groupName = group.name,
        group = group,
        email = "maria.musterfrau@email.com"
    ).toActiveVppId()
    VppIdLinkScreenContent(
        state = VppIdLinkState(
            isLoading = false,
            selectedProfileFoundAtStart = false,
            selectedProfiles = mapOf(
                ProfilePreview.generateClassProfile(group, vppId) to true,
                ProfilePreview.generateClassProfile(group2) to false
            ),
            vppId = vppId,
        )
    )
}