package es.jvbabi.vplanplus.ui.screens.settings.profile.settings

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.jvbabi.vplanplus.data.model.ProfileType
import es.jvbabi.vplanplus.domain.model.DefaultLesson
import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.domain.repository.DefaultLessonRepository
import es.jvbabi.vplanplus.domain.usecase.ProfileUseCases
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileSettingsDefaultLessonsViewModel @Inject constructor(
    private val profileUseCases: ProfileUseCases,
    private val defaultLessonRepository: DefaultLessonRepository
) : ViewModel() {

    private val _state = mutableStateOf(ProfileSettingsDefaultLessonsState())
    val state: State<ProfileSettingsDefaultLessonsState> = _state

    fun init(profileId: Long) {
        viewModelScope.launch {
            profileUseCases.getProfileById(profileId).collect {

                _state.value = ProfileSettingsDefaultLessonsState(profile = it)
                if (_state.value.profile!!.type == ProfileType.STUDENT) {
                    _state.value = _state.value.copy(
                        differentDefaultLessons = defaultLessonRepository.getDefaultLessonByClassId(
                            it!!.referenceId
                        ).size != it.defaultLessons.size
                    )
                }
            }
        }
    }

    fun onDefaultLessonChanged(defaultLesson: DefaultLesson, value: Boolean) {
        viewModelScope.launch {
            if (value) profileUseCases.enableDefaultLesson(
                profileId = state.value.profile!!.id,
                vpId = defaultLesson.vpId
            )
            else profileUseCases.disableDefaultLesson(
                profileId = state.value.profile!!.id,
                vpId = defaultLesson.vpId
            )
        }
    }

    fun onFixDefaultLessons() {
        viewModelScope.launch {
            profileUseCases.deleteDefaultLessonsFromProfile(profileId = state.value.profile!!.id)
            defaultLessonRepository.getDefaultLessonByClassId(state.value.profile!!.referenceId).forEach { dl ->
                profileUseCases.enableDefaultLesson(
                    profileId = state.value.profile!!.id,
                    vpId = dl.vpId
                )
            }
        }
    }
}

data class ProfileSettingsDefaultLessonsState(
    val profile: Profile? = null,
    val differentDefaultLessons: Boolean = false
)