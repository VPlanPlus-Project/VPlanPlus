package es.jvbabi.vplanplus.feature.onboarding.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi
import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.feature.onboarding.domain.usecase.ProfileCreationStage
import es.jvbabi.vplanplus.feature.onboarding.domain.usecase.ProfileCreationStatus
import es.jvbabi.vplanplus.feature.onboarding.ui.common.OnboardingScreen
import es.jvbabi.vplanplus.ui.screens.Screen

@Composable
fun OnboardingSetupScreen(
    navHostController: NavHostController, viewModel: OnboardingViewModel
) {
    val state = viewModel.state.value

    AnimatedVisibility(
        visible = state.allDone,
        enter = fadeIn(tween(500)),
    ) {
        StartAppScreen(
            onClick = {
                navHostController.navigate(Screen.HomeScreen.route) {
                    popUpTo(0)
                }
            }
        )
    }
    AnimatedVisibility(
        visible = state.isLoading,
        exit = fadeOut(tween(500)),
    ) {
        if (state.task == Task.CREATE_SCHOOL) SetupNewSchoolScreen(state.creationStatus)
        if (state.task == Task.CREATE_PROFILE) SetupNewProfileScreen()
    }
}

@OptIn(ExperimentalAnimationGraphicsApi::class)
@Composable
private fun StartAppScreen(
    onClick: () -> Unit,
) {
    OnboardingScreen(
        title = stringResource(id = R.string.onboarding_setupDoneTitle),
        text = { Text(text = stringResource(id = R.string.onboarding_setupDoneText)) },
        buttonText = stringResource(id = R.string.onboarding_setupDoneLetsGo),
        isLoading = false,
        enabled = true,
        onButtonClick = { onClick() },
        content = {}
    )
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Center
    ) {
        val drawable = AnimatedImageVector.animatedVectorResource(
            if (isSystemInDarkTheme()) R.drawable.avd_anim_dark
            else R.drawable.avd_anim
        )
        var atEnd by remember { mutableStateOf(false) }
        LaunchedEffect(drawable) { atEnd = true }
        Image(
            painter = rememberAnimatedVectorPainter(animatedImageVector = drawable, atEnd = atEnd),
            contentDescription = null,
            modifier = Modifier
                .size(300.dp)
                .background(MaterialTheme.colorScheme.surface)
        )
    }
}

@Composable
private fun SetupNewSchoolScreen(
    creationStatus: ProfileCreationStatus
) {
    val alpha by rememberInfiniteTransition(label = "pulsetext").animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(500),
            repeatMode = RepeatMode.Reverse
        ), label = "pulsetext"
    )
    Surface {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Spacer(Modifier.windowInsetsTopHeight(WindowInsets.statusBars))
                Text(
                    text = stringResource(id = R.string.onboarding_setupSettingUpTitle),
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp)
                )
                Text(
                    text = stringResource(id = R.string.onboarding_setupSettingUpText),
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp)
                )
            }
            Column {
                Text(
                    text = when (creationStatus.profileCreationStage) {
                        ProfileCreationStage.INSERT_CLASSES -> stringResource(R.string.onboarding_setupInsertClasses)
                        ProfileCreationStage.INSERT_TEACHERS -> stringResource(R.string.onboarding_setupInsertTeachers)
                        ProfileCreationStage.INSERT_ROOMS -> stringResource(R.string.onboarding_setupInsertRooms)
                        ProfileCreationStage.INSERT_HOLIDAYS -> stringResource(R.string.onboarding_setupInsertHolidays)
                        ProfileCreationStage.INITIAL_SYNC -> stringResource(R.string.onboarding_setupInitialSync)
                        else -> ""
                    },
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = alpha)
                )
                if (creationStatus.progress != null) LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                    progress = { creationStatus.progress.toFloat() }
                ) else LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                )
                Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.systemBars))
            }
        }
    }
}

@Composable
private fun SetupNewProfileScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Center
    ) {
        CircularProgressIndicator()
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun SetupScreenSchoolPreview() {
    SetupNewSchoolScreen(
        ProfileCreationStatus(
            ProfileCreationStage.INSERT_CLASSES,
            null
        )
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun SetupDonePreview() {
    StartAppScreen(
        onClick = {},
    )
}