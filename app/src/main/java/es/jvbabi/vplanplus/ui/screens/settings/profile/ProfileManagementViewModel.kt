package es.jvbabi.vplanplus.ui.screens.settings.profile

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.jvbabi.vplanplus.domain.model.ProfileType
import es.jvbabi.vplanplus.domain.model.School
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
) : ViewModel() {

    private val _state = mutableStateOf(ProfileManagementState())
    val state: State<ProfileManagementState> = _state

    init {
        viewModelScope.launch {
            profileUseCases.getProfiles().collect { profiles ->
                val schools = mutableMapOf<String, List<ProfileManagementProfile>>()
                profiles.forEach {
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
        }
    }

    suspend fun getSchoolByName(name: String): School {
        return schoolUseCases.getSchoolByName(name)
    }
}

data class ProfileManagementState(
    val schools: List<ProfileManagementSchool> = emptyList(),
    val isLoading: Boolean = false,
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

