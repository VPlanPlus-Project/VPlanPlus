package es.jvbabi.vplanplus.feature.exams.ui.details

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.model.Exam
import es.jvbabi.vplanplus.domain.model.ExamCategory
import es.jvbabi.vplanplus.feature.exams.domain.usecase.details.ExamDetailsUseCases
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class ExamDetailsViewModel @Inject constructor(
    private val examDetailsUseCases: ExamDetailsUseCases
) : ViewModel() {
    var state by mutableStateOf(ExamDetailsState())
        private set

    private var updateTitleJob: Job? = null
    private var canCancelUpdateTitleJob = true
    private var previousTitle: String? = null

    private var updateDateJob: Job? = null
    private var canCancelUpdateDateJob = true
    private var previousDate: LocalDate? = null

    private var updateTypeJob: Job? = null
    private var canCancelUpdateTypeJob = true
    private var previousType: ExamCategory? = null

    private var updateDescriptionJob: Job? = null
    private var canCancelUpdateDescriptionJob = true
    private var previousDescription: String? = null

    fun init(examId: Int) {
        viewModelScope.launch {
            combine(
                listOf(
                    examDetailsUseCases.getExamUseCase(examId),
                    examDetailsUseCases.getCurrentProfileUseCase()
                )
            ) { data ->
                val exam = data[0] as? Exam
                val currentProfile = data[1] as? ClassProfile

                state.copy(
                    exam = exam,
                    currentProfile = currentProfile
                )
            }.collect {
                state = it
            }
        }
    }

    fun doAction(event: ExamDetailsEvent) {
        when (event) {
            is ExamDetailsEvent.UpdateTitle -> {
                if (canCancelUpdateTitleJob) {
                    state = state.copy(editModeTitle = event.newTitle)
                    updateTitleJob?.cancel()
                    updateTitleJob = viewModelScope.launch {
                        delay(1000)
                        canCancelUpdateTitleJob = false
                        state = state.copy(editModeUpdatingTitleState = UpdatingState.UPDATING)
                        previousTitle = state.exam?.title
                        state = state.copy(
                            editModeUpdatingTitleState = examDetailsUseCases.updateTitleUseCase(state.exam!!.id, event.newTitle).asChangeResultToUpdatingState(),
                            canUndoTitleUpdate = true,
                        )
                        canCancelUpdateTitleJob = true
                        delay(3000)
                        if (state.editModeUpdatingTitleState == UpdatingState.SUCCESS) state = state.copy(editModeUpdatingTitleState = UpdatingState.IDLE)
                    }
                }
            }
            is ExamDetailsEvent.UndoTitleUpdate -> {
                if (previousTitle == null) return
                state = state.copy(editModeUpdatingTitleState = UpdatingState.UPDATING, editModeTitle = previousTitle)
                canCancelUpdateTitleJob = false
                viewModelScope.launch {
                    state = state.copy(
                        editModeTitle = previousTitle,
                        canUndoTitleUpdate = false,
                        editModeUpdatingTitleState = examDetailsUseCases.updateTitleUseCase(state.exam!!.id, previousTitle ?: state.exam!!.title).asChangeResultToUpdatingState(),
                    )
                    canCancelUpdateTitleJob = true
                    delay(3000) // wait if user inputs more
                    if (state.editModeUpdatingTitleState == UpdatingState.SUCCESS) state = state.copy(editModeUpdatingTitleState = UpdatingState.IDLE)
                }
            }

            is ExamDetailsEvent.UpdateDate -> {
                if (!canCancelUpdateDateJob || state.exam?.date == event.newDate) return
                state = state.copy(editModeUpdatingDateState = UpdatingState.UPDATING, editModeDate = event.newDate)
                previousDate = state.exam?.date
                canCancelUpdateDateJob = false
                updateDateJob?.cancel()
                updateDateJob = viewModelScope.launch {
                    state = state.copy(
                        editModeDate = event.newDate,
                        canUndoDateUpdate = true,
                        editModeUpdatingDateState = examDetailsUseCases.updateDateUseCase(state.exam!!.id, event.newDate).asChangeResultToUpdatingState()
                    )
                    canCancelUpdateDateJob = true
                    delay(3000) // wait if user inputs more
                    if (state.editModeUpdatingDateState == UpdatingState.SUCCESS) state = state.copy(editModeUpdatingDateState = UpdatingState.IDLE)
                }
            }
            is ExamDetailsEvent.UndoDateUpdate -> {
                if (previousDate == null) return
                state = state.copy(editModeUpdatingDateState = UpdatingState.UPDATING, editModeDate = previousDate)
                canCancelUpdateDateJob = false
                updateDateJob = viewModelScope.launch {
                    examDetailsUseCases.updateDateUseCase(state.exam!!.id, previousDate ?: state.exam!!.date)
                    state = state.copy(
                        editModeDate = previousDate,
                        canUndoDateUpdate = false,
                        editModeUpdatingDateState = examDetailsUseCases.updateDateUseCase(state.exam!!.id, previousDate ?: state.exam!!.date).asChangeResultToUpdatingState()
                    )
                    canCancelUpdateDateJob = true
                    delay(3000) // wait if user inputs more
                    if (state.editModeUpdatingDateState == UpdatingState.SUCCESS) state = state.copy(editModeUpdatingDateState = UpdatingState.IDLE)
                }
            }

            is ExamDetailsEvent.UpdateType -> {
                if (!canCancelUpdateTypeJob || state.exam?.type == event.newType) return
                state = state.copy(editModeUpdatingTypeState = UpdatingState.UPDATING, editModeType = event.newType)
                previousType = state.exam?.type
                canCancelUpdateTypeJob = false
                updateTypeJob?.cancel()
                updateTypeJob = viewModelScope.launch {
                    state = state.copy(
                        editModeType = event.newType,
                        canUndoTypeUpdate = true,
                        editModeUpdatingTypeState = examDetailsUseCases.updateCategoryUseCase(state.exam!!.id, event.newType).asChangeResultToUpdatingState(),
                    )
                    canCancelUpdateTypeJob = true
                    delay(3000) // wait if user inputs more
                    if (state.editModeUpdatingTypeState == UpdatingState.SUCCESS) state = state.copy(editModeUpdatingTypeState = UpdatingState.IDLE)
                }
            }
            is ExamDetailsEvent.UndoTypeUpdate -> {
                if (previousType == null) return
                state = state.copy(editModeUpdatingTypeState = UpdatingState.UPDATING, editModeType = previousType)
                canCancelUpdateTypeJob = false
                updateTypeJob = viewModelScope.launch {
                    state = state.copy(
                        editModeType = previousType,
                        canUndoTypeUpdate = false,
                        editModeUpdatingTypeState = examDetailsUseCases.updateCategoryUseCase(state.exam!!.id, previousType ?: state.exam!!.type).asChangeResultToUpdatingState(),
                    )
                    canCancelUpdateTypeJob = true
                    delay(3000)
                    if (state.editModeUpdatingTypeState == UpdatingState.SUCCESS) state = state.copy(editModeUpdatingTypeState = UpdatingState.IDLE)
                }
            }

            is ExamDetailsEvent.UpdateDescription -> {
                if (canCancelUpdateDescriptionJob) {
                    state = state.copy(editModeDescription = event.newDescription)
                    updateDescriptionJob?.cancel()
                    updateDescriptionJob = viewModelScope.launch {
                        delay(1000)
                        canCancelUpdateDescriptionJob = false
                        state = state.copy(editModeUpdatingDescriptionState = UpdatingState.UPDATING)
                        previousDescription = state.exam?.description
                        state = state.copy(
                            editModeUpdatingDescriptionState = examDetailsUseCases.updateExamDetailsUseCase(state.exam!!.id, event.newDescription).asChangeResultToUpdatingState(),
                            canUndoDescriptionUpdate = true
                        )
                        canCancelUpdateDescriptionJob = true
                        delay(3000)
                        if (state.editModeUpdatingDescriptionState == UpdatingState.SUCCESS) state = state.copy(editModeUpdatingDescriptionState = UpdatingState.IDLE)
                    }
                }
            }
            is ExamDetailsEvent.UndoDescriptionUpdate -> {
                if (previousDescription == null) return
                state = state.copy(editModeUpdatingDescriptionState = UpdatingState.UPDATING, editModeDescription = previousDescription)
                canCancelUpdateDescriptionJob = false
                updateDescriptionJob = viewModelScope.launch {
                    state = state.copy(
                        editModeDescription = previousDescription,
                        canUndoDescriptionUpdate = false,
                        editModeUpdatingDescriptionState = examDetailsUseCases.updateExamDetailsUseCase(state.exam!!.id, previousDescription ?: state.exam!!.description).asChangeResultToUpdatingState()
                    )
                    canCancelUpdateDescriptionJob = true
                    delay(3000)
                    if (state.editModeUpdatingDescriptionState == UpdatingState.SUCCESS) state = state.copy(editModeUpdatingDescriptionState = UpdatingState.IDLE)
                }
            }

            is ExamDetailsEvent.UpdateReminderDays -> {
                if (state.exam?.id != null) {
                    viewModelScope.launch {
                        examDetailsUseCases.updateReminderDaysUseCase(state.exam!!.id, event.newDays)
                    }
                }
            }

            is ExamDetailsEvent.DeleteExam -> {
                viewModelScope.launch {
                    if (examDetailsUseCases.deleteExamUseCase(state.exam!!.id)) event.onSuccess.invoke()
                }
            }
        }
    }
}

data class ExamDetailsState(
    val exam: Exam? = null,
    val currentProfile: ClassProfile? = null,

    val editModeTitle: String? = null,
    val canUndoTitleUpdate: Boolean = false,
    val editModeUpdatingTitleState: UpdatingState = UpdatingState.IDLE,

    val editModeDate: LocalDate? = null,
    val canUndoDateUpdate: Boolean = false,
    val editModeUpdatingDateState: UpdatingState = UpdatingState.IDLE,

    val editModeType: ExamCategory? = null,
    val canUndoTypeUpdate: Boolean = false,
    val editModeUpdatingTypeState: UpdatingState = UpdatingState.IDLE,

    val editModeDescription: String? = null,
    val canUndoDescriptionUpdate: Boolean = false,
    val editModeUpdatingDescriptionState: UpdatingState = UpdatingState.IDLE,
) {
    val isUserAllowedToEdit: Boolean
        get() = exam != null && ((exam is Exam.Cloud && exam.createdBy.id == currentProfile?.vppId?.id) || exam is Exam.Local)

    val wasChangeSuccessful: Boolean
        get() = listOf(editModeUpdatingTitleState, editModeUpdatingDescriptionState, editModeUpdatingDateState, editModeUpdatingTypeState).none { it == UpdatingState.FAILURE }
}

sealed class ExamDetailsEvent {
    data class UpdateTitle(val newTitle: String): ExamDetailsEvent()
    data object UndoTitleUpdate: ExamDetailsEvent()

    data class UpdateDate(val newDate: LocalDate): ExamDetailsEvent()
    data object UndoDateUpdate: ExamDetailsEvent()

    data class UpdateType(val newType: ExamCategory): ExamDetailsEvent()
    data object UndoTypeUpdate: ExamDetailsEvent()

    data class UpdateDescription(val newDescription: String): ExamDetailsEvent()
    data object UndoDescriptionUpdate: ExamDetailsEvent()

    data class UpdateReminderDays(val newDays: Set<Int>): ExamDetailsEvent()

    data class DeleteExam(val onSuccess: () -> Unit): ExamDetailsEvent()
}

enum class UpdatingState {
    IDLE,
    UPDATING,
    SUCCESS,
    FAILURE
}

private fun Boolean.asChangeResultToUpdatingState() = if (this) UpdatingState.SUCCESS else UpdatingState.FAILURE