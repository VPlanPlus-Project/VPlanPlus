package es.jvbabi.vplanplus.feature.settings.profile.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.ui.common.BackIcon
import es.jvbabi.vplanplus.ui.common.DOT
import es.jvbabi.vplanplus.ui.common.InfoCard
import es.jvbabi.vplanplus.ui.common.SelectableCard
import es.jvbabi.vplanplus.ui.common.SettingsCategory
import es.jvbabi.vplanplus.ui.common.SettingsSetting
import es.jvbabi.vplanplus.ui.common.SettingsType
import es.jvbabi.vplanplus.ui.preview.GroupPreview
import es.jvbabi.vplanplus.ui.preview.ProfilePreview
import java.util.UUID

@Composable
fun ProfileSettingsDefaultLessonScreen(
    profileId: UUID,
    navController: NavHostController,
    viewModel: ProfileSettingsDefaultLessonsViewModel = hiltViewModel()
) {
    val state = viewModel.state

    LaunchedEffect(key1 = profileId, block = {
        viewModel.init(profileId)
    })
    ProfileSettingsDefaultLessonContent(
        state = state,
        onBackClicked = { navController.navigateUp() },
        onEvent = { event -> viewModel.onEvent(event) },
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ProfileSettingsDefaultLessonContent(
    state: ProfileSettingsDefaultLessonsState,
    onBackClicked: () -> Unit,
    onEvent: (event: ProfileSettingsDefaultLessonsEvent) -> Unit,
) {
    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = { Text(text = stringResource(id = R.string.settings_profileManagementDefaultLessonSettingsTitle)) },
                scrollBehavior = scrollBehavior,
                navigationIcon = {
                    IconButton(onClick = { onBackClicked() }) {
                        BackIcon()
                    }
                }
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            if (state.profile == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
                return@Column
            }
            if (state.differentDefaultLessons) {
                InfoCard(
                    modifier = Modifier.padding(8.dp),
                    imageVector = Icons.Default.Warning,
                    title = stringResource(id = R.string.settings_profileDefaultLessonDifferentDefaultLessonsTitle),
                    text = stringResource(id = R.string.settings_profileDefaultLessonDifferentDefaultLessonsText),
                    buttonAction1 = { onEvent(ProfileSettingsDefaultLessonsEvent.FixDefaultLessons) },
                    buttonText1 = stringResource(id = R.string.fix)
                )
            }
            LazyColumn {
                if (!state.courseGroups.isNullOrEmpty()) item {
                    SettingsCategory(title = stringResource(id = R.string.settingsProfileManagementDefaultLesson_courseGroupsTitle)) {
                        FlowRow(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        ) {
                            state.courseGroups.forEach { group ->
                                val allEnabled = state.profile.defaultLessons.filter { it.key.courseGroup == group }.all { it.value }
                                SelectableCard(
                                    modifier = Modifier
                                        .weight(1f)
                                        .defaultMinSize(48.dp, 48.dp),
                                    isSelected = allEnabled,
                                    onToggleSelected = {
                                        state.profile.defaultLessons.forEach { (key, _) ->
                                            if (key.courseGroup == group) onEvent(ProfileSettingsDefaultLessonsEvent.DefaultLessonChanged(key, !allEnabled))
                                        }
                                    }
                                ) {
                                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                        Text(text = group)
                                    }
                                }
                            }
                        }
                        HorizontalDivider()
                    }
                }
                item {
                    SettingsCategory(title = stringResource(id = R.string.settingsProfileManagementDefaultLesson_lessonsTitle)) {
                        state.profile.defaultLessons.entries.sortedBy { it.key.subject + (it.key.teacher?.acronym ?: "A") + it.key.vpId }.forEach { (defaultLesson, enabled) ->
                            SettingsSetting(
                                icon = null,
                                title = defaultLesson.subject,
                                subtitle = buildString {
                                    append(defaultLesson.teacher?.acronym ?: stringResource(id = R.string.settings_profileDefaultLessonNoTeacher))
                                    if (defaultLesson.courseGroup != null) append(" $DOT ${defaultLesson.courseGroup}")
                                    if (state.isDebug) append(" $DOT ${defaultLesson.vpId}")
                                },
                                type = SettingsType.TOGGLE,
                                enabled = true,
                                checked = enabled,
                                doAction = { onEvent(ProfileSettingsDefaultLessonsEvent.DefaultLessonChanged(defaultLesson, !enabled)) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileSettingsDefaultLessonScreenPreview() {
    val group = GroupPreview.generateGroup()
    ProfileSettingsDefaultLessonContent(
        state = ProfileSettingsDefaultLessonsState(
            differentDefaultLessons = true,
            profile = ProfilePreview.generateClassProfile(group),
            courseGroups = listOf("MA1", "MA2", "DE1", "SPO1")
        ),
        onBackClicked = {},
        onEvent = {}
    )
}