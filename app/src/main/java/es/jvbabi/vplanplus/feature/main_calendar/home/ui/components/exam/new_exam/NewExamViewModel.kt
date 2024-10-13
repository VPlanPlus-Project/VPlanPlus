package es.jvbabi.vplanplus.feature.main_calendar.home.ui.components.exam.new_exam

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.model.DefaultLesson
import es.jvbabi.vplanplus.domain.model.ExamType
import es.jvbabi.vplanplus.feature.exams.domain.usecase.new_exam.NewExamUseCases
import es.jvbabi.vplanplus.feature.main_homework.add.ui.SaveType
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

    fun doAction(event: NewExamUiEvent) {
        when (event) {
            is NewExamUiEvent.OnSubjectsClicked -> {
                if (state.value.subject != null) {
                    val before = _state.value.isSubjectsExpanded
                    hideAllSections()
                    _state.value = _state.value.copy(isSubjectsExpanded = !before)
                }
            }
            is NewExamUiEvent.OnSubjectSelected -> {
                hideAllSections()
                _state.value = _state.value.copy(
                    subject = event.subject,
                    isDateExpanded = _state.value.date == null,
                )
            }

            is NewExamUiEvent.OnDateClicked -> {
                if (state.value.date != null) {
                    val before = _state.value.isDateExpanded
                    hideAllSections()
                    _state.value = _state.value.copy(isDateExpanded = !before)
                }
            }
            is NewExamUiEvent.OnDateSelected -> {
                hideAllSections()
                _state.value = _state.value.copy(
                    date = event.date,
                    isTypeExpanded = state.value.type == null
                )
            }

            is NewExamUiEvent.OnTypeClicked -> {
                if (state.value.type != null) {
                    val before = _state.value.isTypeExpanded
                    hideAllSections()
                    _state.value = _state.value.copy(isTypeExpanded = !before)
                }
            }
            is NewExamUiEvent.OnTypeSelected -> {
                hideAllSections()
                _state.value = _state.value.copy(
                    type = event.type,
                    remindDaysBefore = null,
                    isTopicExpanded = state.value.topic == null
                )
            }

            is NewExamUiEvent.OnTopicClicked -> {
                if (state.value.topic != null) {
                    val before = _state.value.isTopicExpanded
                    hideAllSections()
                    _state.value = _state.value.copy(isTopicExpanded = !before)
                }
            }
            is NewExamUiEvent.OnTopicSelected -> {
                hideAllSections()
                _state.value = _state.value.copy(
                    topic = event.topic,
                    isDetailsExpanded = state.value.details == null
                )
            }

            is NewExamUiEvent.OnDetailsClicked -> {
                val before = _state.value.isDetailsExpanded
                hideAllSections()
                _state.value = _state.value.copy(isDetailsExpanded = !before)
            }
            is NewExamUiEvent.OnDetailsSelected -> {
                hideAllSections()
                _state.value = _state.value.copy(
                    details = event.details,
                    isStorageExpanded = state.value.saveAndShare == null
                )
            }

            is NewExamUiEvent.OnStorageClicked -> {
                val before = _state.value.isStorageExpanded
                hideAllSections()
                _state.value = _state.value.copy(isStorageExpanded = !before)
            }
            is NewExamUiEvent.OnStorageSelected -> {
                hideAllSections()
                _state.value = _state.value.copy(
                    saveAndShare = event.saveAndShare,
                    isSubjectsExpanded = state.value.subject == null,
                    isReminderExpanded = state.value.remindDaysBefore == null
                )
            }

            is NewExamUiEvent.OnReminderClicked -> {
                val before = _state.value.isReminderExpanded
                hideAllSections()
                _state.value = _state.value.copy(isReminderExpanded = !before)
            }
            is NewExamUiEvent.OnRemindDaysBeforeSelected -> {
                _state.value = _state.value.copy(
                    remindDaysBefore = event.days
                )
            }

            is NewExamUiEvent.OnSaveClicked -> {
                hideAllSections()
                viewModelScope.launch {
                    _state.value = _state.value.copy(saveSuccess = null)
                    val result = newExamUseCases.saveExamUseCase(
                        subject = state.value.subject ?: return@launch,
                        date = state.value.date ?: return@launch,
                        type = state.value.type ?: return@launch,
                        topic = state.value.topic ?: return@launch,
                        details = state.value.details,
                        saveType = state.value.saveAndShare ?: return@launch,
                        remindDaysBefore = state.value.remindDaysBefore
                    )
                    _state.value = _state.value.copy(
                        saveSuccess = result
                    )
                }
            }
        }
    }

    private fun hideAllSections() {
        _state.value = _state.value.copy(
            isSubjectsExpanded = false,
            isDateExpanded = false,
            isTypeExpanded = false,
            isTopicExpanded = false,
            isDetailsExpanded = false,
            isStorageExpanded = false,
            isReminderExpanded = false
        )
    }

    init {
        viewModelScope.launch {
            _state.value = _state.value.copy(subjects = newExamUseCases.getDefaultLessonsUseCase().sortedBy { it.subject })
            combine(
                listOf(
                    newExamUseCases.getCurrentProfileUseCase()
                )
            ) { data ->
                val profile = data[0] as? ClassProfile ?: return@combine _state.value
                return@combine _state.value.copy(
                    currentProfile = profile,
                    subjects = _state.value.subjects.filter { profile.isDefaultLessonEnabled(it.vpId) }
                )
            }.collect {
                _state.value = it
            }
        }
    }
}

data class NewExamState(
    val subjects: List<DefaultLesson> = emptyList(),
    val currentProfile: ClassProfile? = null,

    val subject: DefaultLesson? = null,
    val isSubjectsExpanded: Boolean = true,

    val date: LocalDate? = null,
    val isDateExpanded: Boolean = false,

    val type: ExamType? = null,
    val isTypeExpanded: Boolean = false,

    val topic: String? = null,
    val isTopicExpanded: Boolean = false,

    val details: String? = null,
    val isDetailsExpanded: Boolean = false,

    val saveAndShare: SaveType? = null,
    val isStorageExpanded: Boolean = false,

    val isReminderExpanded: Boolean = false,
    val remindDaysBefore: Set<Int>? = null,

    val saveSuccess: Boolean? = null
) {
    val canSave: Boolean
        get() =
            subject != null &&
                    date != null &&
                    type != null &&
                    topic != null &&
                    saveAndShare != null
}

sealed class NewExamUiEvent {
    data object OnSubjectsClicked: NewExamUiEvent()
    data class OnSubjectSelected(val subject: DefaultLesson): NewExamUiEvent()

    data object OnDateClicked: NewExamUiEvent()
    data class OnDateSelected(val date: LocalDate): NewExamUiEvent()

    data object OnTypeClicked: NewExamUiEvent()
    data class OnTypeSelected(val type: ExamType): NewExamUiEvent()

    data object OnTopicClicked: NewExamUiEvent()
    data class OnTopicSelected(val topic: String): NewExamUiEvent()

    data object OnDetailsClicked: NewExamUiEvent()
    data class OnDetailsSelected(val details: String?): NewExamUiEvent()

    data object OnStorageClicked: NewExamUiEvent()
    data class OnStorageSelected(val saveAndShare: SaveType): NewExamUiEvent()

    data object OnReminderClicked: NewExamUiEvent()
    data class OnRemindDaysBeforeSelected(val days: Set<Int>): NewExamUiEvent()

    data object OnSaveClicked: NewExamUiEvent()
}