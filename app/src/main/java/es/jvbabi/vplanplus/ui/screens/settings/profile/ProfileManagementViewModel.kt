package es.jvbabi.vplanplus.ui.screens.settings.profile

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import es.jvbabi.vplanplus.domain.usecase.ClassUseCases
import es.jvbabi.vplanplus.domain.usecase.ProfileUseCases
import es.jvbabi.vplanplus.domain.usecase.SchoolUseCases
import javax.inject.Inject

@HiltViewModel
class ProfileManagementViewModel @Inject constructor(
    private val profileUseCases: ProfileUseCases,
    private val classUseCases: ClassUseCases,
    private val schoolUseCases: SchoolUseCases
): ViewModel() {

    private val _state = mutableStateOf(ProfileManagementState())
    val state: State<ProfileManagementState> = _state

    suspend fun init() {
        _state.value = _state.value.copy(isLoading = true)
        val dbProfiles = profileUseCases.getProfiles()
        val schools = mutableMapOf<String, List<ProfileManagementProfile>>()
        dbProfiles.forEach {
            when (it.type) {
                0 -> {
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
}

data class ProfileManagementState(
    val schools: List<ProfileManagementSchool> = emptyList(),
    val isLoading: Boolean = false
)

data class ProfileManagementSchool(
    val name: String,
    val profiles: List<ProfileManagementProfile>
)

data class ProfileManagementProfile(
    val id: Long,
    val name: String,
    val type: Int
)