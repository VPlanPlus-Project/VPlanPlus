package es.jvbabi.vplanplus.feature.onboarding.ui

import android.content.Context
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.annotations.SerializedName
import dagger.hilt.android.lifecycle.HiltViewModel
import es.jvbabi.vplanplus.data.model.ProfileType
import es.jvbabi.vplanplus.domain.repository.SchoolIdCheckResult
import es.jvbabi.vplanplus.domain.repository.TimeRepository
import es.jvbabi.vplanplus.domain.usecase.sync.SyncUseCases
import es.jvbabi.vplanplus.feature.onboarding.domain.usecase.DefaultLesson
import es.jvbabi.vplanplus.feature.onboarding.domain.usecase.OnboardingUseCases
import es.jvbabi.vplanplus.feature.onboarding.domain.usecase.ProfileCreationStage
import es.jvbabi.vplanplus.feature.onboarding.domain.usecase.ProfileCreationStatus
import es.jvbabi.vplanplus.feature.onboarding.domain.usecase.toLoginState
import es.jvbabi.vplanplus.feature.onboarding.domain.usecase.toResponse
import es.jvbabi.vplanplus.ui.common.Permission
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val onboardingUseCases: OnboardingUseCases,
    private val syncUseCases: SyncUseCases,
    private val timeRepository: TimeRepository
) : ViewModel() {
    private val _state = mutableStateOf(OnboardingState())
    val state: State<OnboardingState> = _state

    fun nextStageSchoolId() {
        _state.value = _state.value.copy(stage = Stage.SCHOOL_ID)
    }

    init {
        viewModelScope.launch {
            timeRepository.getTime().collect {
                _state.value = _state.value.copy(time = it)
            }
        }
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
                    SchoolIdCheckResult.VALID -> HttpStatusCode.OK
                    SchoolIdCheckResult.NOT_FOUND -> HttpStatusCode.NotFound
                    else -> null
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
                stage = if (baseDataResponse.toResponse() == HttpStatusCode.OK) Stage.PROFILE_TYPE else Stage.CREDENTIALS
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
            _state.value = _state.value.copy(stage = Stage.FINISH, isLoading = true)
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
            currentResponseType = HttpStatusCode.OK,
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
        if (state.value.username == "schueler") _state.value =
            _state.value.copy(username = "lehrer")
        else _state.value = _state.value.copy(username = "schueler")
    }

    fun loadDefaultLessons(force: Boolean) {
        if (state.value.defaultLessonsClass == state.value.selectedProfileOption && !force) return
        isLoading(true)
        _state.value = _state.value.copy(defaultLessonsLoading = true, hasDefaultLessons = null)
        viewModelScope.launch {
            _state.value = _state.value.copy(
                defaultLessons = onboardingUseCases.defaultLessonUseCase(
                    schoolId = state.value.schoolId.toLong(),
                    username = state.value.username,
                    password = state.value.password,
                    className = state.value.selectedProfileOption!!
                )?.associateWith { true } ?: mapOf(),
                defaultLessonsLoading = false,
                defaultLessonsClass = state.value.selectedProfileOption!!,
            )
            _state.value = _state.value.copy(
                hasDefaultLessons = _state.value.defaultLessons.isNotEmpty()
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
                }.toMap(),
                onStatusUpdate = {
                    _state.value = _state.value.copy(creationStatus = it)
                }
            )
            if (_state.value.task == Task.CREATE_SCHOOL) {
                _state.value = _state.value.copy(creationStatus = state.value.creationStatus.copy(progress = null, profileCreationStage = ProfileCreationStage.INITIAL_SYNC))
                syncUseCases.triggerSyncUseCase(true)
                delay(1000) // allow worker to start
                syncUseCases.isSyncRunningUseCase().collect {
                    Log.d("OnboardingViewModel", "Sync running: $it")
                    if (!it) {
                        isLoading(false)
                        _state.value = _state.value.copy(allDone = true)
                    }
                }
            } else isLoading(false)
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

    fun useQrResult() {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                schoolId = _state.value.qrResult!!.schoolId,
                username = _state.value.qrResult!!.username,
                password = _state.value.qrResult!!.password,
                schoolIdState = SchoolIdCheckResult.VALID,
            )
            nextStageProfileType()
        }
    }

    fun saveQrResult(result: QrResult) {
        _state.value = _state.value.copy(qrResult = result)
    }

    fun goBackToSchoolId() {
        _state.value = _state.value.copy(
            isLoading = false,
            currentResponseType = null,
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
        if (currentPermissionIndex + 1 < Permission.permissions.size) {
            _state.value = _state.value.copy(currentPermissionIndex = currentPermissionIndex + 1)
            if (Permission.isGranted(
                    context,
                    Permission.permissions[currentPermissionIndex + 1].type
                )
            ) {
                nextPermission(context)
            }
        } else {
            _state.value = _state.value.copy(stage = Stage.FINISH, isLoading = true)
        }
    }

    fun showQr() {
        _state.value = _state.value.copy(showQr = true)
    }

    fun closeQr() {
        _state.value = _state.value.copy(showQr = false)
    }
}

data class OnboardingState(
    val onboardingCause: OnboardingCause = OnboardingCause.FIRST_START,
    val schoolId: String = "",
    val schoolIdState: SchoolIdCheckResult? = SchoolIdCheckResult.INVALID,

    val showQr: Boolean = false,
    val qrResult: QrResult? = null,

    val username: String = "schueler",
    val password: String = "",
    val passwordVisible: Boolean = false,
    val loginState: LoginState = LoginState.NONE,

    val currentResponseType: HttpStatusCode? = HttpStatusCode.OK,
    val isLoading: Boolean = false,

    val profileType: ProfileType? = null,
    val task: Task = Task.CREATE_SCHOOL,

    val profileOptions: List<String> = listOf(),
    val selectedProfileOption: String? = null,

    val showTeacherDialog: Boolean = false,

    val defaultLessons: Map<DefaultLesson, Boolean> = mapOf(),
    val hasDefaultLessons: Boolean? = null,
    val defaultLessonsClass: String = "",
    val defaultLessonsLoading: Boolean = false,

    val showCloseDialog: Boolean = false,

    val stage: Stage = Stage.WELCOME,
    val creationStatus: ProfileCreationStatus = ProfileCreationStatus(ProfileCreationStage.NONE, null),

    val time: ZonedDateTime = ZonedDateTime.now(),

    val currentPermissionIndex: Int = 0,

    val allDone: Boolean = false
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

data class QrResult(
    @SerializedName("schoolId") val schoolId: String,
    @SerializedName("username") val username: String,
    @SerializedName("password") val password: String
)