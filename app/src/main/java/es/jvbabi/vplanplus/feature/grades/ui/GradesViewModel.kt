package es.jvbabi.vplanplus.feature.grades.ui

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.jvbabi.vplanplus.feature.grades.domain.model.Grade
import es.jvbabi.vplanplus.feature.grades.domain.model.Subject
import es.jvbabi.vplanplus.feature.grades.domain.usecase.GradeUseCases
import es.jvbabi.vplanplus.feature.grades.domain.usecase.GradeUseState
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GradesViewModel @Inject constructor(
    private val gradeUseCases: GradeUseCases
) : ViewModel() {

    private val _state = mutableStateOf(GradesState())
    val state: State<GradesState> = _state

    init {
        viewModelScope.launch {
            combine(
                gradeUseCases.isEnabledUseCase(),
                gradeUseCases.getGradesUseCase(),
                gradeUseCases.showBannerUseCase()
            ) { enabled, grades, showBanner ->
                _state.value.copy(enabled = enabled,
                    grades = grades.grades.groupBy { it.subject }.keys.associateWith { subject ->
                        val gradesForSubject = grades.grades.filter { it.subject == subject }
                        val avg = gradesForSubject.groupBy { it.type }
                            .map { it.value.sumOf { grade -> grade.value.toDouble() } / it.value.size }
                            .sum() / gradesForSubject.groupBy { it.type }.size
                        SubjectGradeCollection(
                            subject = subject,
                            grades = gradesForSubject,
                            avg = avg
                        )
                    },
                    latestGrades = grades.grades.sortedByDescending { it.givenAt }.take(5),
                    avg = grades.avg,
                    showBanner = showBanner
                )
            }.collect {
                _state.value = it
            }
        }
    }

    fun onHideBanner() {
        viewModelScope.launch {
            gradeUseCases.hideBannerUseCase()
        }
    }
}

data class GradesState(
    val enabled: GradeUseState? = null,
    val latestGrades: List<Grade> = emptyList(),
    val grades: Map<Subject, SubjectGradeCollection> = emptyMap(),
    val avg: Double = 0.0,
    val showBanner: Boolean = false
)

data class SubjectGradeCollection(
    val subject: Subject,
    val grades: List<Grade>,
    val avg: Double
)