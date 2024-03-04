package es.jvbabi.vplanplus.feature.grades.ui.view

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.jvbabi.vplanplus.domain.repository.BiometricRepository
import es.jvbabi.vplanplus.domain.repository.BiometricStatus
import es.jvbabi.vplanplus.feature.grades.domain.model.Grade
import es.jvbabi.vplanplus.feature.grades.domain.model.Subject
import es.jvbabi.vplanplus.feature.grades.domain.usecase.GradeUseCases
import es.jvbabi.vplanplus.feature.grades.domain.usecase.GradeUseState
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GradesViewModel @Inject constructor(
    private val gradeUseCases: GradeUseCases,
    private val biometricRepository: BiometricRepository
) : ViewModel() {

    private val _state = mutableStateOf(GradesState())
    val state: State<GradesState> = _state

    init {
        viewModelScope.launch {
            combine(
                gradeUseCases.isEnabledUseCase(),
                gradeUseCases.getGradesUseCase(),
                gradeUseCases.showBannerUseCase(),
                gradeUseCases.isBiometricEnabled()
            ) { enabled, grades, showBanner, isBiometricEnabled ->
                val authenticationStatus = biometricRepository.canAuthenticate()
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
                    showBanner = showBanner,
                    biometricStatus = authenticationStatus,
                    granted = authenticationStatus != BiometricStatus.AVAILABLE,
                    isBiometricEnabled = isBiometricEnabled
                )
            }.collect {
                _state.value = it
            }
        }
    }

    fun authenticate(fragmentActivity: FragmentActivity) {
        viewModelScope.launch {
            biometricRepository.promptUser(
                title = "Authenticate",
                subtitle = "this is subtitle",
                cancelString = "Cancel",
                onSuccess = {
                    _state.value =
                        _state.value.copy(granted = true, showAuthenticationScreen = false)
                },
                onFailed = {
                    Log.i("GradesViewModel", "onFailed")
                },
                onError = { errorCode, errorString ->
                    Log.e("GradesViewModel", "onError: $errorCode, $errorString")
                    if (listOf(
                            BiometricRepository.RESULT_CODE_CANCELED,
                            BiometricRepository.RESULT_CODE_CANCELED_BY_USER,
                            BiometricRepository.RESULT_CODE_TOO_MANY_ATTEMPTS
                        ).contains(errorCode)
                    ) {
                        _state.value = _state.value.copy(showAuthenticationScreen = true)
                    }
                },
                activity = fragmentActivity
            )
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
    val showBanner: Boolean = false,
    val biometricStatus: BiometricStatus = BiometricStatus.NOT_SUPPORTED,
    val granted: Boolean = false,
    val showAuthenticationScreen: Boolean = false,
    val isBiometricEnabled: Boolean = false
)

data class SubjectGradeCollection(
    val subject: Subject,
    val grades: List<Grade>,
    val avg: Double
)