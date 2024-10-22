package es.jvbabi.vplanplus.feature.main_calendar.home.ui.components.exam.new_exam

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController


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


@Composable
private fun NewExamContent(
    state: NewExamState,
    onBack: () -> Unit = {},
    doAction: (action: NewExamUiEvent) -> Unit = {}
) {
    Column(Modifier.fillMaxSize()) root@{
        HeadNavigation(onBack) { doAction(NewExamUiEvent.OnSaveClicked) }
        AddExamTitleSection(state.topic) { doAction(NewExamUiEvent.UpdateTitle(it)) }
        AddExamDateSection(state.date) { doAction(NewExamUiEvent.UpdateDate(it)) }
        AddExamSubjectSection(state.subjects, state.currentLessons, state.subject, state.isDeveloperModeEnabled) { doAction(NewExamUiEvent.UpdateSubject(it)) }
        AddExamCategorySection(state.category) { doAction(NewExamUiEvent.UpdateCategory(it)) }
        AddExamReminderSection(state.remindDaysBefore, state.date, state.category) { doAction(NewExamUiEvent.UpdateReminderDays(it)) }
        AddExamDetailsSection(state.details) { doAction(NewExamUiEvent.UpdateDescription(it)) }
        if (state.currentProfile?.vppId != null) AddExamStorageSection(state.storeType, state.currentProfile.vppId.name, state.currentProfile.group.name) { doAction(NewExamUiEvent.UpdateStoreType(it)) }
    }
}

@Preview(showBackground = true)
@Composable
fun NewExamScreenPreview() {
    NewExamContent(
        state = NewExamState()
    )
}