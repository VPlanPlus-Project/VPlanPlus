package es.jvbabi.vplanplus.feature.main_calendar.home.ui.components.exam.new_exam

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import es.jvbabi.vplanplus.R


@Composable
fun NewExamScreen(
    navHostController: NavHostController,
    viewModel: NewExamViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    NewExamContent(
        state = state,
        onBack = navHostController::navigateUp,
        doAction = viewModel::doAction
    )

    LaunchedEffect(state.saveSuccess) {
        if (state.saveSuccess == true) {
            navHostController.navigateUp()
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NewExamContent(
    state: NewExamState,
    onBack: () -> Unit = {},
    doAction: (action: NewExamUiEvent) -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.examsNew_title))
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(android.R.string.cancel)
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { doAction(NewExamUiEvent.OnSaveClicked) },
                        enabled = state.canSave
                    ) {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = stringResource(R.string.examsNew_save)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(Modifier.padding(paddingValues)) {
            SubjectSection(
                subjects = state.subjects,
                selectedSubject = state.subject,
                isExpanded = state.isSubjectsExpanded,
                onHeaderClicked = { doAction(NewExamUiEvent.OnSubjectsClicked) },
                onSubjectClicked = { doAction(NewExamUiEvent.OnSubjectSelected(it)) }
            )

            DateSection(
                selectedDate = state.date,
                isContentExpanded = state.isDateExpanded,
                onHeaderClicked = { doAction(NewExamUiEvent.OnDateClicked) },
                onDateSelected = { doAction(NewExamUiEvent.OnDateSelected(it)) }
            )

            TypeSection(
                selectedType = state.type,
                isContentExpanded = state.isTypeExpanded,
                onHeaderClicked = { doAction(NewExamUiEvent.OnTypeClicked) },
                onTypeSelected = { doAction(NewExamUiEvent.OnTypeSelected(it)) }
            )

            TopicSection(
                currentTopic = state.topic,
                isContentExpanded = state.isTopicExpanded,
                onHeaderClicked = { doAction(NewExamUiEvent.OnTopicClicked) },
                onTopicSelected = { doAction(NewExamUiEvent.OnTopicSelected(it)) }
            )

            DetailsSection(
                currentDetails = state.details,
                isContentExpanded = state.isDetailsExpanded,
                onHeaderClicked = { doAction(NewExamUiEvent.OnDetailsClicked) },
                onDetailsSelected = { doAction(NewExamUiEvent.OnDetailsSelected(it)) }
            )

            StorageSection(
                currentState = state.saveAndShare,
                isContentExpanded = state.isStorageExpanded,
                onHeaderClicked = { doAction(NewExamUiEvent.OnStorageClicked) },
                onTypeSelected = { doAction(NewExamUiEvent.OnStorageSelected(it)) }
            )
            
            if (state.date != null && state.type != null) ReminderSection(
                selectedDays = state.remindDaysBefore,
                selectedDate = state.date,
                selectedType = state.type,
                isContentExpanded = state.isReminderExpanded,
                onHeaderClicked = { doAction(NewExamUiEvent.OnReminderClicked) },
                onRemindDaysBeforeSelected = { doAction(NewExamUiEvent.OnRemindDaysBeforeSelected(it)) }
            )
        }
    }
}

@Preview
@Composable
fun NewExamScreenPreview() {
    NewExamContent(
        state = NewExamState()
    )
}