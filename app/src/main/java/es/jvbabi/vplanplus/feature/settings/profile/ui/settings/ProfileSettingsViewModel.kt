@file:Suppress("UNCHECKED_CAST")

package es.jvbabi.vplanplus.feature.settings.profile.ui.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.jvbabi.vplanplus.domain.model.Calendar
import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.domain.model.ProfileCalendarType
import es.jvbabi.vplanplus.domain.usecase.settings.profiles.ProfileManagementDeletionResult
import es.jvbabi.vplanplus.domain.usecase.settings.profiles.ProfileSettingsUseCases
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject


@HiltViewModel
class ProfileSettingsViewModel @Inject constructor(
    private val profileSettingsUseCases: ProfileSettingsUseCases,
) : ViewModel() {

    var state by mutableStateOf(ProfileSettingsState())
        private set

    fun init(profileId: UUID) {
        viewModelScope.launch {
            combine(
                listOf(
                    profileSettingsUseCases.getProfileByIdUseCase(profileId),
                    profileSettingsUseCases.getCalendarsUseCase(),
                    profileSettingsUseCases.hasProfileLocalAssessmentsUseCase(),
                    profileSettingsUseCases.hasProfileLocalHomeworkUseCase()
                )
            ) { data ->
                val profile = data[0] as? Profile ?: return@combine state.copy(initDone = true)
                val calendars = data[1] as List<Calendar>
                val hasProfileLocalAssessments = data[2] as Boolean
                val hasProfileLocalHomework = data[3] as Boolean

                state.copy(
                    profile = profile,
                    calendars = calendars,
                    initDone = true,
                    profileCalendar = calendars.firstOrNull { it.id == profile.calendarId },
                    profileHasLocalHomework = hasProfileLocalHomework,
                    profileHasLocalAssessments = hasProfileLocalAssessments
                )
            }.collect {
                state = it
            }
        }
    }

    fun onEvent(event: ProfileSettingsEvent) {
        viewModelScope.launch {
            when (event) {
                is ProfileSettingsEvent.DeleteProfile -> { deleteProfile(); event.after() }
                is ProfileSettingsEvent.RenameProfile -> renameProfile(event.name)
                is ProfileSettingsEvent.SetCalendarState -> setCalendarMode(event.to)
                is ProfileSettingsEvent.SetCalendar -> setCalendar(event.calendarId)
                is ProfileSettingsEvent.SetHomeworkEnabled -> updateHomeworkEnabled(event.enabled)
                is ProfileSettingsEvent.SetAssessmentsEnabled -> profileSettingsUseCases.updateAssessmentsEnabledUseCase(state.profile as? ClassProfile ?: return@launch, event.enabled)
                is ProfileSettingsEvent.ToggleNotificationForProfile -> profileSettingsUseCases.toggleNotificationForProfileUseCase((state.profile as? ClassProfile) ?: return@launch, event.enabled)
            }
        }
    }

    private suspend fun setCalendarMode(calendarMode: ProfileCalendarType) {
        val profile = state.profile ?: return
        profileSettingsUseCases.updateCalendarTypeUseCase(
            profile,
            calendarType = calendarMode
        )
    }

    private suspend fun setCalendar(calendarId: Long) {
        val profile = state.profile ?: return
        profileSettingsUseCases.updateCalendarIdUseCase(profile, calendarId)
    }

    private suspend fun deleteProfile() {
        val profile = state.profile ?: return
        state = state.copy(deleteProfileResult = profileSettingsUseCases.deleteProfileUseCase(profile))
    }

    private suspend fun renameProfile(name: String) {
        val profile = state.profile ?: return
        profileSettingsUseCases.updateProfileDisplayNameUseCase(profile, name)
    }

    private suspend fun updateHomeworkEnabled(enabled: Boolean) {
        val profile = state.profile as? ClassProfile ?: return
        profileSettingsUseCases.updateHomeworkEnabledUseCase(profile, enabled)
    }
}

data class ProfileSettingsState(
    val initDone: Boolean = false,
    val profile: Profile? = null,
    val profileCalendar: Calendar? = null,
    val deleteProfileResult: ProfileManagementDeletionResult? = null,
    val calendars: List<Calendar> = listOf(),

    val dialogOpen: Boolean = false,
    val dialogCall: @Composable () -> Unit = {},

    val profileHasLocalHomework: Boolean = false,
    val profileHasLocalAssessments: Boolean = false,
)

sealed class ProfileSettingsEvent {
    data class DeleteProfile(val after: () -> Unit) : ProfileSettingsEvent()
    data class RenameProfile(val name: String) : ProfileSettingsEvent()
    data class SetCalendarState(val to: ProfileCalendarType) : ProfileSettingsEvent()
    data class SetCalendar(val calendarId: Long) : ProfileSettingsEvent()
    data class SetHomeworkEnabled(val enabled: Boolean) : ProfileSettingsEvent()
    data class SetAssessmentsEnabled(val enabled: Boolean): ProfileSettingsEvent()

    data class ToggleNotificationForProfile(val enabled: Boolean) : ProfileSettingsEvent()
}