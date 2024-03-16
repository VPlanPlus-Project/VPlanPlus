package es.jvbabi.vplanplus.feature.grades.ui.view

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
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
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
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.feature.grades.domain.model.Grade
import es.jvbabi.vplanplus.feature.grades.domain.model.GradeModifier
import es.jvbabi.vplanplus.feature.grades.domain.model.Subject
import es.jvbabi.vplanplus.feature.grades.domain.usecase.GradeUseState
import es.jvbabi.vplanplus.feature.grades.ui.calculator.GradeCollection
import es.jvbabi.vplanplus.feature.grades.ui.components.Average
import es.jvbabi.vplanplus.feature.grades.ui.view.components.grades.GradeSubjectGroup
import es.jvbabi.vplanplus.feature.grades.ui.view.components.screens.Authenticate
import es.jvbabi.vplanplus.feature.grades.ui.view.components.screens.NoGrades
import es.jvbabi.vplanplus.feature.grades.ui.view.components.screens.NoVppId
import es.jvbabi.vplanplus.feature.grades.ui.view.components.screens.WrongProfile
import es.jvbabi.vplanplus.ui.common.BackIcon
import es.jvbabi.vplanplus.ui.common.InfoCard
import es.jvbabi.vplanplus.ui.common.SubjectIcon
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
    state: GradesState,
    navBar: @Composable () -> Unit
) {
    var searchExpanded by rememberSaveable { mutableStateOf(false) }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.grades_title)) },
                navigationIcon = { IconButton(onClick = onBack) { BackIcon() } },
                actions = {
                    if (state.enabled == GradeUseState.ENABLED) IconButton(onClick = { searchExpanded = !searchExpanded }) {
                        val painter = rememberAnimatedVectorPainter(
                            animatedImageVector = AnimatedImageVector.animatedVectorResource(R.drawable.anim_search_close),
                            atEnd = searchExpanded
                        )
                        Image(
                            painter = painter,
                            contentDescription = null,
                            modifier = Modifier.scale(.8f),
                            colorFilter =
                                if (isSystemInDarkTheme()) ColorFilter.colorMatrix(ColorMatrix(colorMatrix))
                                else null
                        )
                    }
                }
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
                        horizontalArrangement = Arrangement.Center
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
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Text(text = stringResource(id = R.string.grades_latest), style = MaterialTheme.typography.headlineSmall)
                        LazyRow {
                            items(state.latestGrades) { grade ->
                                LatestGrade(
                                    Modifier.padding(end = 8.dp),
                                    grade.value.toInt(),
                                    grade.modifier,
                                    grade.subject.short
                                )
                            }
                        }
                        HorizontalDivider(modifier = Modifier.padding(top = 8.dp))
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
        onDisableBiometric = {}
    )
}

private val colorMatrix = floatArrayOf(
    -1f, 0f, 0f, 0f, 255f,
    0f, -1f, 0f, 0f, 255f,
    0f, 0f, -1f, 0f, 255f,
    0f, 0f, 0f, 1f, 0f
)

@Composable
private fun LatestGrade(modifier: Modifier = Modifier, value: Int, gradeModifier: GradeModifier, subject: String)  {
    val colorScheme = MaterialTheme.colorScheme
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .size(70.dp)
            .clip(RoundedCornerShape(8.dp))
            .drawWithContent {
                drawRect(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            colorScheme.primary,
                            colorScheme.tertiary
                        )
                    ),
                    topLeft = Offset(0f, 0f),
                    size = Size(size.width, size.height)
                )
                drawContent()
            }
    ) {
        Text(
            text = "$value${gradeModifier.symbol}",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onPrimary,
        )
        Text(
            text = subject,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onPrimary,
        )
    }
}

@Preview
@Composable
private fun LatestGradePreview() {
    LatestGrade(
        value = 2,
        gradeModifier = GradeModifier.MINUS,
        subject = "Math"
    )
}