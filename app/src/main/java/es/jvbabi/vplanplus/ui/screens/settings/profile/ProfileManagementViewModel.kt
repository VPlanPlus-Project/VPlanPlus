package es.jvbabi.vplanplus.ui.screens.settings.profile

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.jvbabi.vplanplus.data.model.ProfileType
import es.jvbabi.vplanplus.domain.model.School
import es.jvbabi.vplanplus.domain.repository.RoomRepository
import es.jvbabi.vplanplus.domain.repository.TeacherRepository
import es.jvbabi.vplanplus.domain.usecase.ClassUseCases
import es.jvbabi.vplanplus.domain.usecase.ProfileUseCases
import es.jvbabi.vplanplus.domain.usecase.SchoolUseCases
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ProfileManagementViewModel @Inject constructor(
    private val profileUseCases: ProfileUseCases,
    private val classUseCases: ClassUseCases,
    private val schoolUseCases: SchoolUseCases,
    private val teacherRepository: TeacherRepository,
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
                            val school = `class`.school
                            if (schools.containsKey(school.name)) {
                                schools[school.name] = schools[school.name]!!.plus(
                                    ProfileManagementProfile(
                                        id = it.id,
                                        name = if (it.displayName.length > 4) it.originalName else it.displayName,
                                        type = it.type
                                    )
                                )
                            } else {
                                schools[school.name] = listOf(
                                    ProfileManagementProfile(
                                        id = it.id,
                                        name = if (it.displayName.length > 4) it.originalName else it.displayName,
                                        type = it.type
                                    )
                                )
                            }
                        }

                        ProfileType.TEACHER -> {
                            val school = teacherRepository.getTeacherById(it.referenceId)!!.school
                            if (schools.containsKey(school.name)) {
                                schools[school.name] = schools[school.name]!!.plus(
                                    ProfileManagementProfile(
                                        id = it.id,
                                        name = if (it.displayName.length > 4) it.originalName else it.displayName,
                                        type = it.type
                                    )
                                )
                            } else {
                                schools[school.name] = listOf(
                                    ProfileManagementProfile(
                                        id = it.id,
                                        name = if (it.displayName.length > 4) it.originalName else it.displayName,
                                        type = it.type
                                    )
                                )
                            }
                        }

                        ProfileType.ROOM -> {
                            val school = roomRepository.getRoomById(it.referenceId).school
                            if (schools.containsKey(school.name)) {
                                schools[school.name] = schools[school.name]!!.plus(
                                    ProfileManagementProfile(
                                        id = it.id,
                                        name = if (it.displayName.length > 4) it.originalName else it.displayName,
                                        type = it.type
                                    )
                                )
                            } else {
                                schools[school.name] = listOf(
                                    ProfileManagementProfile(
                                        id = it.id,
                                        name = if (it.displayName.length > 4) it.originalName else it.displayName,
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

    fun openDeleteSchoolDialog(school: ProfileManagementSchool) {
        _state.value = _state.value.copy(deletingSchool = school)
    }

    fun closeDeleteSchoolDialog() {
        _state.value = _state.value.copy(deletingSchool = null)
    }

    fun deleteSchool() {
        if (_state.value.deletingSchool == null) return
        viewModelScope.launch {
            schoolUseCases.deleteSchool(schoolUseCases.getSchoolByName(_state.value.deletingSchool!!.name).schoolId)
            closeDeleteSchoolDialog()
        }
    }
}

data class ProfileManagementState(
    val schools: List<ProfileManagementSchool> = emptyList(),
    val isLoading: Boolean = false,

    val deletingSchool: ProfileManagementSchool? = null,
)

data class ProfileManagementSchool(
    val name: String,
    val profiles: List<ProfileManagementProfile>
)

data class ProfileManagementProfile(
    val id: UUID,
    val name: String,
    val type: ProfileType
)

