package es.jvbabi.vplanplus.feature.settings.profile.ui.settings

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.jvbabi.vplanplus.BuildConfig
import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.model.DefaultLesson
import es.jvbabi.vplanplus.domain.usecase.settings.profiles.lessons.ProfileDefaultLessonsUseCases
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ProfileSettingsDefaultLessonsViewModel @Inject constructor(
    private val defaultLessonsUseCases: ProfileDefaultLessonsUseCases,
) : ViewModel() {

    private val _state = mutableStateOf(ProfileSettingsDefaultLessonsState())
    val state: State<ProfileSettingsDefaultLessonsState> = _state

    fun init(profileId: UUID) {
        viewModelScope.launch {
            defaultLessonsUseCases.getProfileByIdUseCase(profileId).collect { profile ->
                if (profile as? ClassProfile == null) return@collect
                _state.value = _state.value.copy(
                    profile = profile,
                    differentDefaultLessons = defaultLessonsUseCases.isInconsistentStateUseCase(profile),
                    isDebug = BuildConfig.DEBUG
                )
            }
        }
    }

    fun onDefaultLessonChanged(defaultLesson: DefaultLesson, value: Boolean) {
        viewModelScope.launch {
            defaultLessonsUseCases.changeDefaultLessonUseCase(
                profile = state.value.profile!!,
                defaultLesson = defaultLesson,
                enabled = value
            )
        }
    }

    fun onFixDefaultLessons() {
        viewModelScope.launch {
            defaultLessonsUseCases.fixDefaultLessonsUseCase(state.value.profile!!)
        }
    }
}

data class ProfileSettingsDefaultLessonsState(
    val profile: ClassProfile? = null,
    val differentDefaultLessons: Boolean = false,
    val isDebug: Boolean = false
)