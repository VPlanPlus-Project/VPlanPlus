package es.jvbabi.vplanplus.feature.settings.profile.ui.settings

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.jvbabi.vplanplus.data.model.ProfileCalendarType
import es.jvbabi.vplanplus.domain.model.Calendar
import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.domain.model.VppId
import es.jvbabi.vplanplus.domain.usecase.general.GetClassByProfileUseCase
import es.jvbabi.vplanplus.domain.usecase.settings.profiles.ProfileManagementDeletionResult
import es.jvbabi.vplanplus.domain.usecase.settings.profiles.ProfileSettingsUseCases
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject


@HiltViewModel
class ProfileSettingsViewModel @Inject constructor(
    private val profileSettingsUseCases: ProfileSettingsUseCases,
    private val getClassByProfileUseCase: GetClassByProfileUseCase
) : ViewModel() {

    private val _state = mutableStateOf(ProfileSettingsState())
    val state: State<ProfileSettingsState> = _state

    @SuppressLint("Range")
    fun init(profileId: UUID, context: Context) {
        _state.value = _state.value.copy(
            calendarPermissionState = if (ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.WRITE_CALENDAR
                ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.READ_CALENDAR
                ) == PackageManager.PERMISSION_GRANTED
            ) CalendarPermissionState.GRANTED else CalendarPermissionState.DENIED
        )
        viewModelScope.launch {
            combine(
                profileSettingsUseCases.getProfileByIdUseCase(profileId),
                profileSettingsUseCases.getCalendarsUseCase()
            ) { profile, calendars ->
                if (profile == null) return@combine _state.value.copy(initDone = true)
                val `class` = getClassByProfileUseCase(profile)
                val vppId =
                    if (`class` != null) profileSettingsUseCases.getVppIdByClassUseCase(`class`)
                    else null
                _state.value.copy(
                    profile = profile,
                    calendars = calendars,
                    initDone = true,
                    profileCalendar = calendars.firstOrNull { it.id == profile.calendarId },
                    linkedVppId = vppId
                )
            }.collect {
                _state.value = it
            }
        }
    }

    fun setCalendarMode(calendarMode: ProfileCalendarType) {
        if (_state.value.calendarPermissionState == CalendarPermissionState.DENIED) {
            _state.value = _state.value.copy(calendarPermissionState = CalendarPermissionState.SHOW_DIALOG)
            viewModelScope.launch {
                _state.value.profile?.let { profile ->
                    profileSettingsUseCases.updateCalendarTypeUseCase(profile, calendarType = ProfileCalendarType.NONE)
                }
            }
            return
        }
        viewModelScope.launch {
            _state.value.profile?.let { profile ->
                profileSettingsUseCases.updateCalendarTypeUseCase(profile, calendarType = calendarMode)
            }
        }
    }

    fun dismissPermissionDialog() {
        _state.value = _state.value.copy(calendarPermissionState = CalendarPermissionState.DENIED)
    }

    fun setCalendar(calendarId: Long) {
        viewModelScope.launch {
            _state.value.profile?.let { profile ->
                profileSettingsUseCases.updateCalendarIdUseCase(profile, calendarId)
            }
        }
    }

    fun setDialogOpen(open: Boolean) {
        _state.value = _state.value.copy(dialogOpen = open)
    }

    fun setDialogCall(call: @Composable () -> Unit) {
        _state.value = _state.value.copy(dialogCall = call)
    }

    fun deleteProfile() {
        viewModelScope.launch {
            _state.value = _state.value.copy(deleteProfileResult = profileSettingsUseCases.deleteProfileUseCase(_state.value.profile?:return@launch))
        }
    }

    fun renameProfile(name: String) {
        viewModelScope.launch {
            profileSettingsUseCases.updateProfileDisplayNameUseCase(_state.value.profile?:return@launch, name)
        }
    }

    fun updatePermissionState(granted: Boolean) {
        _state.value = _state.value.copy(
            calendarPermissionState = if (granted) CalendarPermissionState.GRANTED else CalendarPermissionState.DENIED
        )
    }
}

data class ProfileSettingsState(
    val initDone: Boolean = false,
    val profile: Profile? = null,
    val profileCalendar: Calendar? = null,
    val deleteProfileResult: ProfileManagementDeletionResult? = null,
    val calendars: List<Calendar> = listOf(),
    val linkedVppId: VppId? = null,

    val dialogOpen: Boolean = false,
    val dialogCall: @Composable () -> Unit = {},

    val calendarPermissionState: CalendarPermissionState = CalendarPermissionState.GRANTED,
)

enum class CalendarPermissionState {
    GRANTED,
    DENIED,
    SHOW_DIALOG
}