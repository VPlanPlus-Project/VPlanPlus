package es.jvbabi.vplanplus.feature.main_grades.ui.view

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.jvbabi.vplanplus.domain.repository.BiometricRepository
import es.jvbabi.vplanplus.feature.main_grades.domain.model.Grade
import es.jvbabi.vplanplus.feature.main_grades.domain.model.Subject
import es.jvbabi.vplanplus.feature.main_grades.domain.usecase.GradeState
import es.jvbabi.vplanplus.feature.main_grades.domain.usecase.GradeUseCases
import es.jvbabi.vplanplus.feature.main_grades.domain.usecase.GradeUseState
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GradesViewModel @Inject constructor(
    private val gradeUseCases: GradeUseCases,
) : ViewModel() {

    private val _state = mutableStateOf(GradesState())
    val state: State<GradesState> = _state

    init {
        viewModelScope.launch {
            combine(
                listOf(
                    gradeUseCases.isEnabledUseCase(),
                    gradeUseCases.getGradesUseCase(),
                    gradeUseCases.showBannerUseCase(),
                    gradeUseCases.isBiometricEnabled(),
                    gradeUseCases.canShowEnableBiometricBannerUseCase(),
                )
            ) { data ->
                val enabled = data[0] as GradeUseState
                val grades = data[1] as GradeState
                val showBanner = data[2] as Boolean
                val isBiometricEnabled = data[3] as Boolean
                val canShowEnableBiometricBanner = data[4] as Boolean
                val isBiometricSetUp = gradeUseCases.isBiometricSetUpUseCase()

                return@combine _state.value.copy(enabled = enabled,
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
                    latestGrades = grades.grades.sortedByDescending { it.givenAt },
                    avg = grades.avg,
                    visibleSubjects = grades.grades.groupBy { it.subject }.keys.toList(),
                    showBanner = showBanner,
                    showEnableBiometricBanner = canShowEnableBiometricBanner,
                    isBiometricEnabled = isBiometricEnabled,
                    isBiometricSetUp = isBiometricSetUp,
                    authenticationState =
                        if (state.value.authenticationState == AuthenticationState.AUTHENTICATED) AuthenticationState.AUTHENTICATED
                        else if (isBiometricSetUp && isBiometricEnabled) AuthenticationState.NONE
                        else AuthenticationState.AUTHENTICATED
                )
            }.collect {
                _state.value = it
            }
        }
    }

    fun authenticate(fragmentActivity: FragmentActivity) {
        _state.value = _state.value.copy(authenticationState = AuthenticationState.AUTHENTICATING)
        gradeUseCases.requestBiometricUseCase(
            fragmentActivity = fragmentActivity,
            onSuccess = {
                _state.value = _state.value.copy(authenticationState = AuthenticationState.AUTHENTICATED)
            },
            onFail = {
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
                        _state.value = _state.value.copy(authenticationState = AuthenticationState.NONE)
                }
            },
        )
    }

    fun onHideBanner() {
        viewModelScope.launch {
            gradeUseCases.hideBannerUseCase()
        }
    }

    fun onSetBiometric(state: Boolean) {
        viewModelScope.launch {
            gradeUseCases.setBiometricUseCase(state)
        }
    }

    fun onDismissEnableBiometricBanner() {
        viewModelScope.launch {
            gradeUseCases.hideEnableBiometricBannerUseCase()
        }
    }

    fun onToggleSubject(subject: Subject) {
        val visibleSubjects = _state.value.visibleSubjects.toMutableList()
        if (visibleSubjects.size == state.value.grades.size) {
            visibleSubjects.clear()
            visibleSubjects.add(subject)
        } else if (visibleSubjects.contains(subject)) {
            visibleSubjects.remove(subject)
        } else {
            visibleSubjects.add(subject)
        }
        if (visibleSubjects.isEmpty()) {
            visibleSubjects.addAll(state.value.grades.keys)
        }
        _state.value = _state.value.copy(visibleSubjects = visibleSubjects)
    }
}

data class GradesState(
    val enabled: GradeUseState? = null,
    val visibleSubjects: List<Subject> = emptyList(),
    val latestGrades: List<Grade> = emptyList(),
    val grades: Map<Subject, SubjectGradeCollection> = emptyMap(),
    val avg: Double = 0.0,
    val showBanner: Boolean = false,
    val isBiometricEnabled: Boolean = false,
    val showEnableBiometricBanner: Boolean = false,
    val isBiometricSetUp: Boolean = false,
    val authenticationState: AuthenticationState = AuthenticationState.NONE
)

data class SubjectGradeCollection(
    val subject: Subject,
    val grades: List<Grade>,
    val avg: Double
)

enum class AuthenticationState {
    NONE, AUTHENTICATING, AUTHENTICATED
}