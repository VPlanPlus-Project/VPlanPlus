package es.jvbabi.vplanplus.ui.screens.settings.profile.settings

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.Context
import android.provider.CalendarContract
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.domain.model.ProfileCalendarType
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

    @SuppressLint("Range")
    fun init(profileId: Long, context: Context) {

        try {
            val contentResolver = context.contentResolver
            val calendarsUri = CalendarContract.Calendars.CONTENT_URI

            val projection = arrayOf(
                CalendarContract.Calendars._ID,
                CalendarContract.Calendars.ACCOUNT_NAME,
                CalendarContract.Calendars.CALENDAR_DISPLAY_NAME
            )
            val cursor = contentResolver.query(calendarsUri, projection, null, null, null)
            Log.d("Calendar", "Calendars:")
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    // Retrieve calendar information
                    val calendarId =
                        cursor.getLong(cursor.getColumnIndex(CalendarContract.Calendars._ID))
                    val accountName =
                        cursor.getString(cursor.getColumnIndex(CalendarContract.Calendars.ACCOUNT_NAME))
                    val calendarName =
                        cursor.getString(cursor.getColumnIndex(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME))

                    Log.d("Calendar", "Calendar id: $calendarId, Calendar account name: $accountName, Calendar name: $calendarName")
                } while (cursor.moveToNext())
                cursor.close()
            }
        } catch (e: SecurityException) {
            Log.d("Calendar", "No permission to read calendar: ${e.message}")
        }


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

    fun setCalendarMode(calendarMode: ProfileCalendarType) {
        viewModelScope.launch {
            _state.value.profile?.let { profile ->
                profileUseCases.updateProfile(profile.copy(calendarMode = calendarMode))
            }
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
    val deleteProfileResult: ProfileManagementDeletionResult? = null,
)

enum class ProfileManagementDeletionResult {
    SUCCESS,
    LAST_PROFILE,
}