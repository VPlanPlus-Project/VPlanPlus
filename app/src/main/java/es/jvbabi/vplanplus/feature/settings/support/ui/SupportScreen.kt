package es.jvbabi.vplanplus.feature.settings.support.ui

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
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.feature.settings.support.domain.usecase.FeedbackError
import es.jvbabi.vplanplus.ui.common.BackIcon
import es.jvbabi.vplanplus.ui.common.IconSettingsState
import es.jvbabi.vplanplus.ui.common.Setting
import es.jvbabi.vplanplus.ui.common.SettingsType

@Composable
fun SupportScreen(
    navHostController: NavHostController,
    viewModel: SupportScreenViewModel = hiltViewModel()
) {
    val state = viewModel.state.value

    SupportScreenContent(
        onBack = { navHostController.navigateUp() },
        onFeedbackChange = viewModel::onUpdateFeedback,
        onToggleAnonymousSend = viewModel::toggleSender,
        onToggleSystemDetails = viewModel::toggleAttachSystemDetails,
        onEmailChange = viewModel::onUpdateEmail,
        state = state
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SupportScreenContent(
    onBack: () -> Unit = {},
    onFeedbackChange: (String) -> Unit = {},
    onToggleAnonymousSend: () -> Unit = {},
    onToggleSystemDetails: () -> Unit = {},
    onEmailChange: (String) -> Unit = {},
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
                onClick = {},
                text = {
                    Text(text = stringResource(id = R.string.settingsSupport_send))
                },
                icon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.Send,
                        contentDescription = null
                    )
                }
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
                onValueChange = onFeedbackChange,
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
                    imageVector = Icons.Default.Person,
                    title = stringResource(id = R.string.settingsSupport_sendAnonymouslyTitle),
                    subtitle =
                    if (state.sender == SupportMessageSender.ANONYMOUS) stringResource(id = R.string.settingsSupport_sendAnonymouslyNoVppIdSubtitle)
                    else stringResource(id = R.string.settingsSupport_sendAnonymouslySubtitle),
                    type = SettingsType.TOGGLE,
                    enabled = state.sender != SupportMessageSender.ANONYMOUS,
                    checked = state.sender != SupportMessageSender.VPPID,
                    doAction = { onToggleAnonymousSend() }
                )
            )
            Setting(
                IconSettingsState(
                    imageVector = Icons.Default.DeviceUnknown,
                    title = stringResource(id = R.string.settingsSupport_attachSystemDetailsTitle),
                    subtitle = stringResource(id = R.string.settingsSupport_attachSystemDetailsSubtitle),
                    type = SettingsType.TOGGLE,
                    checked = state.attachSystemDetails,
                    doAction = { onToggleSystemDetails() }
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
                            onValueChange = onEmailChange,
                            singleLine = true,
                            trailingIcon = {
                                IconButton(onClick = { onEmailChange("") }) {
                                    Icon(imageVector = Icons.Default.Close, null)
                                }
                            }
                        )
                    }
                )
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun SupportScreenPreview() {
    SupportScreenContent(
        state = SupportScreenState(
            sender = SupportMessageSender.VPP_ID_ANONYMOUS,
            feedbackError = null
        )
    )
}