package es.jvbabi.vplanplus.feature.main_grades.ui.view

import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi
import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
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
import es.jvbabi.vplanplus.feature.main_grades.domain.model.Grade
import es.jvbabi.vplanplus.feature.main_grades.domain.model.Interval
import es.jvbabi.vplanplus.feature.main_grades.domain.model.Subject
import es.jvbabi.vplanplus.feature.main_grades.domain.usecase.GradeUseState
import es.jvbabi.vplanplus.feature.main_grades.ui.calculator.GradeCollection
import es.jvbabi.vplanplus.feature.main_grades.ui.components.Average
import es.jvbabi.vplanplus.feature.main_grades.ui.view.components.grades.GradeSubjectGroup
import es.jvbabi.vplanplus.feature.main_grades.ui.view.components.grades.LatestGrades
import es.jvbabi.vplanplus.feature.main_grades.ui.view.components.screens.Authenticate
import es.jvbabi.vplanplus.feature.main_grades.ui.view.components.screens.NoGrades
import es.jvbabi.vplanplus.feature.main_grades.ui.view.components.screens.NoVppId
import es.jvbabi.vplanplus.feature.main_grades.ui.view.components.screens.WrongProfile
import es.jvbabi.vplanplus.ui.common.BackIcon
import es.jvbabi.vplanplus.ui.common.InfoCard
import es.jvbabi.vplanplus.ui.common.SubjectIcon
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
    val context = LocalContext.current
    var runAutomatically by rememberSaveable { mutableStateOf(true) }

    LaunchedEffect(
        state.authenticationState,
        state.isBiometricEnabled
    ) {
        if (!runAutomatically) return@LaunchedEffect
        if (state.authenticationState == AuthenticationState.NONE && state.isBiometricEnabled && state.isBiometricSetUp) {
            runAutomatically = false
            gradesViewModel.authenticate(activity)
        }
    }

    GradesScreenContent(
        onBack = { navHostController.popBackStack() },
        onLinkVppId = { navHostController.navigate(Screen.SettingsVppIdScreen.route) },
        onHideBanner = { gradesViewModel.onHideBanner() },
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
        onStartAuthenticate = { gradesViewModel.authenticate(activity) },
        onOpenSecuritySettings = {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) activity.startActivity(Intent(Settings.ACTION_BIOMETRIC_ENROLL))
            else activity.startActivity(Intent(Settings.ACTION_SECURITY_SETTINGS))
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
        onToggleSubject = gradesViewModel::onToggleSubject,
        onToggleInterval = gradesViewModel::onToggleInterval,
        state = state,
        navBar = navBar
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class,
    ExperimentalAnimationGraphicsApi::class
)
@Composable
private fun GradesScreenContent(
    onBack: () -> Unit,
    onLinkVppId: () -> Unit,
    onHideBanner: () -> Unit,
    onDismissEnableBiometricBanner: () -> Unit,
    onEnableBiometric: () -> Unit,
    onStartAuthenticate: () -> Unit,
    onOpenSecuritySettings: () -> Unit,
    onDisableBiometric: () -> Unit,
    onStartCalculator: (List<Grade>) -> Unit,
    onToggleSubject: (Subject) -> Unit,
    onToggleInterval: (Interval) -> Unit,
    state: GradesState,
    navBar: @Composable (expanded: Boolean) -> Unit
) {
    var searchExpanded by rememberSaveable { mutableStateOf(false) }

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

            val allGrades = state.grades.flatMap { it.value.grades }
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
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            when (state.enabled) {
                GradeUseState.NO_VPP_ID -> NoVppId(onLinkVppId)
                GradeUseState.WRONG_PROFILE_SELECTED -> WrongProfile()
                else -> {}
            }
            AnimatedVisibility(
                visible = searchExpanded,
                enter = expandVertically(tween(200)),
                exit = shrinkVertically(tween(200))
            ) {
                Column {
                    Text(
                        text = stringResource(id = R.string.grades_filterTitle),
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(start = 8.dp, top = 8.dp)
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        state.grades.keys.sortedBy { it.name }.forEach { subject ->
                            FilterChip(
                                selected = state.visibleSubjects.contains(subject) && state.visibleSubjects.size != state.grades.size,
                                onClick = { onToggleSubject(subject) },
                                label = { Text(text = subject.short) },
                                modifier = Modifier.padding(horizontal = 4.dp),
                                leadingIcon = {
                                    SubjectIcon(
                                        subject = subject.name,
                                        modifier = Modifier,
                                        tint = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            )
                        }
                    }
                    Text(
                        text = stringResource(id = R.string.grades_filterIntervalsTitle),
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(start = 8.dp, top = 8.dp)
                    )
                    FlowRow(Modifier.fillMaxWidth()) {
                        state.intervals.keys.sortedBy { it.name }.forEach { interval ->
                            FilterChip(
                                selected = state.intervals[interval] ?: false,
                                onClick = { onToggleInterval(interval) },
                                label = { Text(text = interval.name) },
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                        }
                    }
                }
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

            if (state.isBiometricEnabled && state.authenticationState != AuthenticationState.AUTHENTICATED) {
                Authenticate { onStartAuthenticate() }
                return@Scaffold
            }
            if (state.enabled == GradeUseState.ENABLED && state.grades.isEmpty()) {
                NoGrades()
                return@Scaffold
            }
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
        onToggleSubject = {},
        onDismissEnableBiometricBanner = {},
        onEnableBiometric = {},
        onDisableBiometric = {},
        onToggleInterval = {}
    )
}

private val colorMatrix = floatArrayOf(
    -1f, 0f, 0f, 0f, 255f,
    0f, -1f, 0f, 0f, 255f,
    0f, 0f, -1f, 0f, 255f,
    0f, 0f, 0f, 1f, 0f
)
