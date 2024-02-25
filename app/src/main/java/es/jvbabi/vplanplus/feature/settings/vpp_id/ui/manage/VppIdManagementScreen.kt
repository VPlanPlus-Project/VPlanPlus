package es.jvbabi.vplanplus.feature.settings.vpp_id.ui.manage

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Info
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.feature.settings.vpp_id.ui.domain.model.Session
import es.jvbabi.vplanplus.feature.settings.vpp_id.ui.domain.model.SessionType
import es.jvbabi.vplanplus.feature.settings.vpp_id.ui.manage.components.RetrySessions
import es.jvbabi.vplanplus.feature.settings.vpp_id.ui.manage.components.SessionEntry
import es.jvbabi.vplanplus.ui.common.BackIcon
import es.jvbabi.vplanplus.ui.common.BigButton
import es.jvbabi.vplanplus.ui.common.BigButtonGroup
import es.jvbabi.vplanplus.ui.common.FullWidthLoadingCircle
import es.jvbabi.vplanplus.ui.common.SettingsCategory
import es.jvbabi.vplanplus.ui.common.YesNoDialog
import es.jvbabi.vplanplus.ui.preview.ClassesPreview
import es.jvbabi.vplanplus.ui.preview.School
import es.jvbabi.vplanplus.ui.preview.VppIdPreview
import java.time.LocalDateTime

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
        onRequestLogout = { viewModel.openLogoutDialog() },
        onLogout = { viewModel.logout() },
        onLogoutDialogDismiss = { viewModel.closeLogoutDialog() },
        onFetchSessions = { viewModel.fetchSessionsFromUi() },
        onSessionClosed = { session -> viewModel.closeSession(session) },
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
                modifier = Modifier.padding(horizontal = 8.dp),
                buttons = listOf(
                    BigButton(
                        Icons.AutoMirrored.Default.Logout,
                        stringResource(id = R.string.vppIdSettingsManagement_logout),
                        onRequestLogout,
                    )
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
                        .fillMaxSize()
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
        }
    }
}

@Preview(showBackground = true)
@Composable
fun VppIdManagementScreenPreview() {
    val school = School.generateRandomSchools(1).first()
    val `class` = ClassesPreview.generateClass(school)
    VppIdManagementContent(
        state = VppIdManagementState(
            vppId = VppIdPreview.generateVppId(`class`),
            logoutDialog = false,
            sessionsState = SessionState.SUCCESS,
            sessions = listOf(
                Session(
                    type = SessionType.VPLANPLUS,
                    name = "VPlanPlus on Google Pixel 7a",
                    id = 2,
                    isCurrent = true,
                    createAt = LocalDateTime.now()
                )
            )
        )
    )
}