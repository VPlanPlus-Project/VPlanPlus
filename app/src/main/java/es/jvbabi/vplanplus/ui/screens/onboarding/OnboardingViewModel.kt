package es.jvbabi.vplanplus.ui.screens.onboarding

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.jvbabi.vplanplus.data.model.ProfileType
import es.jvbabi.vplanplus.domain.usecase.Response
import es.jvbabi.vplanplus.domain.usecase.SchoolIdCheckResult
import es.jvbabi.vplanplus.domain.usecase.onboarding.DefaultLesson
import es.jvbabi.vplanplus.domain.usecase.onboarding.OnboardingUseCases
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val onboardingUseCases: OnboardingUseCases
) : ViewModel() {
    private val _state = mutableStateOf(OnboardingState())
    val state: State<OnboardingState> = _state

    // UI TEXT INPUT EVENT HANDLERS
    fun onSchoolIdInput(schoolId: String) {
        _state.value = _state.value.copy(
            schoolId = schoolId,
            schoolIdState = if (onboardingUseCases.checkSchoolIdSyntax(schoolId)) SchoolIdCheckResult.SYNTACTICALLY_CORRECT else SchoolIdCheckResult.INVALID
        )
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

    fun reset() {
        _state.value = OnboardingState()
    }

    fun newScreen() {
        _state.value = _state.value.copy(
            isLoading = false,
            currentResponseType = Response.NONE,
            showTeacherDialog = false
        )
    }

    /**
     * Called when user clicks next button on [OnboardingSchoolIdScreen]
     */
    fun onSchoolIdSubmit() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            val result = onboardingUseCases.testSchoolExistence(state.value.schoolId.toLong())

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
        }
    }

    /**
     * Called when user clicks next button on [OnboardingLoginScreen]
     */
    suspend fun onLogin() {
        isLoading(true)

        val baseDataResponse = onboardingUseCases.loginUseCase(
            schoolId = state.value.schoolId,
            username = state.value.username,
            password = state.value.password
        )

        _state.value = _state.value.copy(
            isLoading = false,
            currentResponseType = baseDataResponse,
            loginSuccessful = baseDataResponse == Response.SUCCESS
        )
    }

    /**
     * Called when user clicks profile card on [OnboardingAddProfileScreen]
     */
    fun onFirstProfileSelect(profileType: ProfileType?) {
        _state.value = _state.value.copy(profileType = profileType)
    }

    /**
     * Called when user clicks next button on [OnboardingAddProfileScreen]
     */
    fun onProfileTypeSubmit() {
        viewModelScope.launch {
            with(state.value) {
                isLoading(true)
                _state.value = _state.value.copy(
                    profileOptions = onboardingUseCases.profileOptionsUseCase(
                        schoolId = schoolId.toLong(),
                        profileType = profileType!!
                    ),
                    isLoading = false
                )
            }
        }
    }

    fun onProfileSelect(p: String) {
        _state.value = _state.value.copy(selectedProfileOption = p)
    }

    /**
     * Called when user clicks next button on [OnboardingProfileOptionListScreen]
     */
    fun onProfileSubmit() {
        _state.value = _state.value.copy(isLoading = true)

        when (_state.value.profileType) {
            ProfileType.STUDENT -> {
                loadDefaultLessons()
            }

            else -> onInsertData()
        }
    }

    fun loadDefaultLessons() {
        isLoading(true)
        _state.value = _state.value.copy(defaultLessonsLoading = true)
        viewModelScope.launch {
            _state.value = _state.value.copy(
                defaultLessons = onboardingUseCases.defaultLessonUseCase(
                    schoolId = state.value.schoolId.toLong(),
                    username = state.value.username,
                    password = state.value.password,
                    className = state.value.selectedProfileOption!!
                )?.associateWith { true }?: mapOf(),
                defaultLessonsLoading = false
            )
        }
    }

    fun onInsertData() {
        viewModelScope.launch {
            onboardingUseCases.saveProfileUseCase(
                schoolId = state.value.schoolId.toLong(),
                username = state.value.username,
                password = state.value.password,
                type = state.value.profileType!!,
                referenceName = state.value.selectedProfileOption!!,
                defaultLessonsEnabled = state.value.defaultLessons.map {
                    it.key.vpId to it.value
                }.toMap()
            )
            _state.value = _state.value.copy(isLoading = false)
        }
    }

    fun onAutomaticSchoolIdInput(schoolId: Long) {
        viewModelScope.launch {
            val school = onboardingUseCases.getSchoolByIdUseCase(schoolId)!!
            _state.value = _state.value.copy(
                schoolId = schoolId.toString(),
                schoolIdState = SchoolIdCheckResult.VALID,
                username = school.username,
                password = school.password,
                loginSuccessful = true
            )
        }
    }

    fun setTask(task: Task) {
        _state.value = _state.value.copy(task = task)
    }

    fun setOnboardingCause(cause: OnboardingCause) {
        _state.value = _state.value.copy(onboardingCause = cause)
    }

    fun setTeacherDialogVisibility(v: Boolean) {
        _state.value = _state.value.copy(showTeacherDialog = v)
    }

    fun setDefaultLesson(lesson: DefaultLesson, activated: Boolean) {
        _state.value = _state.value.copy(
            defaultLessons = state.value.defaultLessons.toMutableMap()
                .apply { this[lesson] = activated })
    }

    fun isLoading(loading: Boolean) {
        _state.value = _state.value.copy(isLoading = loading)
    }
}

data class OnboardingState(
    val onboardingCause: OnboardingCause = OnboardingCause.FIRST_START,
    val schoolId: String = "",
    val schoolIdState: SchoolIdCheckResult? = SchoolIdCheckResult.INVALID,

    val username: String = "",
    val password: String = "",
    val passwordVisible: Boolean = false,
    val loginSuccessful: Boolean = false,

    val currentResponseType: Response = Response.NONE,
    val isLoading: Boolean = false,

    val profileType: ProfileType? = null,
    val task: Task = Task.CREATE_SCHOOL,

    val profileOptions: List<String> = listOf(),
    val selectedProfileOption: String? = null,

    val showTeacherDialog: Boolean = false,

    val defaultLessons: Map<DefaultLesson, Boolean> = mapOf(),
    val defaultLessonsLoading: Boolean = false
)

enum class Task {
    CREATE_SCHOOL, CREATE_PROFILE
}

enum class OnboardingCause {
    FIRST_START, NEW_PROFILE
}