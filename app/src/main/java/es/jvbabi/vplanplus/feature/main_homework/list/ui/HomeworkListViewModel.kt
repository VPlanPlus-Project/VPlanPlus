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
                homeworkListUseCases.getHomeworkUseCase()
            )) { data ->
                val profile = data[0] as? ClassProfile
                val homework = data[1] as List<Homework>

                state.copy(
                    userUsesFalseProfileType = profile == null,
                    profile = profile,
                    homework = homework
                )
            }.collect { state = it }
        }
    }
}

data class HomeworkListState(
    val userUsesFalseProfileType: Boolean = false,
    val profile: ClassProfile? = null,
    val homework: List<Homework> = emptyList(),
    val filters: List<HomeworkFilter> = listOf(HomeworkFilter.VisibilityFilter(true), HomeworkFilter.CompletionFilter(true))
)

sealed interface HomeworkFilter {
    @Composable fun buildLabel(): String
    @Composable fun buildLeadingIcon(): (@Composable () -> Unit)?
    @Composable fun buildTrailingIcon(): (@Composable () -> Unit)? = { Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null) }
    fun filter(homework: Homework): Boolean

    /**
     * @param visible: null to show all, true to show only visible, false to show only hidden
     */
    data class VisibilityFilter(val visible: Boolean?) : HomeworkFilter {
        @Composable
        override fun buildLabel(): String {
            return when (visible) {
                true -> stringResource(id = R.string.homework_filterVisibilityVisible)
                false -> stringResource(id = R.string.homework_filterVisibilityHidden)
                null -> stringResource(id = R.string.homework_filterVisibilityName)
            }
        }

        override fun filter(homework: Homework): Boolean {
            if (visible == null) return true
            if (visible && homework is Homework.LocalHomework) return true
            if (visible && homework is Homework.CloudHomework && !homework.isHidden) return true
            if (!visible && homework is Homework.CloudHomework && homework.isHidden) return true
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