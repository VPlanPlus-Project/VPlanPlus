package es.jvbabi.vplanplus.feature.settings.profile.ui

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.domain.model.School
import es.jvbabi.vplanplus.domain.usecase.settings.profiles.ProfileSettingsUseCases
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileManagementViewModel @Inject constructor(
    private val profileSettingsUseCase: ProfileSettingsUseCases
) : ViewModel() {

    private val _state = mutableStateOf(ProfileManagementState())
    val state: State<ProfileManagementState> = _state

    private var checkNewCredentialsValidityJob: Job? = null

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

    fun deleteSchool() {
        if (_state.value.deletingSchool == null) return
        viewModelScope.launch {
            profileSettingsUseCase.deleteSchoolUseCase(_state.value.deletingSchool!!.id)
            closeDeleteSchoolDialog()
        }
    }

    fun share(school: School) {
        _state.value = _state.value.copy(
            shareSchool = Gson().toJson(
                ShareSchool(
                    school.sp24SchoolId,
                    school.username,
                    school.password
                )
            )
        )
    }

    fun closeShareDialog() {
        _state.value = _state.value.copy(shareSchool = null)
    }

    fun openUpdateCredentialsDialog(schoolId: Int) {
        val school = state.value.profiles.keys.firstOrNull { it.id == schoolId } ?: return
        _state.value = _state.value.copy(
            changeCredentials = ProfileManagementChangeCredentialsState(
                school = school,
                username = school.username,
                password = school.password,
                isValid = null
            )
        )
    }

    fun resetUpdateCredentialsValidity() {
        _state.value = _state.value.copy(
            changeCredentials = _state.value.changeCredentials?.copy(isValid = null)
        )
    }

    fun checkCredentialsValidity(username: String, password: String) {
        val sp24SchoolId = _state.value.changeCredentials?.school?.sp24SchoolId ?: return
        checkNewCredentialsValidityJob?.cancel()
        _state.value = _state.value.copy(
            changeCredentials = _state.value.changeCredentials?.copy(
                isLoading = true
            )
        )
        checkNewCredentialsValidityJob = viewModelScope.launch {
            val isValid = profileSettingsUseCase.checkCredentialsUseCase(sp24SchoolId, username, password)
            _state.value = _state.value.copy(
                changeCredentials = _state.value.changeCredentials?.copy(
                    isValid = isValid,
                    hasError = isValid == null,
                    isLoading = false
                )
            )
        }
    }

    fun confirmNewCredentials(username: String, password: String) {
        val school = _state.value.changeCredentials?.school ?: return
        viewModelScope.launch {
            profileSettingsUseCase.updateCredentialsUseCase(school, username, password)
            closeUpdateCredentialsDialog()
        }
        _state.value = _state.value.copy(taskCompleted = true)
    }

    fun closeUpdateCredentialsDialog() {
        _state.value = _state.value.copy(changeCredentials = null, taskCompleted = true)
    }
}

data class ProfileManagementState(
    val profiles: Map<School, List<Profile>> = emptyMap(),
    val isLoading: Boolean = false,

    val deletingSchool: School? = null,
    val shareSchool: String? = null,
    val changeCredentials: ProfileManagementChangeCredentialsState? = null,
    val taskCompleted: Boolean = false
)

private data class ShareSchool(
    val schoolId: Int,
    val username: String,
    val password: String
)

data class ProfileManagementChangeCredentialsState(
    val school: School,
    val username: String,
    val password: String,
    val isLoading: Boolean = false,
    val isValid: Boolean? = null,
    val hasError: Boolean = false
)