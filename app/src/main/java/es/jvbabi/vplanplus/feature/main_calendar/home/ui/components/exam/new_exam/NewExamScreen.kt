package es.jvbabi.vplanplus.feature.main_calendar.home.ui.components.exam.new_exam

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import es.jvbabi.vplanplus.ui.common.SmallDragHandler
import kotlinx.coroutines.launch
import java.time.LocalDate


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewExamBottomSheet(
    viewModel: NewExamViewModel = hiltViewModel(),
    date: LocalDate? = null,
    onDismissRequest: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.doAction(NewExamUiEvent.OnInit(date = date))
    }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        dragHandle = { SmallDragHandler() },
        windowInsets = BottomSheetDefaults.windowInsets.only(WindowInsetsSides.Bottom),
        modifier = Modifier.padding(top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding() + 8.dp),
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .padding(bottom = WindowInsets.systemBars.asPaddingValues().calculateBottomPadding(), top = 4.dp)
                .fillMaxHeight()
        ) {
            NewExamContent(
                state = state,
                onBack = { scope.launch { sheetState.hide(); onDismissRequest() } },
                doAction = { viewModel.doAction(it) }
            )
        }
    }
    LaunchedEffect(state.saveSuccess) {
        if (state.saveSuccess == true) scope.launch {
            sheetState.hide()
            onDismissRequest()
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
        Column(Modifier.verticalScroll(rememberScrollState())) {
            AddExamDateSection(state.date) { doAction(NewExamUiEvent.UpdateDate(it)) }
            AddExamSubjectSection(state.subjects, state.currentLessons, state.subject, state.isDeveloperModeEnabled) { doAction(NewExamUiEvent.UpdateSubject(it)) }
            AddExamCategorySection(state.category) { doAction(NewExamUiEvent.UpdateCategory(it)) }
            AddExamReminderSection(state.remindDaysBefore, state.date, state.category) { doAction(NewExamUiEvent.UpdateReminderDays(it)) }
            AddExamDetailsSection(state.details) { doAction(NewExamUiEvent.UpdateDescription(it)) }
            if (state.currentProfile?.vppId != null) AddExamStorageSection(state.storeType, state.currentProfile.vppId.name, state.currentProfile.group.name) { doAction(NewExamUiEvent.UpdateStoreType(it)) }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NewExamScreenPreview() {
    NewExamContent(
        state = NewExamState()
    )
}