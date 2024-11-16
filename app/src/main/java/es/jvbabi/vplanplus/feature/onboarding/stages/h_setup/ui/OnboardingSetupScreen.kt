package es.jvbabi.vplanplus.feature.onboarding.stages.h_setup.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi
import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import es.jvbabi.vplanplus.MainActivity
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.feature.onboarding.ui.common.OnboardingScreen
import es.jvbabi.vplanplus.ui.screens.Screen

@Composable
fun OnboardingSetupScreen(
    navHostController: NavHostController,
    viewModel: OnboardingSetupViewModel = hiltViewModel()
) {
    val state = viewModel.state

    AnimatedVisibility(
        visible = state.isDone,
        enter = fadeIn(tween(500)),
    ) {
        StartAppScreen(
            onClick = {
                navHostController.navigate(Screen.HomeScreen.route) {
                    popUpTo(0)
                }
            },
            someErrorOccurred = state.hadError
        )
    }
    AnimatedVisibility(
        visible = !state.isDone,
        exit = fadeOut(tween(500)),
    ) {
        if (state.isFirstProfile) SetupNewSchoolScreen()
        else SetupNewProfileScreen()
    }
}

@OptIn(ExperimentalAnimationGraphicsApi::class)
@Composable
private fun StartAppScreen(
    onClick: () -> Unit,
    someErrorOccurred: Boolean = false
) {
    OnboardingScreen(
        title = stringResource(id = if (!someErrorOccurred) R.string.onboarding_setupDoneTitle else R.string.unknownError),
        text = { Text(text = stringResource(id = if (!someErrorOccurred) R.string.onboarding_setupDoneText else R.string.please_try_again)) },
        buttonText = stringResource(id = R.string.onboarding_setupDoneLetsGo),
        isLoading = false,
        enabled = !someErrorOccurred,
        onButtonClick = { onClick() },
        content = {}
    )
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val drawable = AnimatedImageVector.animatedVectorResource(
            if (MainActivity.isAppInDarkMode.value) R.drawable.avd_anim_dark
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
private fun SetupNewSchoolScreen() {
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
                LinearProgressIndicator(
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
    SetupNewSchoolScreen()
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun SetupDonePreview() {
    StartAppScreen(
        onClick = {},
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun SetupDoneWithErrorsPreview() {
    StartAppScreen(
        onClick = {},
        someErrorOccurred = true
    )
}