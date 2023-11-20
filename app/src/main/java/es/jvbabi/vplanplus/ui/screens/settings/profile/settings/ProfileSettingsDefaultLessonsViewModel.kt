package es.jvbabi.vplanplus.ui.screens.settings.profile.settings

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.jvbabi.vplanplus.domain.model.DefaultLesson
import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.domain.usecase.ProfileUseCases
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileSettingsDefaultLessonsViewModel @Inject constructor(
    private val profileUseCases: ProfileUseCases
) : ViewModel() {

    private val _state = mutableStateOf(ProfileSettingsDefaultLessonsState())
    val state: State<ProfileSettingsDefaultLessonsState> = _state

    fun init(profileId: Long) {
        viewModelScope.launch {
            profileUseCases.getProfileById(profileId).collect {
                _state.value = ProfileSettingsDefaultLessonsState(profile = it)
            }
        }
    }

    fun onDefaultLessonChanged(defaultLesson: DefaultLesson, value: Boolean) {
        viewModelScope.launch {
            if (value) profileUseCases.enableDefaultLesson(profileId = state.value.profile!!.id, vpId = defaultLesson.vpId)
            else profileUseCases.disableDefaultLesson(profileId = state.value.profile!!.id, vpId = defaultLesson.vpId)
        }
    }
}

data class ProfileSettingsDefaultLessonsState(
    val profile: Profile? = null,
)