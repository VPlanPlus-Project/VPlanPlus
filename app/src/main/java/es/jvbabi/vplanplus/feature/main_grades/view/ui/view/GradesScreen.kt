package es.jvbabi.vplanplus.feature.main_grades.view.ui.view

import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi
import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.gson.Gson
import es.jvbabi.vplanplus.MainActivity
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.feature.main_grades.view.domain.model.Grade
import es.jvbabi.vplanplus.feature.main_grades.view.domain.usecase.GradeUseState
import es.jvbabi.vplanplus.feature.main_grades.view.ui.calculator.GradeCollection
import es.jvbabi.vplanplus.feature.main_grades.view.ui.components.Average
import es.jvbabi.vplanplus.feature.main_grades.view.ui.view.components.AdvertiseBiometricAuthenticationBanner
import es.jvbabi.vplanplus.feature.main_grades.view.ui.view.components.BiometricNotSetUpBanner
import es.jvbabi.vplanplus.feature.main_grades.view.ui.view.components.Filter
import es.jvbabi.vplanplus.feature.main_grades.view.ui.view.components.grades.GradeSubjectGroup
import es.jvbabi.vplanplus.feature.main_grades.view.ui.view.components.grades.LatestGrades
import es.jvbabi.vplanplus.feature.main_grades.view.ui.view.components.screens.Authenticate
import es.jvbabi.vplanplus.feature.main_grades.view.ui.view.components.screens.NoGrades
import es.jvbabi.vplanplus.feature.main_grades.view.ui.view.components.screens.NoVppId
import es.jvbabi.vplanplus.feature.main_grades.view.ui.view.components.screens.WrongProfile
import es.jvbabi.vplanplus.ui.common.BackIcon
import es.jvbabi.vplanplus.ui.common.InfoCard
import es.jvbabi.vplanplus.ui.common.charts.BarChart
import es.jvbabi.vplanplus.ui.common.charts.BarChartData
import es.jvbabi.vplanplus.ui.screens.Screen
import java.nio.charset.StandardCharsets
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalEncodingApi::class)
@Composable
fun GradesScreen(
    navHostController: NavHostController,
    navBar: @Composable (expanded: Boolean) -> Unit,
    gradesViewModel: GradesViewModel = hiltViewModel()
) {
    val activity = LocalContext.current as FragmentActivity
    val state = gradesViewModel.state.value
    var runAutomatically by rememberSaveable { mutableStateOf(true) }

    LaunchedEffect(
        state.authenticationState,
        state.isBiometricEnabled
    ) {
        if (!runAutomatically) return@LaunchedEffect
        if (state.authenticationState == AuthenticationState.NONE && state.isBiometricEnabled && state.isBiometricSetUp) {
            runAutomatically = false
            gradesViewModel.onEvent(GradeEvent.StartBiometricAuthentication(activity))
        }
    }

    GradesScreenContent(
        onBack = { navHostController.popBackStack() },
        onLinkVppId = { navHostController.navigate(Screen.SettingsVppIdScreen.route) },
        onStartCalculator = { grades ->
            val data = grades.filter { it.actualValue != null }.groupBy { it.type }.map {
                GradeCollection(
                    name = it.key,
                    grades = it.value.map { grade -> grade.value to grade.modifier }
                )
            }
            val encodedString: String =
                Base64.encode(Gson().toJson(data).toByteArray(StandardCharsets.UTF_8))
            navHostController.navigate("${Screen.GradesCalculatorScreen.route}/?grades=$encodedString&isSek2=${state.isSek2}")
        },
        onOpenSecuritySettings = {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) activity.startActivity(Intent(Settings.ACTION_BIOMETRIC_ENROLL))
            else activity.startActivity(Intent(Settings.ACTION_SECURITY_SETTINGS))
        },
        onEvent = gradesViewModel::onEvent,
        state = state,
        navBar = navBar
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationGraphicsApi::class)
@Composable
private fun GradesScreenContent(
    onBack: () -> Unit,
    onLinkVppId: () -> Unit,
    onOpenSecuritySettings: () -> Unit,
    onStartCalculator: (List<Grade>) -> Unit,
    onEvent: (event: GradeEvent) -> Unit,
    state: GradesState,
    navBar: @Composable (expanded: Boolean) -> Unit
) {
    var searchExpanded by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current

    var statisticsSheetOpen by rememberSaveable { mutableStateOf(false) }
    val statisticsSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    if (statisticsSheetOpen && state.authenticationState == AuthenticationState.AUTHENTICATED) {
        ModalBottomSheet(
            onDismissRequest = { statisticsSheetOpen = false },
            sheetState = statisticsSheetState,
        ) {
            Text(
                text = stringResource(id = R.string.grades_statsTitle),
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(start = 16.dp)
            )

            val allGrades = state.grades.flatMap { it.value.grades }.filter { it.actualValue != null }
            if (allGrades.isNotEmpty()) BarChart(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxHeight(0.5f),
                items = ((if (state.isSek2) 0 else 1)..(if (state.isSek2) 15 else 6)).toList().map { value ->
                    BarChartData(
                        group = "$value",
                        value = allGrades.count { it.value == value.toFloat() }.toFloat()
                    )
                },
                showValueInBars = { "${it.value.toInt()}x"},
                labeling = { it.group }
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.grades_title)) },
                navigationIcon = { IconButton(onClick = onBack) { BackIcon() } },
                actions = {
                    if (state.enabled == GradeUseState.ENABLED) {
                        AnimatedVisibility(visible = state.authenticationState == AuthenticationState.AUTHENTICATED) {
                            IconButton(onClick = { statisticsSheetOpen = true }) {
                                Icon(imageVector = Icons.Default.BarChart, contentDescription = stringResource(id = R.string.grades_statsTitle))
                            }
                        }
                        IconButton(onClick = { searchExpanded = !searchExpanded }) {
                            val painter = rememberAnimatedVectorPainter(
                                animatedImageVector = AnimatedImageVector.animatedVectorResource(R.drawable.anim_search_close),
                                atEnd = searchExpanded
                            )
                            Image(
                                painter = painter,
                                contentDescription = null,
                                modifier = Modifier.scale(.8f),
                                colorFilter =
                                    if (MainActivity.isAppInDarkMode.value) ColorFilter.colorMatrix(ColorMatrix(colorMatrix))
                                    else null
                            )
                        }
                    }
                }
            )
        },
        bottomBar = { navBar(true) }
    ) { paddingValues ->
        when (state.enabled) {
            GradeUseState.NO_VPP_ID -> NoVppId(onLinkVppId)
            GradeUseState.WRONG_PROFILE_SELECTED -> WrongProfile()
            GradeUseState.ENABLED -> {}
            else -> { /* This should not happen, stopping VPlanPlus from loading your grades is currently not supported */ }
        }
        if (state.enabled != GradeUseState.ENABLED) return@Scaffold
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            Filter(
                visible = searchExpanded,
                subjects = state.grades.keys.sortedBy { it.name }.associateWith { state.visibleSubjects.contains(it) && state.visibleSubjects.size != state.grades.size },
                intervals = state.intervals,
                onEvent = onEvent
            )
            AdvertiseBiometricAuthenticationBanner(state.showEnableBiometricBanner, onEvent)
            BiometricNotSetUpBanner(state.isBiometricEnabled && !state.isBiometricSetUp, onOpenSecuritySettings, onEvent)

            if (state.isBiometricEnabled && state.authenticationState != AuthenticationState.AUTHENTICATED) {
                Authenticate { onEvent(GradeEvent.StartBiometricAuthentication(context as FragmentActivity)) }
                return@Column
            }
            if (state.grades.isEmpty()) {
                NoGrades()
                return@Column
            }
            val grades = state.grades.entries.sortedBy { it.key.name }
            AnimatedVisibility(
                visible = state.showCalculationDisclaimerBanner,
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
                    buttonAction1 = { onEvent(GradeEvent.DismissDisclaimerBanner) },
                    modifier = Modifier.padding(8.dp)
                )
            }
            LazyColumn {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Average(avg = state.avg, isSek2 = state.isSek2)
                    }
                }
                item {
                    AnimatedVisibility(
                        visible = state.visibleSubjects.size == state.grades.size,
                        enter = expandVertically(tween(200)),
                        exit = shrinkVertically(tween(200)),
                        modifier = Modifier.padding(8.dp)
                    ) {
                        LatestGrades(state.latestGrades)
                    }
                }
                items(grades) { (_, grades) ->
                    AnimatedVisibility(
                        visible = state.visibleSubjects.contains(grades.subject),
                        enter = expandVertically(tween(200)),
                        exit = shrinkVertically(tween(200)),
                        modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
                    ) {
                        GradeSubjectGroup(
                            grades = grades,
                            onStartCalculator = { onStartCalculator(grades.grades) },
                            withIntervals = state.intervals.filterValues { it }.keys,
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
        onStartCalculator = {},
        navBar = {},
        state = GradesState(
            enabled = GradeUseState.ENABLED,
            isBiometricEnabled = true,
            isBiometricSetUp = false
//            biometricStatus = BiometricStatus.NOT_SET_UP
        ),
        onOpenSecuritySettings = {},
        onEvent = {}
    )
}

private val colorMatrix = floatArrayOf(
    -1f, 0f, 0f, 0f, 255f,
    0f, -1f, 0f, 0f, 255f,
    0f, 0f, -1f, 0f, 255f,
    0f, 0f, 0f, 1f, 0f
)
