package es.jvbabi.vplanplus.feature.homework.add.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.domain.model.DefaultLesson
import es.jvbabi.vplanplus.ui.common.DOT
import es.jvbabi.vplanplus.ui.common.SelectDialog
import es.jvbabi.vplanplus.ui.common.SettingsSetting
import es.jvbabi.vplanplus.ui.common.SettingsType

@Composable
fun AddHomeworkScreen(
    navHostController: NavHostController,
    viewModel: AddHomeworkViewModel = hiltViewModel()
) {
    val state = viewModel.state.value
    AddHomeworkContent(
        onBack = { navHostController.popBackStack() },
        onOpenDefaultLessonDialog = { viewModel.setLessonDialogOpen(true) },
        onCloseDefaultLessonDialog = { viewModel.setLessonDialogOpen(false) },
        onSetDefaultLesson = { viewModel.setDefaultLesson(it) },
        state = state
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddHomeworkContent(
    onBack: () -> Unit = {},
    onOpenDefaultLessonDialog: () -> Unit = {},
    onCloseDefaultLessonDialog: () -> Unit = {},
    onSetDefaultLesson: (DefaultLesson?) -> Unit = {},
    state: AddHomeworkState
) {
    val noTeacher = stringResource(id = R.string.settings_profileDefaultLessonNoTeacher)
    if (state.isLessonDialogOpen) {
        SelectDialog(
            icon = Icons.Default.School,
            title = stringResource(id = R.string.addHomework_defaultLessonTitle),
            items = state.defaultLessons.sortedBy { it.subject },
            value = state.selectedDefaultLesson,
            itemToString = { it.subject + " $DOT " + (it.teacher?.acronym ?: noTeacher) },
            onDismiss = onCloseDefaultLessonDialog,
            onOk = { onSetDefaultLesson(it) }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.home_addHomeworkLabel)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(id = R.string.close)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues)
        ) {
            val withoutTeacher = stringResource(id = R.string.addHomework_lessonSubtitleNoTeacher, state.selectedDefaultLesson?.subject ?: "")
            SettingsSetting(
                icon = Icons.Default.School,
                title = stringResource(id = R.string.addHomework_lesson),
                subtitle =
                    if (state.selectedDefaultLesson == null) stringResource(id = R.string.addHomework_lessonNotSelected)
                    else if (state.selectedDefaultLesson.teacher == null) withoutTeacher
                    else stringResource(
                        id = R.string.addHomework_lessonSubtitle,
                        state.selectedDefaultLesson.subject,
                        state.selectedDefaultLesson.teacher.acronym
                    ),
                type = SettingsType.SELECT,
                doAction = onOpenDefaultLessonDialog,
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun AddHomeworkScreenPreview() {
    AddHomeworkContent(
        state = AddHomeworkState(
            isLessonDialogOpen = true
        )
    )
}