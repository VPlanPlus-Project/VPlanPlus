package es.jvbabi.vplanplus.ui.screens.settings.profile.settings

import android.app.NotificationManager
import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.domain.usecase.ProfileUseCases
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileSettingsViewModel @Inject constructor(
    private val profileUseCases: ProfileUseCases
): ViewModel() {

    private val _state = mutableStateOf(ProfileSettingsState())
    val state: State<ProfileSettingsState> = _state

    fun init(profileId: Long) {
        viewModelScope.launch {
            profileUseCases.getProfileById(profileId).collect {
                _state.value = _state.value.copy(profile = it, initDone = true)
            }
        }
    }

    private fun setDeleteProfileResult(result: ProfileManagementDeletionResult) {
        viewModelScope.launch {
            _state.value = _state.value.copy(deleteProfileResult = result)
        }
    }

    fun deleteProfile(context: Context) {
        viewModelScope.launch {
            _state.value.profile?.let { profile ->
                if (profileUseCases.getProfilesBySchoolId(profileUseCases.getSchoolFromProfileId(profile.id!!).id!!).size == 1) {
                    setDeleteProfileResult(ProfileManagementDeletionResult.LAST_PROFILE)
                    return@launch
                }
                val activeProfile = profileUseCases.getActiveProfile()!!
                if (activeProfile.id == profile.id) {
                    profileUseCases.setActiveProfile(
                        profileUseCases.getProfiles().first().find { it.id != profile.id }?.id ?: -1
                    )
                }
                profileUseCases.deleteProfile(profile.id)
                setDeleteProfileResult(ProfileManagementDeletionResult.SUCCESS)
                val notificationManager: NotificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.deleteNotificationChannel("PROFILE_${profile.name}")
            }
        }
    }

    fun renameProfile(name: String) {
        viewModelScope.launch {
            _state.value.profile?.let { profile ->
                profileUseCases.updateProfile(profile.copy(customName = name))
            }
        }
    }
}

data class ProfileSettingsState(
    val initDone: Boolean = false,
    val profile: Profile? = null,
    val deleteProfileResult: ProfileManagementDeletionResult? = null
)

enum class ProfileManagementDeletionResult {
    SUCCESS,
    LAST_PROFILE,
}