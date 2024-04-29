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
import es.jvbabi.vplanplus.feature.main_grades.domain.model.Interval
import es.jvbabi.vplanplus.feature.main_grades.domain.model.Subject
import es.jvbabi.vplanplus.feature.main_grades.domain.usecase.GradeUseCases
import es.jvbabi.vplanplus.feature.main_grades.domain.usecase.GradeUseState
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@Suppress("UNCHECKED_CAST")
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
                val grades = data[1] as List<Grade>
                val showBanner = data[2] as Boolean
                val isBiometricEnabled = data[3] as Boolean
                val canShowEnableBiometricBanner = data[4] as Boolean
                val isBiometricSetUp = gradeUseCases.isBiometricSetUpUseCase()

                return@combine _state.value.copy(enabled = enabled,
                    grades = grades.groupBy { it.subject }.keys.associateWith { subject ->
                        val gradesForSubject = grades.filter { it.subject == subject }
                        SubjectGradeCollection(
                            subject = subject,
                            grades = gradesForSubject,
                        )
                    },
                    visibleSubjects = grades.groupBy { it.subject }.keys.toList(),
                    showBanner = showBanner,
                    showEnableBiometricBanner = canShowEnableBiometricBanner,
                    isBiometricEnabled = isBiometricEnabled,
                    isBiometricSetUp = isBiometricSetUp,
                    authenticationState =
                        if (state.value.authenticationState == AuthenticationState.AUTHENTICATED) AuthenticationState.AUTHENTICATED
                        else if (isBiometricSetUp && isBiometricEnabled) AuthenticationState.NONE
                        else AuthenticationState.AUTHENTICATED,
                    isSek2 = grades.any { it.interval.type == "Sek II" },
                    intervals = grades.groupBy { it.interval }.keys.associateWith { interval ->
                        state.value.intervals.getOrDefault(interval, true)
                    }
                )
            }.collect {
                _state.value = it
            }
        }
    }

    fun onToggleInterval(interval: Interval) {
        val intervals = _state.value.intervals.toMutableMap()
        intervals[interval] = !intervals.getOrDefault(interval, true)
        _state.value = _state.value.copy(intervals = intervals)
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
    val grades: Map<Subject, SubjectGradeCollection> = emptyMap(),
    val showBanner: Boolean = false,
    val isBiometricEnabled: Boolean = false,
    val showEnableBiometricBanner: Boolean = false,
    val isBiometricSetUp: Boolean = false,
    val authenticationState: AuthenticationState = AuthenticationState.NONE,
    val isSek2: Boolean = false,
    val intervals: Map<Interval, Boolean> = emptyMap()
) {
    val avg: Double
        get() {
            val avg = grades.mapNotNull { (subject, grades) ->
                if (visibleSubjects.contains(subject)) {
                    grades.grades
                        .filter { intervals.getOrDefault(it.interval, false) && it.actualValue != null }
                        .groupBy { grade -> grade.type }
                        .map { (_, grades) -> grades.map { it.value }.average() }
                        .average()
                }
                else null
            }.filter { !it.isNaN() }
            return avg.average()
        }

    val latestGrades
        get() = grades
            .flatMap { it.value.grades }
            .sortedByDescending { it.givenAt }
            .filter { visibleSubjects.contains(it.subject) && intervals.getOrDefault(it.interval, false) }
}

data class SubjectGradeCollection(
    val subject: Subject,
    val grades: List<Grade>,
)

enum class AuthenticationState {
    NONE, AUTHENTICATING, AUTHENTICATED
}