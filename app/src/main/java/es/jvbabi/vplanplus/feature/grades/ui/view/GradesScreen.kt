package es.jvbabi.vplanplus.feature.grades.ui.view

import android.content.Intent
import android.hardware.biometrics.BiometricManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.provider.Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.gson.Gson
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.feature.grades.domain.model.Grade
import es.jvbabi.vplanplus.feature.grades.domain.usecase.GradeUseState
import es.jvbabi.vplanplus.feature.grades.ui.calculator.GradeCollection
import es.jvbabi.vplanplus.feature.grades.ui.components.Average
import es.jvbabi.vplanplus.feature.grades.ui.view.components.error.NoGrades
import es.jvbabi.vplanplus.feature.grades.ui.view.components.error.NoVppId
import es.jvbabi.vplanplus.feature.grades.ui.view.components.error.NotActivated
import es.jvbabi.vplanplus.feature.grades.ui.view.components.error.WrongProfile
import es.jvbabi.vplanplus.feature.grades.ui.view.components.grades.GradeSubjectGroup
import es.jvbabi.vplanplus.feature.grades.ui.view.components.grades.LatestGrades
import es.jvbabi.vplanplus.shared.data.VppIdServer
import es.jvbabi.vplanplus.ui.common.BackIcon
import es.jvbabi.vplanplus.ui.common.InfoCard
import es.jvbabi.vplanplus.ui.screens.Screen
import java.nio.charset.StandardCharsets
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalEncodingApi::class)
@Composable
fun GradesScreen(
    navHostController: NavHostController,
    navBar: @Composable () -> Unit,
    gradesViewModel: GradesViewModel = hiltViewModel()
) {
    val activity = LocalContext.current as FragmentActivity
    val state = gradesViewModel.state.value
    val context = LocalContext.current

//    LaunchedEffect(
//        state.biometricStatus,
//        state.granted
//    ) {
//        if (state.biometricStatus == BiometricStatus.AVAILABLE && !state.granted && state.isBiometricEnabled) {
//            gradesViewModel.authenticate(activity)
//        }
//    }

    GradesScreenContent(
        onBack = { navHostController.popBackStack() },
        onLinkVppId = { navHostController.navigate(Screen.SettingsVppIdScreen.route) },
        onFixOnline = {
            val browserIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse(VppIdServer.url)
            )
            startActivity(context, browserIntent, null)
        },
        onHideBanner = { gradesViewModel.onHideBanner() },
        onStartCalculator = { grades ->
            val data = grades.groupBy { it.type }.map {
                GradeCollection(
                    name = it.key,
                    grades = it.value.map { grade -> grade.value to grade.modifier }
                )
            }
            val encodedString: String =
                Base64.encode(Gson().toJson(data).toByteArray(StandardCharsets.UTF_8))
            navHostController.navigate("${Screen.GradesCalculatorScreen.route}/$encodedString")
        },
        onStartAuthenticate = { gradesViewModel.authenticate(activity) },
        onOpenSecuritySettings = {
            val intent: Intent = when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                    Intent(Settings.ACTION_BIOMETRIC_ENROLL).putExtra(
                        EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                        BiometricManager.Authenticators.BIOMETRIC_WEAK
                    )
                }

                else -> {
                    Intent(Settings.ACTION_SECURITY_SETTINGS)
                }
            }
            if (intent.resolveActivity(context.packageManager) != null) {
                startActivity(context, intent, null)
            } else {
                startActivity(context, Intent(Settings.ACTION_SETTINGS), null)
            }
        },
        onEnableBiometric = {
            gradesViewModel.onSetBiometric(true)
            Toast.makeText(
                context,
                context.getString(R.string.grades_biometricNextTime),
                Toast.LENGTH_SHORT
            ).show()
        },
        onDismissEnableBiometricBanner = { gradesViewModel.onDismissEnableBiometricBanner() },
        onDisableBiometric = { gradesViewModel.onSetBiometric(false) },
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
    onHideBanner: () -> Unit,
    onDismissEnableBiometricBanner: () -> Unit,
    onEnableBiometric: () -> Unit,
    onStartAuthenticate: () -> Unit,
    onOpenSecuritySettings: () -> Unit,
    onDisableBiometric: () -> Unit,
    onStartCalculator: (List<Grade>) -> Unit,
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
            AnimatedVisibility(
                visible = state.showEnableBiometricBanner,
                enter = expandVertically(tween(200)),
                exit = shrinkVertically(tween(200))
            ) {
                InfoCard(
                    imageVector = Icons.Default.Fingerprint,
                    title = stringResource(id = R.string.grades_enableBiometricTitle),
                    text = stringResource(id = R.string.grades_enableBiometricText),
                    buttonText1 = stringResource(id = R.string.not_now),
                    buttonAction1 = onDismissEnableBiometricBanner,
                    buttonText2 = stringResource(id = R.string.enable),
                    buttonAction2 = onEnableBiometric,
                    modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
                )
            }

            AnimatedVisibility(visible = state.isBiometricEnabled && !state.isBiometricSetUp) {
                InfoCard(
                    imageVector = Icons.Default.Fingerprint,
                    title = stringResource(id = R.string.grades_biometricNotSetUpTitle),
                    text = stringResource(id = R.string.grades_biometricNotSetUpText),
                    modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp),
                    buttonText1 = stringResource(id = R.string.grades_openSecuritySettings),
                    buttonAction1 = onOpenSecuritySettings,
                    buttonText2 = stringResource(id = R.string.disable),
                    buttonAction2 = onDisableBiometric
                )
            }

            if (state.isBiometricEnabled && state.authenticationState == AuthenticationState.NONE) {
                TextButton(onClick = onStartAuthenticate) {
                    Text(text = stringResource(id = R.string.grades_login))
                }
                return@Scaffold
            }
            if (state.enabled == GradeUseState.ENABLED && state.grades.isEmpty()) {
                NoGrades()
                return@Scaffold
            }
//            if (!state.granted) return@Scaffold
            val grades = state.grades.entries.sortedBy { it.key.name }
            AnimatedVisibility(
                visible = state.showBanner,
                enter = expandVertically(tween(200)),
                exit = shrinkVertically(tween(200))
            ) {
                InfoCard(
                    imageVector = Icons.Default.Warning,
                    title = stringResource(id = R.string.grades_warningCardTitle),
                    text = stringResource(
                        id = R.string.grades_warningCardText
                    ),
                    buttonText1 = stringResource(id = R.string.hideForever),
                    buttonAction1 = onHideBanner,
                    modifier = Modifier.padding(8.dp)
                )
            }
            LazyColumn {
                if (state.avg != 0.0) item {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Average(avg = state.avg)
                    }
                }
                item {
                    Column(
                        modifier = Modifier.padding(8.dp)
                    ) {
                        LatestGrades(grades = state.latestGrades)
                        HorizontalDivider(modifier = Modifier.padding(top = 8.dp))
                    }
                }
                items(grades) { (_, grades) ->
                    Box(
                        modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
                    ) {
                        GradeSubjectGroup(
                            grades = grades,
                            onStartCalculator = { onStartCalculator(grades.grades) }
                        )
                    }
                }
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
        onHideBanner = {},
        onStartCalculator = {},
        navBar = {},
        state = GradesState(
            enabled = GradeUseState.ENABLED,
            isBiometricEnabled = true,
            isBiometricSetUp = false
//            biometricStatus = BiometricStatus.NOT_SET_UP
        ),
        onStartAuthenticate = {},
        onOpenSecuritySettings = {},
        onDismissEnableBiometricBanner = {},
        onEnableBiometric = {},
        onDisableBiometric = {}
    )
}