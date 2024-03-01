package es.jvbabi.vplanplus.feature.grades.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.feature.grades.domain.usecase.GradeUseState
import es.jvbabi.vplanplus.feature.grades.ui.components.error.NoGrades
import es.jvbabi.vplanplus.feature.grades.ui.components.error.NoVppId
import es.jvbabi.vplanplus.feature.grades.ui.components.error.NotActivated
import es.jvbabi.vplanplus.feature.grades.ui.components.error.WrongProfile
import es.jvbabi.vplanplus.feature.grades.ui.components.grades.GradeSubjectGroup
import es.jvbabi.vplanplus.feature.grades.ui.components.grades.LatestGrades
import es.jvbabi.vplanplus.shared.data.VppIdServer
import es.jvbabi.vplanplus.ui.common.BackIcon
import es.jvbabi.vplanplus.ui.common.InfoCard
import es.jvbabi.vplanplus.ui.screens.Screen
import java.math.RoundingMode

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
        onHideBanner = { gradesViewModel.onHideBanner() },
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
                if (state.avg != 0.0) item {
                    val colorScheme = MaterialTheme.colorScheme
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(16.dp)
                                .size(128.dp)
                                .clip(RoundedCornerShape(50))
                                .drawWithContent {
                                    val percentage = (6 - state.avg) / 5f
                                    drawRect(
                                        color = colorScheme.secondary,
                                        topLeft = Offset(0f, 0f),
                                        size = Size(size.width, size.height)
                                    )
                                    drawRect(
                                        brush = Brush.verticalGradient(
                                            listOf(
                                                colorScheme.primary,
                                                colorScheme.tertiary
                                            )
                                        ),
                                        topLeft = Offset(
                                            0f,
                                            size.height * (1 - percentage.toFloat())
                                        ),
                                        size = Size(size.width, size.height * percentage.toFloat())
                                    )
                                    drawContent()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Ø ${
                                    state.avg.toBigDecimal().setScale(2, RoundingMode.HALF_EVEN)
                                }",
                                style = MaterialTheme.typography.headlineLarge,
                                color = MaterialTheme.colorScheme.onSecondary
                            )
                        }
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
                        GradeSubjectGroup(grades = grades)
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
        navBar = {},
        state = GradesState(
            enabled = GradeUseState.WRONG_PROFILE_SELECTED
        )
    )
}