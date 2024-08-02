package es.jvbabi.vplanplus.feature.settings.profile.ui.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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

    var state by mutableStateOf(ProfileSettingsDefaultLessonsState())
        private set

    fun init(profileId: UUID) {
        viewModelScope.launch {
            defaultLessonsUseCases.getProfileByIdUseCase(profileId).collect { profile ->
                if (profile !is ClassProfile) return@collect
                val courseGroups = profile.defaultLessons.keys
                    .map { it.courseGroup }
                    .distinct()
                    .filterNotNull()
                    .sortedBy { it }

                state = state.copy(
                    profile = profile,
                    differentDefaultLessons = defaultLessonsUseCases.isInconsistentStateUseCase(profile),
                    courseGroups = courseGroups.ifEmpty { null },
                    isDebug = BuildConfig.DEBUG
                )
            }
        }
    }

    fun onEvent(event: ProfileSettingsDefaultLessonsEvent) {
        viewModelScope.launch {
            when (event) {
                is ProfileSettingsDefaultLessonsEvent.DefaultLessonChanged -> defaultLessonsUseCases.changeDefaultLessonUseCase(
                    profile = state.profile!!,
                    defaultLesson = event.defaultLesson,
                    enabled = event.value
                )
                is ProfileSettingsDefaultLessonsEvent.FixDefaultLessons -> defaultLessonsUseCases.fixDefaultLessonsUseCase(state.profile!!)
            }
        }
    }
}

data class ProfileSettingsDefaultLessonsState(
    val profile: ClassProfile? = null,
    val courseGroups: List<String>? = null,
    val differentDefaultLessons: Boolean = false,
    val isDebug: Boolean = false
)

sealed class ProfileSettingsDefaultLessonsEvent {
    data object FixDefaultLessons : ProfileSettingsDefaultLessonsEvent()
    data class DefaultLessonChanged(val defaultLesson: DefaultLesson, val value: Boolean) : ProfileSettingsDefaultLessonsEvent()
}