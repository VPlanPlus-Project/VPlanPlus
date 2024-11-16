package es.jvbabi.vplanplus.feature.onboarding.stages.a_welcome.ui

import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.feature.onboarding.ui.common.OnboardingScreen
import es.jvbabi.vplanplus.ui.common.InfoCard
import es.jvbabi.vplanplus.ui.common.openLink
import es.jvbabi.vplanplus.ui.screens.Screen

@Composable
fun OnboardingWelcomeScreen(
    navController: NavHostController,
    viewModel: OnboardingWelcomeViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val state = viewModel.state

    Welcome(
        onPrivacyPolicy = { openLink(context, "${state.server.uiHost}/privacy") },
        onNext = { navController.navigate(Screen.OnboardingSchoolIdScreen.route) },
        onOpenAppInfo = {
            val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.data = android.net.Uri.parse("package:${context.packageName}")
            context.startActivity(intent)
            Toast.makeText(context, context.getString(R.string.onboarding_crashToast), Toast.LENGTH_LONG).show()
        }
    )
}

@Composable
fun Welcome(
    onPrivacyPolicy: () -> Unit,
    onNext: () -> Unit = {},
    onOpenAppInfo: () -> Unit = {}
) {
    var showCloseDialog by rememberSaveable { mutableStateOf(false) }
    BackHandler { showCloseDialog = true }

    OnboardingScreen(
        title = stringResource(id = R.string.app_name),
        text = { Text(text = stringResource(id = R.string.onboarding_welcomeText)) },
        buttonText = stringResource(id = R.string.lets_go),
        isLoading = false,
        enabled = true,
        onButtonClick = { onNext() },
        content = {
            InfoCard(
                imageVector = Icons.Default.Error,
                title = stringResource(id = R.string.onboarding_crashTitle),
                text = stringResource(id = R.string.onboarding_crashText),
                buttonAction1 = onOpenAppInfo,
                buttonText1 = stringResource(id = R.string.onboarding_crashButton)
            )
        },
        footer = {
            val footerText = buildAnnotatedString {
                withStyle(MaterialTheme.typography.labelMedium.toSpanStyle().copy(color = MaterialTheme.colorScheme.onSurface)) {
                    append(stringResource(id = R.string.onboarding_welcomeAcceptPrivacyPolicyStart))
                    append(" ")
                    withLink(
                        LinkAnnotation.Clickable(
                            linkInteractionListener = { onPrivacyPolicy() },
                            tag = "PRIVACY_POLICY"
                        )
                    ) {
                        withStyle(MaterialTheme.typography.labelMedium.toSpanStyle().copy(color = MaterialTheme.colorScheme.primary)) {
                            append(stringResource(id = R.string.onboarding_welcomeAcceptPrivacyPolicy))
                        }
                    }
                }
            }
            Text(footerText)
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun OnboardingWelcomeScreenPreview() {
    Welcome(
        onPrivacyPolicy = {},
    )
}