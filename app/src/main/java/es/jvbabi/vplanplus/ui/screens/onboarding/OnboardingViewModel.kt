package es.jvbabi.vplanplus.ui.screens.onboarding

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.jvbabi.vplanplus.domain.model.Holiday
import es.jvbabi.vplanplus.domain.model.Week
import es.jvbabi.vplanplus.domain.model.xml.BaseDataParserStudents
import es.jvbabi.vplanplus.domain.usecase.BaseDataUseCases
import es.jvbabi.vplanplus.domain.usecase.ClassUseCases
import es.jvbabi.vplanplus.domain.usecase.HolidayUseCases
import es.jvbabi.vplanplus.domain.usecase.KeyValueUseCases
import es.jvbabi.vplanplus.domain.usecase.Keys
import es.jvbabi.vplanplus.domain.usecase.OnboardingUseCases
import es.jvbabi.vplanplus.domain.usecase.ProfileUseCases
import es.jvbabi.vplanplus.domain.usecase.Response
import es.jvbabi.vplanplus.domain.usecase.SchoolIdCheckResult
import es.jvbabi.vplanplus.domain.usecase.SchoolUseCases
import es.jvbabi.vplanplus.util.DateUtils
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val schoolUseCases: SchoolUseCases,
    private val profileUseCases: ProfileUseCases,
    private val onboardingUseCases: OnboardingUseCases,
    private val classUseCases: ClassUseCases,
    private val keyValueUseCases: KeyValueUseCases,
    private val holidayUseCases: HolidayUseCases,
    private val baseDataUseCases: BaseDataUseCases,
) : ViewModel() {
    private val _state = mutableStateOf(OnboardingState())
    val state: State<OnboardingState> = _state

    lateinit var baseData: BaseDataParserStudents

    fun onSchoolIdInput(schoolId: String) {
        _state.value = _state.value.copy(
            schoolId = schoolId,
            schoolIdState = schoolUseCases.checkSchoolId(schoolId)
        )
    }

    fun newScreen() {
        _state.value = _state.value.copy(isLoading = false, currentResponseType = Response.NONE)
    }

    suspend fun onSchoolIdSubmit() {
        _state.value = _state.value.copy(isLoading = true)
        schoolUseCases.checkSchoolIdOnline(state.value.schoolId).onEach { result ->
            Log.d("OnboardingViewModel", "onSchoolIdSubmit: $result")
            _state.value = _state.value.copy(
                isLoading = false,
                schoolIdState = result,
                currentResponseType = when (result) {
                    SchoolIdCheckResult.VALID -> Response.SUCCESS
                    SchoolIdCheckResult.NOT_FOUND -> Response.NOT_FOUND
                    null -> Response.NO_INTERNET
                    else -> Response.OTHER
                }
            )
        }.launchIn(viewModelScope)
    }

    fun onUsernameInput(username: String) {
        _state.value = _state.value.copy(username = username)
    }

    fun onPasswordInput(password: String) {
        _state.value = _state.value.copy(password = password)
    }

    fun onPasswordVisibilityToggle() {
        _state.value = _state.value.copy(passwordVisible = !state.value.passwordVisible)
    }

    suspend fun onLogin() {
        _state.value = _state.value.copy(isLoading = true)

        val baseData = baseDataUseCases.getBaseDataXml(
            schoolId = state.value.schoolId,
            username = state.value.username,
            password = state.value.password
        )

        _state.value = _state.value.copy(
            isLoading = false,
            currentResponseType = baseData.response
        )

        if (baseData.data != null) this.baseData = baseData.data

        if (state.value.currentResponseType == Response.SUCCESS) {
            _state.value = _state.value.copy(loginSuccessful = true)
        }
    }

    fun onFirstProfileSelect(firstProfile: FirstProfile) {
        _state.value = _state.value.copy(firstProfile = firstProfile)
    }

    fun onFirstProfileSubmit() {
        _state.value = _state.value.copy(isLoading = true)

        if (state.value.firstProfile == FirstProfile.STUDENT) {
            _state.value = _state.value.copy(
                classList = baseData.classes,
            )
        }
    }

    fun onClassSelect(className: String) {
        _state.value = _state.value.copy(selectedClass = className)
    }

    @OptIn(DelicateCoroutinesApi::class)
    suspend fun onClassSubmit() {
        _state.value = _state.value.copy(isLoading = true)
        GlobalScope.launch {

            schoolUseCases.createSchool(
                schoolId = state.value.schoolId,
                username = state.value.username,
                password = state.value.password,
                name = baseData.schoolName
            )

            state.value.classList.forEach {
                classUseCases.createClass(
                    schoolId = state.value.schoolId,
                    className = it
                )
            }
            val classId = classUseCases.getClassIdBySchoolIdAndClassName(
                schoolId = state.value.schoolId,
                className = state.value.selectedClass!!,
            )
            profileUseCases.createStudentProfile(
                classId = classId,
                name = state.value.selectedClass!!
            )

            holidayUseCases.insertHolidays(baseData.holidays.map {
                Holiday(
                    schoolId = if (it.second) null else state.value.schoolId,
                    timestamp = DateUtils.getDayTimestamp(
                        year = it.first.first,
                        month = it.first.second,
                        day = it.first.third
                    )
                )
            })

            baseDataUseCases.insertWeeks(
                baseData.schoolWeeks.map {
                    Week(
                        schoolId = state.value.schoolId,
                        week = it.week,
                        start = it.start,
                        end = it.end,
                        type = it.type
                    )
                }
            )

            keyValueUseCases.set(
                Keys.ACTIVE_PROFILE.name,
                profileUseCases.getProfileByClassId(classId).id.toString()
            )
            _state.value = _state.value.copy(isLoading = false)
        }

    }
}

data class OnboardingState(
    val schoolId: String = "",
    val schoolIdState: SchoolIdCheckResult? = SchoolIdCheckResult.INVALID,

    val username: String = "",
    val password: String = "",
    val passwordVisible: Boolean = false,
    val loginSuccessful: Boolean = false,

    val currentResponseType: Response = Response.NONE,
    val isLoading: Boolean = false,

    val firstProfile: FirstProfile? = null,

    val classList: List<String> = listOf(),
    val selectedClass: String? = null,
)

enum class FirstProfile {
    TEACHER, STUDENT
}