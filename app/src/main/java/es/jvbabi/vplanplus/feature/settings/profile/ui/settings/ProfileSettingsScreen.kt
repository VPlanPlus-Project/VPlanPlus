package es.jvbabi.vplanplus.feature.settings.profile.ui.settings

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Link
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
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.data.model.ProfileCalendarType
import es.jvbabi.vplanplus.data.model.ProfileType
import es.jvbabi.vplanplus.domain.model.Calendar
import es.jvbabi.vplanplus.ui.common.BackIcon
import es.jvbabi.vplanplus.ui.common.BigButton
import es.jvbabi.vplanplus.ui.common.BigButtonGroup
import es.jvbabi.vplanplus.ui.common.InputDialog
import es.jvbabi.vplanplus.ui.common.RadioCard
import es.jvbabi.vplanplus.ui.common.RadioCardGroup
import es.jvbabi.vplanplus.ui.common.SelectDialog
import es.jvbabi.vplanplus.ui.common.SettingsCategory
import es.jvbabi.vplanplus.ui.common.SettingsSetting
import es.jvbabi.vplanplus.ui.common.SettingsType
import es.jvbabi.vplanplus.ui.common.YesNoDialog
import es.jvbabi.vplanplus.ui.preview.ClassesPreview
import es.jvbabi.vplanplus.ui.preview.ProfilePreview
import es.jvbabi.vplanplus.ui.preview.VppIdPreview
import es.jvbabi.vplanplus.ui.screens.Screen
import java.util.UUID

@Composable
fun ProfileSettingsScreen(
    navController: NavHostController,
    viewModel: ProfileSettingsViewModel = hiltViewModel(),
    profileId: UUID
) {

    val state = viewModel.state.value
    val context = LocalContext.current

    LaunchedEffect(key1 = profileId, block = {
        viewModel.init(profileId = profileId, context = context)
    })

    val readLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = {
            viewModel.updatePermissionState(it)
            if (!it) Toast.makeText(
                context,
                context.getString(R.string.permission_denied_forever),
                Toast.LENGTH_LONG
            ).show()
        },
    )

    val writeLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = {
               if (it) readLauncher.launch(android.Manifest.permission.READ_CALENDAR)
               else viewModel.dismissPermissionDialog()
        },
    )

    if (state.initDone) {
        if (state.profile == null) {
            navController.navigateUp()
            return
        }
        ProfileSettingsScreenContent(
            state = state,
            onBackClicked = { navController.navigateUp() },
            onProfileDeleteDialogYes = {
                viewModel.deleteProfile()
                navController.navigateUp()
            },
            onProfileRenamed = viewModel::renameProfile,
            onCalendarModeSet = viewModel::setCalendarMode,
            onCalendarSet = { viewModel.setCalendar(it.id) },
            onDefaultLessonsClicked = {
                navController.navigate(
                    Screen.SettingsProfileDefaultLessonsScreen.route.replace(
                        "{profileId}", profileId.toString()
                    )
                )
            },
            onSetDialogVisible = { viewModel.setDialogOpen(it) },
            onSetDialogCall = { viewModel.setDialogCall(it) },
            onOpenVppIdSettings = {
                if (state.linkedVppId == null) navController.navigate(Screen.SettingsVppIdScreen.route)
                else navController.navigate(Screen.SettingsVppIdManageScreen.route + "/${state.linkedVppId.id}")
            },
            onLaunchPermissionDialog = { writeLauncher.launch(android.Manifest.permission.WRITE_CALENDAR) },
            onDismissedPermissionDialog = { viewModel.dismissPermissionDialog() }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileSettingsScreenContent(
    state: ProfileSettingsState,
    onBackClicked: () -> Unit,
    onProfileDeleteDialogYes: () -> Unit = {},
    onProfileRenamed: (String) -> Unit = {},
    onCalendarModeSet: (ProfileCalendarType) -> Unit = {},
    onCalendarSet: (Calendar) -> Unit = {},
    onSetDialogVisible: (Boolean) -> Unit = {},
    onSetDialogCall: (@Composable () -> Unit) -> Unit = {},
    onDefaultLessonsClicked: () -> Unit = {},
    onLaunchPermissionDialog: () -> Unit = {},
    onDismissedPermissionDialog: () -> Unit = {},
    onOpenVppIdSettings: () -> Unit = {}
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
                    if (state.profile.originalName == state.profile.displayName) Text(
                        text = stringResource(
                            id = R.string.settings_profileManagementScreenTitle,
                            state.profile.originalName
                        )
                    )
                    else Text(
                        text = stringResource(
                            id = R.string.settings_profileManagementScreenTitle,
                            "${state.profile.displayName} (${state.profile.originalName})"
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
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { paddingValues ->
        if (state.dialogOpen) {
            state.dialogCall()
        }
        if (deleteDialogOpen) {
            YesNoDialog(
                icon = Icons.Default.Delete,
                title = stringResource(id = R.string.profileManagement_deleteProfileDialogTitle),
                message = stringResource(
                    id = R.string.profileManagement_deleteProfileDialogText,
                    state.profile.originalName
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
                placeholder = state.profile.originalName,
                message = stringResource(id = R.string.settings_profileManagementScreenRenameProfileDialogText),
                onOk = {
                    if (it?.isNotEmpty() == true) onProfileRenamed(it)
                    else onProfileRenamed(state.profile.originalName)
                    renameDialogOpen = false
                },
            )
        }
        Column(
            modifier = Modifier
                .padding(paddingValues = paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
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
                            selected = state.profile.calendarType == ProfileCalendarType.DAY
                        ),
                        RadioCard(
                            icon = Icons.Outlined.CalendarMonth,
                            title = stringResource(id = R.string.settings_profileManagementCalendarLessonsTitle),
                            subtitle = stringResource(id = R.string.settings_profileManagementCalendarLessonsText),
                            onClick = { onCalendarModeSet(ProfileCalendarType.LESSON) },
                            selected = state.profile.calendarType == ProfileCalendarType.LESSON
                        ),
                        RadioCard(
                            icon = Icons.Outlined.EventBusy,
                            title = stringResource(id = R.string.settings_profileManagementCalendarNoneTitle),
                            subtitle = stringResource(id = R.string.settings_profileManagementCalendarNoneText),
                            onClick = { onCalendarModeSet(ProfileCalendarType.NONE) },
                            selected = state.profile.calendarType == ProfileCalendarType.NONE
                        )
                    )
                )
                SettingsSetting(
                    icon = Icons.Default.EditCalendar,
                    title = stringResource(id = R.string.settings_profileManagementCalendarNameTitle),
                    type = SettingsType.SELECT,
                    enabled = state.profile.calendarType != ProfileCalendarType.NONE && state.calendars.isNotEmpty() && state.calendarPermissionState == CalendarPermissionState.GRANTED,
                    subtitle =
                    if (state.profile.calendarType == ProfileCalendarType.NONE) stringResource(id = R.string.settings_profileManagementCalendarNameDisabled)
                    else if (state.calendars.isEmpty()) stringResource(id = R.string.settings_profileManagementNoCalendars)
                    else state.profileCalendar?.displayName
                        ?: stringResource(id = R.string.settings_profileManagementCalendarNameNone),
                    doAction = {
                        onSetDialogCall {
                            SelectDialog(
                                icon = Icons.Default.EditCalendar,
                                title = stringResource(id = R.string.settings_profileManagementCalendarNameTitle),
                                items = state.calendars.sortedBy { it.owner + it.displayName },
                                itemToString = { "${it.displayName} (${it.owner})" },
                                onDismiss = { onSetDialogVisible(false) },
                                value = state.profileCalendar,
                                onOk = {
                                    if (it == null) return@SelectDialog
                                    onCalendarSet(it)
                                    onSetDialogVisible(false)
                                }
                            )
                        }
                        onSetDialogVisible(true)
                    }
                )
            }

            SettingsCategory(
                title = stringResource(id = R.string.profileManagement_defaultLessonsTitle)
            ) {
                if (state.profile.type == ProfileType.STUDENT) SettingsSetting(
                    icon = Icons.Default.FilterAlt,
                    title = stringResource(id = R.string.settings_profileManagementDefaultLessonSettingsTitle),
                    subtitle = stringResource(
                        id = R.string.settings_profileManagementDefaultLessonSettingsText,
                        state.profile.defaultLessons.values.count { !it }
                    ),
                    type = SettingsType.FUNCTION,
                    doAction = {
                        onDefaultLessonsClicked()
                    })
            }
            if (state.profile.type == ProfileType.STUDENT) SettingsCategory(
                title = stringResource(
                    id = R.string.profileManagement_vppIDCategoryTitle
                )
            ) {
                if (state.linkedVppId == null) {
                    SettingsSetting(
                        icon = Icons.Default.Link,
                        title = stringResource(id = R.string.profileManagement_vppIDTitle),
                        subtitle = stringResource(id = R.string.profileManagement_vppIDNotLinked),
                        type = SettingsType.FUNCTION,
                        doAction = onOpenVppIdSettings
                    )
                } else {
                    SettingsSetting(
                        icon = Icons.Default.Link,
                        title = stringResource(id = R.string.profileManagement_vppIDTitle),
                        subtitle = state.linkedVppId.name,
                        type = SettingsType.FUNCTION,
                        doAction = onOpenVppIdSettings
                    )
                }
            }
        }
    }

    if (state.calendarPermissionState == CalendarPermissionState.SHOW_DIALOG) {
        YesNoDialog(
            icon = Icons.Default.EditCalendar,
            title = stringResource(id = R.string.settings_profileManagementCalendarPermissionDialogTitle),
            message = stringResource(id = R.string.settings_profileManagementCalendarPermissionDialogText),
            onNo = onDismissedPermissionDialog,
            onYes = onLaunchPermissionDialog
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun ProfileSettingsScreenPreview() {
    val classes = ClassesPreview.generateClass(null)
    ProfileSettingsScreenContent(
        state = ProfileSettingsState(
            profile = ProfilePreview.generateClassProfile().copy(calendarType = ProfileCalendarType.DAY),
            linkedVppId = VppIdPreview.generateVppId(classes)
        ),
        onBackClicked = {}
    )
}