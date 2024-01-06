package es.jvbabi.vplanplus.ui.screens.onboarding

import android.content.Context
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
import es.jvbabi.vplanplus.domain.usecase.onboarding.toLoginState
import es.jvbabi.vplanplus.domain.usecase.onboarding.toResponse
import es.jvbabi.vplanplus.ui.common.Permission
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val onboardingUseCases: OnboardingUseCases
) : ViewModel() {
    private val _state = mutableStateOf(OnboardingState())
    val state: State<OnboardingState> = _state

    fun nextStageSchoolId() {
        _state.value = _state.value.copy(stage = Stage.SCHOOL_ID)
    }

    fun nextStageCredentials() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            val result = onboardingUseCases.testSchoolExistence(state.value.schoolId.toLong())

            _state.value = _state.value.copy(
                isLoading = false,
                username = "schueler",
                password = "",
                schoolIdState = result,
                currentResponseType = when (result) {
                    SchoolIdCheckResult.VALID -> Response.SUCCESS
                    SchoolIdCheckResult.NOT_FOUND -> Response.NOT_FOUND
                    null -> Response.NO_INTERNET
                    else -> Response.OTHER
                },
                stage = if (result == SchoolIdCheckResult.VALID) Stage.CREDENTIALS else Stage.SCHOOL_ID
            )
        }
    }

    fun nextStageProfileType() {
        viewModelScope.launch {
            isLoading(true)

            val baseDataResponse = onboardingUseCases.loginUseCase(
                schoolId = state.value.schoolId,
                username = state.value.username,
                password = state.value.password
            )

            _state.value = _state.value.copy(
                isLoading = false,
                currentResponseType = baseDataResponse.toResponse(),
                loginState = baseDataResponse.toLoginState(),
                testSchoolError = baseDataResponse.toResponse() != Response.SUCCESS && state.value.schoolId == "10000000",
                stage = if (baseDataResponse.toResponse() == Response.SUCCESS) Stage.PROFILE_TYPE else Stage.CREDENTIALS
            )
        }
    }

    fun nextStageProfile() {
        viewModelScope.launch {
            with(state.value) {
                isLoading(true)
                _state.value = _state.value.copy(
                    profileOptions = onboardingUseCases.profileOptionsUseCase(
                        schoolId = schoolId.toLong(),
                        profileType = profileType!!
                    ),
                    isLoading = false,
                    stage = Stage.PROFILE
                )
            }
        }
    }

    fun nextStageDefaultLessonOrPermissions(context: Context) {
        _state.value = _state.value.copy(isLoading = true)

        when (_state.value.profileType) {
            ProfileType.STUDENT -> {
                _state.value = _state.value.copy(stage = Stage.DEFAULT_LESSONS)
                loadDefaultLessons(false)
            }
            else -> nextStagePermissions(context)
        }
    }

    fun nextStagePermissions(context: Context) {
        onInsertData()
        if (Permission.permissions.any { !Permission.isGranted(context, it.type) }) {
            _state.value = _state.value.copy(stage = Stage.PERMISSIONS)
            if (Permission.isGranted(context, Permission.permissions[0].type)) {
                nextPermission(context)
            }
        } else {
            _state.value = _state.value.copy(stage = Stage.FINISH)
        }
    }

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
            showTeacherDialog = false,
            showCloseDialog = false
        )
    }

    /**
     * Called when user clicks profile card on [OnboardingAddProfileScreen]
     */
    fun onFirstProfileSelect(profileType: ProfileType?) {
        _state.value = _state.value.copy(profileType = profileType)
    }

    fun onProfileSelect(p: String) {
        _state.value = _state.value.copy(selectedProfileOption = p)
    }

    fun toggleUserName() {
        if (state.value.username == "schueler") _state.value = _state.value.copy(username = "lehrer")
        else _state.value = _state.value.copy(username = "schueler")
    }

    fun loadDefaultLessons(force: Boolean) {
        if (state.value.defaultLessonsClass == state.value.selectedProfileOption && !force) return
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
                defaultLessonsLoading = false,
                defaultLessonsClass = state.value.selectedProfileOption!!,
            )
            isLoading(false)
        }
    }

    private fun onInsertData() {
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
                loginState = if (school.fullyCompatible) LoginState.FULL else LoginState.PARTIAL,
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

    fun onTestSchoolErrorDialogDismissed() {
        _state.value = _state.value.copy(testSchoolError = false)
    }

    fun useTestSchool() {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                schoolId = "10000000",
                username = "schueler",
                password = "test",
                schoolIdState = SchoolIdCheckResult.VALID,
                testSchoolLoading = true
            )
            nextStageProfileType()
        }
    }

    fun goBackToSchoolId() {
        _state.value = _state.value.copy(
            isLoading = false,
            currentResponseType = Response.NONE,
            username = "",
            password = "",
            loginState = LoginState.NONE,
            schoolIdState = SchoolIdCheckResult.SYNTACTICALLY_CORRECT,
            stage = Stage.SCHOOL_ID
        )
    }

    fun showCloseDialog() {
        _state.value = _state.value.copy(showCloseDialog = true)
    }

    fun hideCloseDialog() {
        _state.value = _state.value.copy(showCloseDialog = false)
    }

    fun goBackToProfileType() {
        _state.value = _state.value.copy(
            selectedProfileOption = null,
            stage = Stage.PROFILE_TYPE
        )
    }

    fun goBackToProfile() {
        _state.value = _state.value.copy(
            stage = Stage.PROFILE,
            isLoading = false,
            defaultLessons = state.value.defaultLessons.mapValues { true }
        )
    }

    fun nextPermission(context: Context) {
        val currentPermissionIndex = state.value.currentPermissionIndex
        if (currentPermissionIndex+1 < Permission.permissions.size) {
            _state.value = _state.value.copy(currentPermissionIndex = currentPermissionIndex+1)
            if (Permission.isGranted(context, Permission.permissions[currentPermissionIndex+1].type)) {
                nextPermission(context)
            }
        } else {
            _state.value = _state.value.copy(stage = Stage.FINISH)
        }
    }
}

data class OnboardingState(
    val onboardingCause: OnboardingCause = OnboardingCause.FIRST_START,
    val schoolId: String = "",
    val schoolIdState: SchoolIdCheckResult? = SchoolIdCheckResult.INVALID,

    val username: String = "schueler",
    val password: String = "",
    val passwordVisible: Boolean = false,
    val loginState: LoginState = LoginState.NONE,

    val currentResponseType: Response = Response.NONE,
    val isLoading: Boolean = false,

    val profileType: ProfileType? = null,
    val task: Task = Task.CREATE_SCHOOL,

    val profileOptions: List<String> = listOf(),
    val selectedProfileOption: String? = null,

    val showTeacherDialog: Boolean = false,

    val defaultLessons: Map<DefaultLesson, Boolean> = mapOf(),
    val defaultLessonsClass: String = "",
    val defaultLessonsLoading: Boolean = false,

    val testSchoolError: Boolean = false,
    val testSchoolLoading: Boolean = false,
    val showCloseDialog: Boolean = false,

    val stage: Stage = Stage.WELCOME,

    val currentPermissionIndex: Int = 0
)

enum class Task {
    CREATE_SCHOOL, CREATE_PROFILE
}

enum class OnboardingCause {
    FIRST_START, NEW_PROFILE
}

enum class LoginState {
    NONE, FULL, PARTIAL
}

enum class Stage {
    WELCOME,
    SCHOOL_ID,
    CREDENTIALS,
    PROFILE_TYPE,
    PROFILE,
    DEFAULT_LESSONS,
    PERMISSIONS,
    FINISH
}