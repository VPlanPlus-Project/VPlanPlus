package es.jvbabi.vplanplus.feature.settings.vpp_id.manage

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AutoDelete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.SupervisorAccount
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.feature.settings.vpp_id.domain.model.Session
import es.jvbabi.vplanplus.feature.settings.vpp_id.domain.model.SessionType
import es.jvbabi.vplanplus.feature.settings.vpp_id.manage.components.RetrySessions
import es.jvbabi.vplanplus.feature.settings.vpp_id.manage.components.SelectProfilesDialog
import es.jvbabi.vplanplus.feature.settings.vpp_id.manage.components.SessionEntry
import es.jvbabi.vplanplus.ui.common.BackIcon
import es.jvbabi.vplanplus.ui.common.BigButton
import es.jvbabi.vplanplus.ui.common.BigButtonGroup
import es.jvbabi.vplanplus.ui.common.FullWidthLoadingCircle
import es.jvbabi.vplanplus.ui.common.IconSettingsState
import es.jvbabi.vplanplus.ui.common.Setting
import es.jvbabi.vplanplus.ui.common.SettingsCategory
import es.jvbabi.vplanplus.ui.common.SettingsType
import es.jvbabi.vplanplus.ui.common.YesNoDialog
import es.jvbabi.vplanplus.ui.common.openLink
import es.jvbabi.vplanplus.ui.preview.GroupPreview
import es.jvbabi.vplanplus.ui.preview.PreviewFunction
import es.jvbabi.vplanplus.ui.preview.ProfilePreview.toActiveVppId
import es.jvbabi.vplanplus.ui.preview.SchoolPreview
import es.jvbabi.vplanplus.ui.preview.VppIdPreview
import java.time.ZonedDateTime

@Composable
fun VppIdManagementScreen(
    navHostController: NavHostController,
    vppId: Int,
    viewModel: VppIdManagementViewModel = hiltViewModel()
) {
    val state = viewModel.state.value
    val context = LocalContext.current

    LaunchedEffect(key1 = vppId) {
        viewModel.init(vppId)
    }

    val logoutMessages = arrayOf(
        stringResource(id = R.string.vppIdSettingsManagement_logoutSuccess),
        stringResource(id = R.string.vppIdSettingsManagement_logoutFailure)
    )
    LaunchedEffect(key1 = state.logoutSuccess) {
        if (state.logoutSuccess == null) return@LaunchedEffect
        Toast.makeText(
            context,
            if (state.logoutSuccess) logoutMessages[0]
            else logoutMessages[1],
            Toast.LENGTH_SHORT
        ).show()
        if (state.logoutSuccess) navHostController.popBackStack()
    }

    VppIdManagementContent(
        onBack = { navHostController.popBackStack() },
        onRequestLogout = viewModel::openLogoutDialog,
        onLogout = viewModel::logout,
        onLogoutDialogDismiss = viewModel::closeLogoutDialog,
        onFetchSessions = viewModel::fetchSessionsFromUi,
        onSessionClosed = viewModel::closeSession,
        onConfirmLinkedProfilesSelection = viewModel::onSetLinkedProfiles,
        onDeletionRequested = { openLink(context, state.currentServer.uiHost + "/app/id/settings/delete?forcelogout") },
        state = state
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VppIdManagementContent(
    onBack: () -> Unit = {},
    onLogout: () -> Unit = {},
    onRequestLogout: () -> Unit = {},
    onLogoutDialogDismiss: () -> Unit = {},
    onFetchSessions: () -> Unit = {},
    onSessionClosed: (Session) -> Unit = {},
    onConfirmLinkedProfilesSelection: (result: Map<ClassProfile, Boolean>) -> Unit = {},
    onDeletionRequested: () -> Unit = {},
    state: VppIdManagementState
) {
    if (state.vppId == null) return

    if (state.logoutDialog) {
        YesNoDialog(
            icon = Icons.AutoMirrored.Default.Logout,
            title = stringResource(id = R.string.vppIdSettingsManagement_logoutTitle),
            message = stringResource(id = R.string.vppIdSettingsManagement_logoutMessage, state.vppId.name),
            onYes = onLogout,
            onNo = onLogoutDialogDismiss
        )
    }

    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    var showLinkedProfilesSelectDialog by rememberSaveable { mutableStateOf(false) }

    if (showLinkedProfilesSelectDialog) {
        SelectProfilesDialog(
            vppId = state.vppId,
            profiles = state.profiles.filterIsInstance<ClassProfile>(),
            onDismiss = { showLinkedProfilesSelectDialog = false },
            onOk = onConfirmLinkedProfilesSelection
        )
    }

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Column {
                        Text(text = state.vppId.name)
                        Text(text = state.vppId.email ?: stringResource(id = R.string.vppIdSettingsManagement_missingEmail), style = MaterialTheme.typography.titleSmall)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        BackIcon()
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues)
        ) {
            BigButtonGroup(
                modifier = Modifier.padding(top = 8.dp, bottom = 16.dp),
                buttons = listOf(
                    BigButton(
                        Icons.AutoMirrored.Default.Logout,
                        stringResource(id = R.string.vppIdSettingsManagement_logout),
                        onRequestLogout,
                    )
                )
            )
            Setting(
                IconSettingsState(
                    title = stringResource(id = R.string.vppIdSettingsManagement_linkedProfilesTitle),
                    subtitle =
                        if (state.profiles.isEmpty()) stringResource(id = R.string.vppIdSettingsManagement_noProfilesPossible, state.vppId.groupName)
                        else if (state.profiles.count { (it as? ClassProfile)?.vppId == state.vppId } == 0) stringResource(id = R.string.vppIdSettings_noProfilesConnected)
                        else state.profiles.filter { (it as? ClassProfile)?.vppId == state.vppId }.joinToString(", ") { it.displayName },
                    enabled = state.profiles.isNotEmpty(),
                    type = SettingsType.FUNCTION,
                    doAction = { showLinkedProfilesSelectDialog = true },
                    imageVector = Icons.Default.SupervisorAccount
                )
            )
            SettingsCategory(
                title = stringResource(id = R.string.vppIdSettingsManagement_sessionsTitle)
            ) {
                when (state.sessionsState) {
                    SessionState.LOADING -> FullWidthLoadingCircle()
                    SessionState.ERROR -> RetrySessions(onFetchSessions)
                    else -> {
                        state.sessions.forEach { session ->
                            SessionEntry(session) { onSessionClosed(session) }
                        }
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier
                            .size(16.dp)
                    )
                    Text(text = stringResource(id = R.string.vppIdSettingsManagement_sessionsCloseInfo), modifier = Modifier.padding(start = 8.dp))
                }
            }
            Setting(IconSettingsState(
                type = SettingsType.FUNCTION,
                title = stringResource(id = R.string.vppIdSettingsManagement_requestDeletion),
                subtitle = stringResource(id = R.string.vppIdSettingsManagement_requestDeletionSubtitle),
                imageVector = Icons.Default.AutoDelete,
                enabled = true,
                isLoading = false,
                doAction = { onDeletionRequested() }
            ))
        }
    }
}

@OptIn(PreviewFunction::class)
@Preview(showBackground = true)
@Composable
fun VppIdManagementScreenPreview() {
    val school = SchoolPreview.generateRandomSchools(1).first()
    val `class` = GroupPreview.generateGroup(school)
    VppIdManagementContent(
        state = VppIdManagementState(
            vppId = VppIdPreview.generateVppId(`class`).toActiveVppId(),
            logoutDialog = false,
            sessionsState = SessionState.SUCCESS,
            sessions = listOf(
                Session(
                    type = SessionType.VPLANPLUS,
                    name = "VPlanPlus on Google Pixel 7a",
                    id = 2,
                    isCurrent = true,
                    createAt = ZonedDateTime.now()
                )
            )
        )
    )
}