package es.jvbabi.vplanplus.feature.exams.ui.details

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.model.Exam
import es.jvbabi.vplanplus.feature.exams.domain.usecase.details.ExamDetailsUseCases
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExamDetailsViewModel @Inject constructor(
    private val examDetailsUseCases: ExamDetailsUseCases
) : ViewModel() {
    var state by mutableStateOf(ExamDetailsState())
        private set

    fun init(examId: Int) {
        viewModelScope.launch {
            combine(
                listOf(
                    examDetailsUseCases.getExamUseCase(examId),
                    examDetailsUseCases.getCurrentProfileUseCase()
                )
            ) { data ->
                val exam = data[0] as? Exam
                val currentProfile = data[1] as? ClassProfile

                state.copy(
                    exam = exam,
                    currentProfile = currentProfile
                )
            }.collect {
                state = it
            }
        }
    }
}

data class ExamDetailsState(
    val exam: Exam? = null,
    val currentProfile: ClassProfile? = null
) {
    val isUserAllowedToEdit: Boolean
        get() = exam != null && (exam.id < 0 || exam.createdBy?.id == currentProfile?.vppId?.id)
}