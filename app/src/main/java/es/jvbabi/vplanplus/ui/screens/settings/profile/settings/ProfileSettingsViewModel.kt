package es.jvbabi.vplanplus.ui.screens.settings.profile.settings

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.data.model.ProfileCalendarType
import es.jvbabi.vplanplus.data.model.ProfileType
import es.jvbabi.vplanplus.domain.model.Calendar
import es.jvbabi.vplanplus.domain.model.DefaultLesson
import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.domain.repository.CalendarRepository
import es.jvbabi.vplanplus.domain.usecase.ProfileUseCases
import es.jvbabi.vplanplus.ui.common.MultipleSelectDialog
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject


@HiltViewModel
class ProfileSettingsViewModel @Inject constructor(
    private val profileUseCases: ProfileUseCases,
    private val calendarRepository: CalendarRepository,
) : ViewModel() {

    private val _state = mutableStateOf(ProfileSettingsState())
    val state: State<ProfileSettingsState> = _state

    @SuppressLint("Range")
    fun init(profileId: Long) {

        viewModelScope.launch {
            var firstRun = true
            combine(
                profileUseCases.getProfileById(profileId),
                calendarRepository.getCalendars(),
            ) { profile, calendars ->
                _state.value.copy(
                    profile = profile,
                    calendars = calendars,
                    initDone = true,
                    profileCalendar = calendars.firstOrNull { it.id == profile.calendarId })
            }.collect {
                _state.value = it
                if (firstRun) {
                    if (_state.value.profile!!.type == ProfileType.STUDENT) {
                        _state.value =
                            _state.value.copy(defaultLessons = it.profile!!.defaultLessons.toMap())
                    }
                    firstRun = false
                }
            }
        }
    }

    private fun setDeleteProfileResult(result: ProfileManagementDeletionResult) {
        viewModelScope.launch {
            _state.value = _state.value.copy(deleteProfileResult = result)
        }
    }

    fun setCalendarMode(calendarMode: ProfileCalendarType) {
        viewModelScope.launch {
            _state.value.profile?.let { profile ->
                profileUseCases.setCalendarType(profile.id, calendarType = calendarMode)
            }
        }
    }

    fun setCalendar(calendarId: Long) {
        viewModelScope.launch {
            _state.value.profile?.let { profile ->
                profileUseCases.setCalendarId(profile.id, calendarId)
            }
        }
    }

    fun setDefaultLesson(id: UUID, value: Boolean) {
        _state.value = _state.value.copy(
            defaultLessons = _state.value.defaultLessons.toMutableMap().apply {
                this[this.keys.first { it.defaultLessonId == id }] = value
            }
        )
        Log.d(
            "ProfileSettingsViewModel",
            _state.value.defaultLessons[_state.value.defaultLessons.keys.first { it.defaultLessonId == id }].toString()
        )
    }

    fun setDialogOpen(open: Boolean) {
        _state.value = _state.value.copy(dialogOpen = open)
    }

    fun setDialogCall(call: @Composable () -> Unit) {
        _state.value = _state.value.copy(dialogCall = call)
    }

    fun deleteProfile(context: Context) {
        viewModelScope.launch {
            _state.value.profile?.let { profile ->
                if (profileUseCases.getProfilesBySchoolId(
                        profileUseCases.getSchoolFromProfileId(
                            profile.id
                        ).schoolId
                    ).size == 1
                ) {
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
                notificationManager.deleteNotificationChannel("PROFILE_${profile.originalName}")
            }
        }
    }

    private fun onDefaultLessonsSet() {
        viewModelScope.launch {
            _state.value.profile!!.defaultLessons.forEach {
                if (_state.value.defaultLessons[it.key] != _state.value.profile!!.defaultLessons[it.key]) {
                    if (_state.value.defaultLessons[it.key]!!) {
                        profileUseCases.enableDefaultLesson(_state.value.profile!!.id, it.key.vpId)
                        Log.d("ProfileSettingsViewModel", "Enabled ${it.key.vpId}")
                    } else {
                        profileUseCases.disableDefaultLesson(_state.value.profile!!.id, it.key.vpId)
                        Log.d("ProfileSettingsViewModel", "Disabled ${it.key.vpId}")
                    }
                }
            }
        }
    }

    @Composable
    fun DefaultLessonsDialog(
        onSetDefaultLesson: (UUID, Boolean) -> Unit
    ) {
        MultipleSelectDialog(
            icon = Icons.Default.FilterAlt,
            title = stringResource(id = R.string.settings_profileManagementDefaultLessonSettingsTitle),
            items = _state.value.defaultLessons.toSortedMap(),
            onItemChange = { item, value ->
                onSetDefaultLesson(item.defaultLessonId, value)
            },
            onOk = {
                setDialogOpen(false)
                onDefaultLessonsSet()
            },
            onDismiss = {
                setDialogOpen(false)
                _state.value =
                    _state.value.copy(defaultLessons = _state.value.profile!!.defaultLessons.toMap())

            },
            toText = {
                it.subject + " - " + (it.teacher?.acronym ?: "No Teacher")
            } // TODO to stringResource
        )
    }

    fun renameProfile(name: String) {
        viewModelScope.launch {
            _state.value.profile?.let { profile ->
                profileUseCases.setDisplayName(profile.id, name)
            }
        }
    }
}

data class ProfileSettingsState(
    val initDone: Boolean = false,
    val profile: Profile? = null,
    val profileCalendar: Calendar? = null,
    val deleteProfileResult: ProfileManagementDeletionResult? = null,
    val calendars: List<Calendar> = listOf(),
    val defaultLessons: Map<DefaultLesson, Boolean> = mapOf(),

    val dialogOpen: Boolean = false,
    val dialogCall: @Composable () -> Unit = {}
)


enum class ProfileManagementDeletionResult {
    SUCCESS,
    LAST_PROFILE,
}