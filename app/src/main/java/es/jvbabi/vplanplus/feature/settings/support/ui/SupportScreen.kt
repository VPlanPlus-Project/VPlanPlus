package es.jvbabi.vplanplus.feature.settings.support.ui

import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeviceUnknown
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.feature.settings.support.domain.usecase.FeedbackError
import es.jvbabi.vplanplus.ui.common.BackIcon
import es.jvbabi.vplanplus.ui.common.IconSettingsState
import es.jvbabi.vplanplus.ui.common.InfoCard
import es.jvbabi.vplanplus.ui.common.Setting
import es.jvbabi.vplanplus.ui.common.SettingsType
import es.jvbabi.vplanplus.ui.common.SmallProgressIndicator

@Composable
fun SupportScreen(
    navHostController: NavHostController,
    viewModel: SupportScreenViewModel = hiltViewModel()
) {
    val state = viewModel.state.value
    val context = LocalContext.current

    SupportScreenContent(
        onBack = { navHostController.navigateUp() },
        onEvent = viewModel::onEvent,
        state = state
    )

    LaunchedEffect(key1 = state.sendState) {
        if (state.sendState == FeedbackSendState.SUCCESS) {
            navHostController.navigateUp()
            Toast.makeText(
                context,
                context.getString(R.string.settingsSupport_feedbackSent),
                LENGTH_SHORT
            ).show()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SupportScreenContent(
    onBack: () -> Unit = {},
    onEvent: (SupportScreenEvent) -> Unit = {},
    state: SupportScreenState
) {
    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = { Text(text = stringResource(id = R.string.settingsSupport_title)) },
                navigationIcon = { IconButton(onClick = onBack) { BackIcon() } },
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { onEvent(SupportScreenEvent.Send) },
                text = {
                    val alpha = animateFloatAsState(
                        targetValue =
                            if (state.isLoading) 1f
                            else 0f,
                        label = "Send Button Alpha"
                    ).value
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        SmallProgressIndicator(Modifier.alpha(alpha))
                        Text(
                            modifier = Modifier.alpha(1f - alpha),
                            text = stringResource(id = R.string.settingsSupport_send)
                        )
                    }
                },
                icon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.Send,
                        contentDescription = null
                    )
                },
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            TextField(
                value = state.feedback,
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxSize(),
                minLines = 5,
                placeholder = { Text(text = stringResource(id = R.string.settingsSupport_fieldPlaceholder)) },
                onValueChange = { onEvent(SupportScreenEvent.SetFeedback(it)) },
                isError = state.feedbackError != null,
                supportingText = {
                    Text(
                        text =
                        when (state.feedbackError) {
                            FeedbackError.EMPTY -> stringResource(id = R.string.settingsSupport_feedbackCantBeEmpty)
                            FeedbackError.TOO_SHORT -> stringResource(id = R.string.settingsSupport_feedbackTooShort)
                            else -> ""
                        },
                        color = MaterialTheme.colorScheme.error
                    )
                }
            )
            Setting(
                IconSettingsState(
                    imageVector = Icons.Default.DeviceUnknown,
                    title = stringResource(id = R.string.settingsSupport_attachSystemDetailsTitle),
                    subtitle = stringResource(id = R.string.settingsSupport_attachSystemDetailsSubtitle),
                    type = SettingsType.TOGGLE,
                    checked = state.attachSystemDetails,
                    doAction = { onEvent(SupportScreenEvent.ToggleSystemDetails) }
                )
            )
            Setting(
                IconSettingsState(
                    imageVector = Icons.Default.AlternateEmail,
                    title = stringResource(id = R.string.settingsSupport_emailTitle),
                    subtitle = stringResource(id = R.string.settingsSupport_emailSubtitle),
                    type = SettingsType.DISPLAY,
                    clickable = false,
                    doAction = {},
                    customContent = {
                        TextField(
                            modifier = Modifier
                                .padding(start = 60.dp, top = 8.dp, end = 8.dp, bottom = 8.dp)
                                .fillMaxSize(),
                            value = state.email ?: "",
                            placeholder = { Text(text = stringResource(id = R.string.settingsSupport_emailTitle)) },
                            onValueChange = { onEvent(SupportScreenEvent.UpdateEmail(it)) },
                            singleLine = true,
                            trailingIcon = {
                                IconButton(onClick = { onEvent(SupportScreenEvent.UpdateEmail("")) }) {
                                    Icon(imageVector = Icons.Default.Close, null)
                                }
                            },
                            isError = !state.emailValid,
                            supportingText = {
                                Text(
                                    text =
                                    if (state.emailValid) ""
                                    else stringResource(id = R.string.settingsSupport_emailInvalid),
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        )
                    }
                )
            )
            AnimatedVisibility(
                visible = state.sendState == FeedbackSendState.ERROR,
                enter = expandVertically(tween(250)),
                exit = shrinkVertically(tween(250))
            ) {
                InfoCard(
                    modifier = Modifier.padding(8.dp),
                    imageVector = Icons.Default.Error,
                    title = stringResource(id = R.string.something_went_wrong),
                    text = stringResource(id = R.string.settingsSupport_feedbackSendError)
                )
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun SupportScreenPreview() {
    SupportScreenContent(
        state = SupportScreenState(
            feedbackError = null,
            isLoading = true
        )
    )
}