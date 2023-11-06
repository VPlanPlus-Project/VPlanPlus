package es.jvbabi.vplanplus.ui.screens.settings.profile

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.jvbabi.vplanplus.domain.model.ProfileType
import es.jvbabi.vplanplus.domain.repository.RoomRepository
import es.jvbabi.vplanplus.domain.repository.TeacherRepository
import es.jvbabi.vplanplus.domain.usecase.ClassUseCases
import es.jvbabi.vplanplus.domain.usecase.ProfileUseCases
import es.jvbabi.vplanplus.domain.usecase.SchoolUseCases
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileManagementViewModel @Inject constructor(
    private val profileUseCases: ProfileUseCases,
    private val classUseCases: ClassUseCases,
    private val schoolUseCases: SchoolUseCases,
    private val teacherRepostitory: TeacherRepository,
    private val roomRepository: RoomRepository
): ViewModel() {

    private val _state = mutableStateOf(ProfileManagementState())
    val state: State<ProfileManagementState> = _state

    suspend fun init() {
        _state.value = _state.value.copy(isLoading = true)
        val dbProfiles = profileUseCases.getProfiles()
        val schools = mutableMapOf<String, List<ProfileManagementProfile>>()
        dbProfiles.forEach {
            when (it.type) {
                ProfileType.STUDENT -> {
                    val `class` = classUseCases.getClassById(it.referenceId)
                    val school = schoolUseCases.getSchoolFromId(`class`.schoolId)
                    if (schools.containsKey(school.name)) {
                        schools[school.name] = schools[school.name]!!.plus(
                            ProfileManagementProfile(
                                id = it.id!!,
                                name = it.name,
                                type = it.type
                            )
                        )
                    } else {
                        schools[school.name] = listOf(
                            ProfileManagementProfile(
                                id = it.id!!,
                                name = it.name,
                                type = it.type
                            )
                        )
                    }
                }
                ProfileType.TEACHER -> {
                    val teacher = teacherRepostitory.getTeacherById(it.referenceId)
                    val school = schoolUseCases.getSchoolFromId(teacher!!.schoolId)
                    if (schools.containsKey(school.name)) {
                        schools[school.name] = schools[school.name]!!.plus(
                            ProfileManagementProfile(
                                id = it.id!!,
                                name = it.name,
                                type = it.type
                            )
                        )
                    } else {
                        schools[school.name] = listOf(
                            ProfileManagementProfile(
                                id = it.id!!,
                                name = it.name,
                                type = it.type
                            )
                        )
                    }
                }
                ProfileType.ROOM -> {
                    val room = roomRepository.getRoomById(it.referenceId)
                    val school = schoolUseCases.getSchoolFromId(room.schoolId)
                    if (schools.containsKey(school.name)) {
                        schools[school.name] = schools[school.name]!!.plus(
                            ProfileManagementProfile(
                                id = it.id!!,
                                name = it.name,
                                type = it.type
                            )
                        )
                    } else {
                        schools[school.name] = listOf(
                            ProfileManagementProfile(
                                id = it.id!!,
                                name = it.name,
                                type = it.type
                            )
                        )
                    }
                }
            }
        }

        _state.value = _state.value.copy(isLoading = false, schools = schools.map {
            ProfileManagementSchool(
                name = it.key,
                profiles = it.value
            )
        })
    }

    suspend fun getSchoolByName(schoolName: String) = schoolUseCases.getSchoolByName(schoolName)

    fun onProfileDeleteDialogOpen(profile: ProfileManagementProfile) {
        _state.value = state.value.copy(deleteProfileDialogProfile = profile)
    }

    fun onProfileDeleteDialogClose() {
        _state.value = state.value.copy(deleteProfileDialogProfile = null)
    }

    fun deleteProfile(profile: ProfileManagementProfile) {
        viewModelScope.launch {
            val school = profileUseCases.getSchoolFromProfileId(profile.id)
            if (profileUseCases.getProfilesBySchoolId(school.id!!).size == 1) {
                setDeleteProfileResult(ProfileManagementDeletionResult.LAST_PROFILE)
                return@launch
            }
            val activeProfile = profileUseCases.getActiveProfile()!!
            if (activeProfile.id == profile.id) {
                profileUseCases.setActiveProfile(
                    profileUseCases.getProfiles().find { it.id != profile.id }?.id?:-1
                )
            }
            profileUseCases.deleteProfile(profile.id)
            setDeleteProfileResult(ProfileManagementDeletionResult.SUCCESS)
            init()
        }
    }

    fun setDeleteProfileResult(result: ProfileManagementDeletionResult?) {
        _state.value = state.value.copy(deleteProfileResult = result)
    }
}

data class ProfileManagementState(
    val schools: List<ProfileManagementSchool> = emptyList(),
    val isLoading: Boolean = false,
    val deleteProfileDialogProfile: ProfileManagementProfile? = null,
    val deleteProfileResult: ProfileManagementDeletionResult? = null,
)

data class ProfileManagementSchool(
    val name: String,
    val profiles: List<ProfileManagementProfile>
)

data class ProfileManagementProfile(
    val id: Long,
    val name: String,
    val type: ProfileType
)

enum class ProfileManagementDeletionResult {
    SUCCESS,
    LAST_PROFILE,
}