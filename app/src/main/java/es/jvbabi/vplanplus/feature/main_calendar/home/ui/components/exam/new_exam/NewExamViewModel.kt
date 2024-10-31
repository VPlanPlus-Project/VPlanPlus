package es.jvbabi.vplanplus.feature.main_calendar.home.ui.components.exam.new_exam

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.model.DefaultLesson
import es.jvbabi.vplanplus.domain.model.ExamCategory
import es.jvbabi.vplanplus.feature.exams.domain.usecase.new_exam.NewExamUseCases
import es.jvbabi.vplanplus.feature.main_homework.add.ui.SaveType
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class NewExamViewModel @Inject constructor(
    private val newExamUseCases: NewExamUseCases
) : ViewModel() {
    private val _state = MutableStateFlow(NewExamState())
    val state = _state.asStateFlow()
    private var flowJob: Job? = null

    fun doAction(event: NewExamUiEvent) {
        when (event) {
            is NewExamUiEvent.UpdateTitle -> _state.value = _state.value.copy(topic = event.to)
            is NewExamUiEvent.UpdateDate -> _state.value = _state.value.copy(date = event.to)
            is NewExamUiEvent.UpdateSubject -> _state.value = _state.value.copy(subject = event.to)
            is NewExamUiEvent.UpdateCategory -> _state.value = _state.value.copy(category = event.to)
            is NewExamUiEvent.UpdateReminderDays -> _state.value = _state.value.copy(remindDaysBefore = event.to)
            is NewExamUiEvent.UpdateDescription -> _state.value = _state.value.copy(details = event.to)
            is NewExamUiEvent.UpdateStoreType -> _state.value = _state.value.copy(storeType = event.to)

            is NewExamUiEvent.OnSaveClicked -> {
                viewModelScope.launch {
                    _state.value = _state.value.copy(saveSuccess = null)
                    val result = newExamUseCases.saveExamUseCase(
                        subject = state.value.subject ?: return@launch,
                        date = state.value.date ?: return@launch,
                        type = state.value.category ?: return@launch,
                        topic = state.value.topic,
                        details = state.value.details,
                        saveType = state.value.storeType ?: return@launch,
                        remindDaysBefore = state.value.remindDaysBefore
                    )
                    _state.value = _state.value.copy(
                        saveSuccess = result
                    )
                }
            }
            is NewExamUiEvent.OnInit -> init(date = event.date)
        }
    }

    init {
        init()
    }

    private fun init(
        date: LocalDate? = null
    ) {
        flowJob?.cancel()
        _state.value = NewExamState(date = date)
        flowJob = viewModelScope.launch {
            _state.value = _state.value.copy(subjects = newExamUseCases.getDefaultLessonsUseCase())
            combine(
                listOf(
                    newExamUseCases.getCurrentProfileUseCase(),
                    newExamUseCases.isDeveloperModeEnabled()
                )
            ) { data ->
                val profile = data[0] as? ClassProfile ?: return@combine _state.value
                val isDeveloperModeEnabled = data[1] as? Boolean ?: return@combine _state.value
                return@combine _state.value.copy(
                    currentProfile = profile,
                    subjects = _state.value.subjects.filter { profile.isDefaultLessonEnabled(it.vpId) }.toSet(),
                    isDeveloperModeEnabled = isDeveloperModeEnabled,
                    currentLessons = newExamUseCases.getCurrentLessonsUseCase()
                )
            }.collect {
                _state.value = it
            }
        }
    }
}

data class NewExamState(
    val subjects: Set<DefaultLesson> = emptySet(),
    val currentLessons: Set<DefaultLesson> = emptySet(),
    val currentProfile: ClassProfile? = null,
    val isDeveloperModeEnabled: Boolean = false,

    val subject: DefaultLesson? = null,

    val date: LocalDate? = null,

    val category: ExamCategory? = null,

    val topic: String = "",

    val details: String = "",

    val storeType: SaveType? = null,

    val remindDaysBefore: Set<Int>? = null,

    val saveSuccess: Boolean? = null
)

sealed class NewExamUiEvent {
    data class UpdateTitle(val to: String) : NewExamUiEvent()
    data class UpdateDate(val to: LocalDate) : NewExamUiEvent()
    data class UpdateSubject(val to: DefaultLesson) : NewExamUiEvent()
    data class UpdateCategory(val to: ExamCategory) : NewExamUiEvent()
    data class UpdateReminderDays(val to: Set<Int>) : NewExamUiEvent()
    data class UpdateDescription(val to: String) : NewExamUiEvent()
    data class UpdateStoreType(val to: SaveType) : NewExamUiEvent()

    data object OnSaveClicked : NewExamUiEvent()
    data class OnInit(
        val date: LocalDate? = null
    ) : NewExamUiEvent()
}