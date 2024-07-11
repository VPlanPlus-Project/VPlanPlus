package es.jvbabi.vplanplus.feature.main_homework.list.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.feature.main_homework.list.domain.usecase.HomeworkListUseCases
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.Homework
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@Suppress("UNCHECKED_CAST")
@HiltViewModel
class HomeworkListViewModel @Inject constructor(
    private val homeworkListUseCases: HomeworkListUseCases
) : ViewModel() {
    var state by mutableStateOf(HomeworkListState())
        private set

    init {
        viewModelScope.launch {
            combine(listOf(
                homeworkListUseCases.getCurrentProfileUseCase(),
                homeworkListUseCases.getHomeworkUseCase(),
                homeworkListUseCases.updateHomeworkUseCase.isUpdateRunning()
            )) { data ->
                val profile = data[0] as? ClassProfile
                val homework = data[1] as List<Homework>
                val isUpdatingHomework = data[2] as Boolean

                state.copy(
                    userUsesFalseProfileType = profile == null,
                    profile = profile,
                    homework = homework,
                    isUpdatingHomework = isUpdatingHomework
                )
            }.collect { state = it }
        }
    }

    fun onEvent(event: HomeworkListEvent) {
        viewModelScope.launch {
            if (state.error != null) state = state.copy(error = null) // Clear error state only when an error existed to prevent card resetting
            when (event) {
                is HomeworkListEvent.DeleteOrHide -> {
                    val homework = (event.homework as? Homework.CloudHomework) ?: return@launch
                    if (homework.createdBy == state.profile?.vppId) {
                        homeworkListUseCases.deleteHomeworkUseCase(homework).let { success ->
                            if (!success) state = state.copy(error = HomeworkListError.DeleteOrHideError)
                        }
                    }
                    else homeworkListUseCases.toggleHomeworkHiddenStateUseCase(homework)
                }
                is HomeworkListEvent.MarkAsDone -> {
                    val homework = event.homework
                    homeworkListUseCases.markHomeworkAsDoneUseCase(homework).let { success ->
                        if (!success) state = state.copy(error = HomeworkListError.MarkAsDoneError)
                    }
                }
                is HomeworkListEvent.UpdateFilter -> {
                    val filter = event.filter
                    state = state.copy(filters = state.filters.map {
                        if (it::class == filter::class) filter else it
                    })
                }
                is HomeworkListEvent.ResetFilters -> state = state.copy(filters = HomeworkListState().filters)
                is HomeworkListEvent.RefreshHomework -> homeworkListUseCases.updateHomeworkUseCase()
            }
        }
    }
}

data class HomeworkListState(
    val userUsesFalseProfileType: Boolean = false,
    val profile: ClassProfile? = null,
    val homework: List<Homework> = emptyList(),
    val filters: List<HomeworkFilter> = listOf(HomeworkFilter.VisibilityFilter(true), HomeworkFilter.CompletionFilter(false)),

    val isUpdatingHomework: Boolean = false,

    val error: HomeworkListError? = null
) {
    inline fun <reified T: HomeworkFilter> getFilter(): T {
        return filters.filterIsInstance<T>().first()
    }
}

sealed interface HomeworkFilter {
    @Composable fun buildLabel(): String
    @Composable fun buildLeadingIcon(): (@Composable () -> Unit)?
    @Composable fun buildTrailingIcon(): (@Composable () -> Unit)? = { Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null) }
    fun filter(homework: Homework): Boolean

    /**
     * @param showVisible: null to show all, true to show only visible, false to show only hidden
     */
    data class VisibilityFilter(val showVisible: Boolean?) : HomeworkFilter {
        @Composable
        override fun buildLabel(): String {
            return when (showVisible) {
                true -> stringResource(id = R.string.homework_filterVisibilityVisible)
                false -> stringResource(id = R.string.homework_filterVisibilityHidden)
                null -> stringResource(id = R.string.homework_filterVisibilityName)
            }
        }

        override fun filter(homework: Homework): Boolean {
            if (showVisible == null) return true
            if (showVisible && homework is Homework.LocalHomework) return true
            if (showVisible && homework is Homework.CloudHomework && !homework.isHidden) return true
            if (!showVisible && homework is Homework.CloudHomework && homework.isHidden) return true
            return false
        }

        @Composable
        override fun buildLeadingIcon(): @Composable (() -> Unit) = {
            Icon(imageVector = Icons.Default.Visibility, contentDescription = null)
        }
    }

    /**
     * @param showCompleted: null to show all, true to show only completed, false to show only uncompleted
     */
    data class CompletionFilter(val showCompleted: Boolean?) : HomeworkFilter {
        @Composable
        override fun buildLabel(): String {
            return when (showCompleted) {
                true -> stringResource(id = R.string.homework_filterCompletionCompleted)
                false -> stringResource(id = R.string.homework_filterCompletionUncompleted)
                null -> stringResource(id = R.string.homework_filterCompletionName)
            }
        }

        @Composable
        override fun buildLeadingIcon(): @Composable (() -> Unit) = {
            Icon(imageVector = Icons.Default.Check, contentDescription = null)
        }

        override fun filter(homework: Homework): Boolean {
            if (showCompleted == null) return true
            if (showCompleted && homework.tasks.all { it.isDone }) return true
            if (!showCompleted && homework.tasks.any { !it.isDone }) return true
            return false
        }
    }
}

sealed class HomeworkListEvent {
    data class DeleteOrHide(val homework: Homework) : HomeworkListEvent()
    data class MarkAsDone(val homework: Homework) : HomeworkListEvent()
    data class UpdateFilter(val filter: HomeworkFilter) : HomeworkListEvent()
    data object ResetFilters : HomeworkListEvent()
    data object RefreshHomework : HomeworkListEvent()
}

sealed class HomeworkListError {
    data object MarkAsDoneError : HomeworkListError()
    data object DeleteOrHideError : HomeworkListError()
}