package es.jvbabi.vplanplus.feature.main_grades.view.ui.calculator

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.filled.Filter1
import androidx.compose.material.icons.filled.Filter2
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.feature.main_grades.view.ui.calculator.components.CollectionGroup
import es.jvbabi.vplanplus.feature.main_grades.view.ui.components.Average
import es.jvbabi.vplanplus.ui.common.BackIcon
import es.jvbabi.vplanplus.ui.common.InfoDialog

@Composable
fun GradeCalculatorScreen(
    navHostController: NavHostController,
    viewModel: GradeCalculatorViewModel = hiltViewModel(),
    grades: List<GradeCollection>,
    isSek2: Boolean
) {
    val state = viewModel.state.value
    LaunchedEffect(grades, isSek2) { viewModel.init(grades, isSek2, true) }
    GradeCalculatorContent(
        state = state,
        onBack = { navHostController.popBackStack() },
        onAddGrade = { collection, grade -> viewModel.addGrade(collection, grade) },
        onRemoveGrade = { collection, index -> viewModel.removeGradeByIndex(collection, index) },
        onRestoreGrades = viewModel::restore,
        onChangeSek2 = viewModel::onSek2Change
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GradeCalculatorContent(
    onBack: () -> Unit = {},
    onAddGrade: (String, Float) -> Unit = { _, _ -> },
    onRemoveGrade: (String, Int) -> Unit = { _, _ -> },
    onRestoreGrades: () -> Unit = {},
    onChangeSek2: (isSek2: Boolean) -> Unit = {},
    state: GradeCalculatorState
) {
    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    var infoDialogOpen by rememberSaveable { mutableStateOf(false) }
    if (infoDialogOpen) InfoDialog(
        icon = Icons.AutoMirrored.Outlined.HelpOutline,
        title = stringResource(id = R.string.info),
        message = stringResource(id = R.string.gradesCalculator_text),
        onOk = { infoDialogOpen = false }
    )

    var showSek2Toggle by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = { Text(text = stringResource(id = R.string.gradesCalculator_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) { BackIcon() }
                },
                actions = {
                    IconButton(onClick = { showSek2Toggle = !showSek2Toggle }) {
                        Icon(
                            imageVector =
                                if (state.isSek2) Icons.Default.Filter2
                                else Icons.Default.Filter1,
                            contentDescription = null
                        )
                    }
                    DropdownMenu(expanded = showSek2Toggle, onDismissRequest = { showSek2Toggle = false }) {
                        DropdownMenuItem(
                            text = { Text(stringResource(id = R.string.grades_sek1)) },
                            leadingIcon = { RadioIcon(!state.isSek2) },
                            onClick = { onChangeSek2(false); showSek2Toggle = false }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(id = R.string.grades_sek2)) },
                            leadingIcon = { RadioIcon(state.isSek2) },
                            onClick = { onChangeSek2(true); showSek2Toggle = false }
                        )
                    }
                    IconButton(onClick = { infoDialogOpen = true }) {
                        Icon(Icons.AutoMirrored.Outlined.HelpOutline, contentDescription = stringResource(id = R.string.info))
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValues ->
        Column(
            Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .verticalScroll(rememberScrollState())
        ) {
            Box(Modifier.fillMaxWidth()) {
                Average(Modifier.align(Alignment.Center), avg = state.avg, isSek2 = state.isSek2)
            }
            OutlinedButton(
                onClick = onRestoreGrades,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp, horizontal = 8.dp)
            ) {
                Text(text = stringResource(id = R.string.gradesCalculator_restore))
            }
            state.grades.forEach { collection ->
                CollectionGroup(
                    group = collection.name,
                    grades = collection.grades,
                    avg = collection.grades.map { it.first }.average(),
                    onAddGrade = { value -> onAddGrade(collection.name, value) },
                    onRemoveGrade = { index -> onRemoveGrade(collection.name, index) },
                    isSek2 = state.isSek2
                )
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun GradeCalculatorScreenPreview() {
    GradeCalculatorContent(
        state = GradeCalculatorState(
            grades = emptyList(),
            avg = 2.4
        )
    )
}

@Composable
private fun RadioIcon(enabled: Boolean) {
    Icon(
        imageVector =
            if (enabled) Icons.Default.RadioButtonChecked
            else Icons.Default.RadioButtonUnchecked,
        contentDescription =
            if (enabled) stringResource(id = R.string.selected)
            else stringResource(id = R.string.not_selected)
    )
}