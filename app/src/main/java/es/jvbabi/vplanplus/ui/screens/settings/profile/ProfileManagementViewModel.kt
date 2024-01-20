package es.jvbabi.vplanplus.ui.screens.settings.profile

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.domain.model.School
import es.jvbabi.vplanplus.domain.usecase.settings.profiles.ProfileSettingsUseCases
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileManagementViewModel @Inject constructor(
    private val profileSettingsUseCase: ProfileSettingsUseCases
) : ViewModel() {

    private val _state = mutableStateOf(ProfileManagementState())
    val state: State<ProfileManagementState> = _state

    init {
        viewModelScope.launch {
            profileSettingsUseCase.getProfilesUseCase().collect {
                _state.value = _state.value.copy(
                    profiles = it,
                    isLoading = false
                )
            }
        }
    }

    fun openDeleteSchoolDialog(school: School) {
        _state.value = _state.value.copy(deletingSchool = school)
    }

    fun closeDeleteSchoolDialog() {
        _state.value = _state.value.copy(deletingSchool = null)
    }

    fun deleteSchool(context: Context) {
        if (_state.value.deletingSchool == null) return
        viewModelScope.launch {
            profileSettingsUseCase.deleteSchoolUseCase(
                context,
                _state.value.deletingSchool!!.schoolId
            )
            closeDeleteSchoolDialog()
        }
    }

    fun share(school: School) {
        _state.value = _state.value.copy(shareSchool = Gson().toJson(ShareSchool(
            school.schoolId,
            school.username,
            school.password
        )))
    }

    fun closeShareDialog() {
        _state.value = _state.value.copy(shareSchool = null)
    }
}

data class ProfileManagementState(
    val profiles: Map<School, List<Profile>> = emptyMap(),
    val isLoading: Boolean = false,

    val deletingSchool: School? = null,
    val shareSchool: String? = null,
)

private data class ShareSchool (
    val schoolId: Long,
    val username: String,
    val password: String
)