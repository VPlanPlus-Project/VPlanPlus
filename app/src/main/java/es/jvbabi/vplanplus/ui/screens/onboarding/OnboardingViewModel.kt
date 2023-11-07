package es.jvbabi.vplanplus.ui.screens.onboarding

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.jvbabi.vplanplus.domain.model.ProfileType
import es.jvbabi.vplanplus.domain.model.XmlBaseData
import es.jvbabi.vplanplus.domain.repository.RoomRepository
import es.jvbabi.vplanplus.domain.repository.TeacherRepository
import es.jvbabi.vplanplus.domain.usecase.BaseDataUseCases
import es.jvbabi.vplanplus.domain.usecase.ClassUseCases
import es.jvbabi.vplanplus.domain.usecase.KeyValueUseCases
import es.jvbabi.vplanplus.domain.usecase.Keys
import es.jvbabi.vplanplus.domain.usecase.ProfileUseCases
import es.jvbabi.vplanplus.domain.usecase.Response
import es.jvbabi.vplanplus.domain.usecase.SchoolIdCheckResult
import es.jvbabi.vplanplus.domain.usecase.SchoolUseCases
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val schoolUseCases: SchoolUseCases,
    private val profileUseCases: ProfileUseCases,
    private val classUseCases: ClassUseCases,
    private val keyValueUseCases: KeyValueUseCases,
    private val baseDataUseCases: BaseDataUseCases,
    private val teacherRepository: TeacherRepository,
    private val roomRepository: RoomRepository
) : ViewModel() {
    private val _state = mutableStateOf(OnboardingState())
    val state: State<OnboardingState> = _state

    private lateinit var baseData: XmlBaseData

    // UI TEXT INPUT EVENT HANDLERS
    fun onSchoolIdInput(schoolId: String) {
        _state.value = _state.value.copy(
            schoolId = schoolId,
            schoolIdState = schoolUseCases.checkSchoolId(schoolId)
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
        _state.value = _state.value.copy(isLoading = false, currentResponseType = Response.NONE)
    }

    /**
     * Called when user clicks next button on [OnboardingSchoolIdScreen]
     */
    suspend fun onSchoolIdSubmit() {
        _state.value = _state.value.copy(isLoading = true)
        schoolUseCases.checkSchoolIdOnline(state.value.schoolId.toLong()).onEach { result ->
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

    /**
     * Called when user clicks next button on [OnboardingLoginScreen]
     */
    suspend fun onLogin() {
        _state.value = _state.value.copy(isLoading = true)

        val baseData = baseDataUseCases.getBaseData(
            schoolId = state.value.schoolId.toLong(),
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

    /**
     * Called when user clicks profile card on [OnboardingAddProfileScreen]
     */
    fun onFirstProfileSelect(profileType: ProfileType?) {
        _state.value = _state.value.copy(profileType = profileType)
    }

    /**
     * Called when user clicks next button on [OnboardingAddProfileScreen]
     */
    suspend fun onProfileTypeSubmit() {
        _state.value = _state.value.copy(isLoading = true)

        if (state.value.profileType == ProfileType.STUDENT) {
            if (state.value.task == Task.CREATE_SCHOOL) {
                _state.value = _state.value.copy(
                    profileOptions = baseData.classNames,
                )
            } else {
                _state.value = _state.value.copy(
                    profileOptions = classUseCases.getClassesBySchool(schoolUseCases.getSchoolFromId(state.value.schoolId.toLong())).map { it.className },
                )
            }
        } else if (state.value.profileType == ProfileType.TEACHER) {
            if (state.value.task == Task.CREATE_SCHOOL) {
                _state.value = _state.value.copy(
                    profileOptions = baseData.teacherShorts,
                )
            } else {
                _state.value = _state.value.copy(
                    profileOptions = teacherRepository.getTeachersBySchoolId(state.value.schoolId.toLong()).map { it.acronym },
                )
            }
        } else if (state.value.profileType == ProfileType.ROOM) {
            if (state.value.task == Task.CREATE_SCHOOL) {
                _state.value = _state.value.copy(
                    profileOptions = baseData.roomNames,
                )
            } else {
                _state.value = _state.value.copy(
                    profileOptions = roomRepository.getRoomsBySchool(schoolUseCases.getSchoolFromId(state.value.schoolId.toLong())).map { it.name },
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
    suspend fun onProfileSubmit() {
        _state.value = _state.value.copy(isLoading = true)
        viewModelScope.launch {

            if (state.value.task == Task.CREATE_SCHOOL) {

                schoolUseCases.createSchool(
                    schoolId = state.value.schoolId.toLong(),
                    username = state.value.username,
                    password = state.value.password,
                    name = baseData.schoolName
                )

                baseDataUseCases.processBaseData(
                    schoolId = state.value.schoolId.toLong(),
                    baseData = baseData
                )
            }

            when (state.value.profileType!!) {
                ProfileType.STUDENT -> {
                    val `class` = classUseCases.getClassBySchoolIdAndClassName(
                        schoolId = state.value.schoolId.toLong(),
                        className = state.value.selectedProfileOption!!,
                    )!!
                    profileUseCases.createStudentProfile(
                        classId = `class`.id!!,
                        name = state.value.selectedProfileOption!!
                    )
                    keyValueUseCases.set(
                        Keys.ACTIVE_PROFILE.name,
                        profileUseCases.getProfileByClassId(`class`.id).id.toString()
                    )
                }
                ProfileType.TEACHER -> {
                    val teacher = teacherRepository.find(
                        school = schoolUseCases.getSchoolFromId(state.value.schoolId.toLong()),
                        acronym = state.value.selectedProfileOption!!,
                        createIfNotExists = false
                    )!!
                    profileUseCases.createTeacherProfile(teacherId = teacher.id!!, name = teacher.acronym)
                    keyValueUseCases.set(
                        Keys.ACTIVE_PROFILE.name,
                        profileUseCases.getProfileByTeacherId(teacher.id).id.toString()
                    )
                }
                ProfileType.ROOM -> {
                    val room = roomRepository.getRoomsBySchool(schoolUseCases.getSchoolFromId(state.value.schoolId.toLong())).find { it.name == state.value.selectedProfileOption!! }!!
                    profileUseCases.createRoomProfile(roomId = room.id!!, name = room.name)
                    keyValueUseCases.set(
                        Keys.ACTIVE_PROFILE.name,
                        profileUseCases.getProfileByRoomId(room.id).id.toString()
                    )
                }
            }

            _state.value = _state.value.copy(isLoading = false)
        }

    }

    fun onAutomaticSchoolIdInput(schoolId: Long) {
        val school = schoolUseCases.getSchoolFromId(schoolId)
        _state.value = _state.value.copy(
            schoolId = schoolId.toString(),
            schoolIdState = SchoolIdCheckResult.VALID,
            username = school.username,
            password = school.password,
            loginSuccessful = true
        )
    }

    fun setTask(task: Task) {
        _state.value = _state.value.copy(task = task)
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

    val profileType: ProfileType? = null,
    val task: Task = Task.CREATE_SCHOOL,

    val profileOptions: List<String> = listOf(),
    val selectedProfileOption: String? = null,
)

enum class Task {
    CREATE_SCHOOL, CREATE_PROFILE
}