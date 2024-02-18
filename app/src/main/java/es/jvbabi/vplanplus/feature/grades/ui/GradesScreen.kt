package es.jvbabi.vplanplus.feature.grades.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.feature.grades.domain.usecase.GradeUseState
import es.jvbabi.vplanplus.feature.grades.ui.components.NoVppId
import es.jvbabi.vplanplus.feature.grades.ui.components.NotActivated
import es.jvbabi.vplanplus.feature.grades.ui.components.WrongProfile
import es.jvbabi.vplanplus.shared.data.VppIdServer
import es.jvbabi.vplanplus.ui.common.BackIcon
import es.jvbabi.vplanplus.ui.screens.Screen

@Composable
fun GradesScreen(
    navHostController: NavHostController,
    navBar: @Composable () -> Unit,
    gradesViewModel: GradesViewModel = hiltViewModel()
) {
    val state = gradesViewModel.state.value
    val context = LocalContext.current

    GradesScreenContent(
        onBack = { navHostController.popBackStack() },
        onLinkVppId = { navHostController.navigate(Screen.SettingsVppIdScreen.route) },
        onFixOnline = {
            val browserIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse(VppIdServer.url)
            )
            ContextCompat.startActivity(context, browserIntent, null)
        },
        state = state,
        navBar = navBar
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GradesScreenContent(
    onBack: () -> Unit,
    onLinkVppId: () -> Unit,
    onFixOnline: () -> Unit,
    state: GradesState,
    navBar: @Composable () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.grades_title)) },
                navigationIcon = { IconButton(onClick = onBack) { BackIcon() } }
            )
        },
        bottomBar = navBar
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            when (state.enabled) {
                GradeUseState.NO_VPP_ID -> NoVppId(onLinkVppId)
                GradeUseState.WRONG_PROFILE_SELECTED -> WrongProfile()
                GradeUseState.NOT_ENABLED -> NotActivated(onFixOnline, onLinkVppId)
                else -> {}
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun GradesScreenPreview() {
    GradesScreenContent(
        onBack = {},
        onLinkVppId = {},
        onFixOnline = {},
        navBar = {},
        state = GradesState(
            enabled = GradeUseState.WRONG_PROFILE_SELECTED
        )
    )
}