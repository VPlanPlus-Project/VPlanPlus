package es.jvbabi.vplanplus.ui.screens.settings.profile.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.EventBusy
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.domain.model.ProfileCalendarType
import es.jvbabi.vplanplus.ui.common.BackIcon
import es.jvbabi.vplanplus.ui.common.BigButton
import es.jvbabi.vplanplus.ui.common.BigButtonGroup
import es.jvbabi.vplanplus.ui.common.InputDialog
import es.jvbabi.vplanplus.ui.common.RadioCard
import es.jvbabi.vplanplus.ui.common.RadioCardGroup
import es.jvbabi.vplanplus.ui.common.SettingsCategory
import es.jvbabi.vplanplus.ui.common.SettingsSetting
import es.jvbabi.vplanplus.ui.common.SettingsType
import es.jvbabi.vplanplus.ui.common.YesNoDialog
import es.jvbabi.vplanplus.ui.preview.Profile

@Composable
fun ProfileSettingsScreen(
    navController: NavHostController,
    viewModel: ProfileSettingsViewModel = hiltViewModel(),
    profileId: Long
) {

    val state = viewModel.state.value
    val context = LocalContext.current

    LaunchedEffect(key1 = profileId, block = {
        viewModel.init(profileId = profileId, context = context)
    })

    if (state.initDone) ProfileSettingsScreenContent(
        state = state,
        onBackClicked = { navController.popBackStack() },
        onProfileDeleteDialogYes = {
            viewModel.deleteProfile(context)
            navController.popBackStack()
        },
        onProfileRenamed = {
            viewModel.renameProfile(it)
        },
        onCalendarModeSet = {
            viewModel.setCalendarMode(it)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileSettingsScreenContent(
    state: ProfileSettingsState,
    onBackClicked: () -> Unit,
    onProfileDeleteDialogYes: () -> Unit = {},
    onProfileRenamed: (String) -> Unit = {},
    onCalendarModeSet: (ProfileCalendarType) -> Unit = {}
) {
    if (state.profile == null) return

    var deleteDialogOpen by remember { mutableStateOf(false) }
    var renameDialogOpen by remember { mutableStateOf(false) }

    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    if (state.profile.name == state.profile.customName) Text(
                        text = stringResource(
                            id = R.string.settings_profileManagementScreenTitle,
                            state.profile.name
                        )
                    )
                    else Text(
                        text = stringResource(
                            id = R.string.settings_profileManagementScreenTitle,
                            "${state.profile.customName} (${state.profile.name})"
                        )
                    )
                },
                scrollBehavior = scrollBehavior,
                navigationIcon = {
                    IconButton(onClick = { onBackClicked() }) {
                        BackIcon()
                    }
                }
            )
        },
    ) { paddingValues ->
        if (deleteDialogOpen) {
            YesNoDialog(
                icon = Icons.Default.Delete,
                title = stringResource(id = R.string.profileManagement_deleteProfileDialogTitle),
                message = stringResource(
                    id = R.string.profileManagement_deleteProfileDialogText,
                    state.profile.name
                ),
                onYes = {
                    onProfileDeleteDialogYes()
                    deleteDialogOpen = false
                },
                onNo = {
                    deleteDialogOpen = false
                }
            )
        }
        if (renameDialogOpen) {
            InputDialog(
                icon = Icons.Default.Edit,
                title = stringResource(id = R.string.settings_profileManagementScreenRenameProfileButton),
                placeholder = state.profile.name,
                message = stringResource(id = R.string.settings_profileManagementScreenRenameProfileDialogText),
                onOk = {
                    if (it?.isNotEmpty() == true) onProfileRenamed(it)
                    else onProfileRenamed(state.profile.name)
                    renameDialogOpen = false
                },
            )
        }
        Column(modifier = Modifier.padding(paddingValues = paddingValues)) {
            BigButtonGroup(
                buttons = listOf(
                    BigButton(
                        Icons.Outlined.Delete,
                        stringResource(id = R.string.settings_profileManagementScreenDeleteProfileButton),
                        onClick = { deleteDialogOpen = true }),
                    BigButton(
                        Icons.Outlined.Edit,
                        stringResource(id = R.string.settings_profileManagementScreenRenameProfileButton),
                        onClick = { renameDialogOpen = true })
                ),
                modifier = Modifier.padding(16.dp)
            )

            SettingsCategory(
                title = stringResource(id = R.string.settings_profileManagementCalendarTitle),
            ) {
                RadioCardGroup(
                    modifier = Modifier.padding(top = 16.dp),
                    options = listOf(
                        RadioCard(
                            icon = Icons.Outlined.CalendarToday,
                            title = stringResource(id = R.string.settings_profileManagementCalendarDayTitle),
                            subtitle = stringResource(id = R.string.settings_profileManagementCalendarDayText),
                            onClick = { onCalendarModeSet(ProfileCalendarType.DAY) },
                            selected = state.profile.calendarMode == ProfileCalendarType.DAY
                        ),
                        RadioCard(
                            icon = Icons.Outlined.CalendarMonth,
                            title = stringResource(id = R.string.settings_profileManagementCalendarLessonsTitle),
                            subtitle = stringResource(id = R.string.settings_profileManagementCalendarLessonsText),
                            onClick = { onCalendarModeSet(ProfileCalendarType.LESSON) },
                            selected = state.profile.calendarMode == ProfileCalendarType.LESSON
                        ),
                        RadioCard(
                            icon = Icons.Outlined.EventBusy,
                            title = stringResource(id = R.string.settings_profileManagementCalendarNoneTitle),
                            subtitle = stringResource(id = R.string.settings_profileManagementCalendarNoneText),
                            onClick = { onCalendarModeSet(ProfileCalendarType.NONE) },
                            selected = state.profile.calendarMode == ProfileCalendarType.NONE
                        )
                    )
                )
                SettingsSetting(
                    icon = Icons.Default.EditCalendar,
                    title = stringResource(id = R.string.settings_profileManagementCalendarNameTitle),
                    type = SettingsType.SELECT,
                    enabled = state.profile.calendarMode != ProfileCalendarType.NONE,
                    subtitle =
                    if (state.profile.calendarMode != ProfileCalendarType.NONE) ""
                    else stringResource(id = R.string.settings_profileManagementCalendarNameDisabled),
                    doAction = {}
                )
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun ProfileSettingsScreenPreview() {
    ProfileSettingsScreenContent(
        state = ProfileSettingsState(profile = Profile.generateClassProfile()),
        onBackClicked = {}
    )
}