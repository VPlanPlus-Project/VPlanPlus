package es.jvbabi.vplanplus.feature.main_homework.add.ui

import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.model.DefaultLesson
import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentProfileUseCase
import es.jvbabi.vplanplus.domain.usecase.general.HOMEWORK_DOCUMENT_BALLOON
import es.jvbabi.vplanplus.feature.main_homework.add.domain.usecase.AddHomeworkUseCases
import es.jvbabi.vplanplus.feature.main_homework.add.domain.usecase.HomeworkDocumentType
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.HomeworkModificationResult
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class AddHomeworkViewModel @Inject constructor(
    private val addHomeworkUseCases: AddHomeworkUseCases,
    private val getCurrentProfileUseCase: GetCurrentProfileUseCase
) : ViewModel() {

    val state = mutableStateOf(AddHomeworkState())

    fun init() {
        state.value = AddHomeworkState()
    }

    init {
        viewModelScope.launch {
            combine(
                listOf(
                    getCurrentProfileUseCase(),
                    addHomeworkUseCases.isBalloonUseCase(HOMEWORK_DOCUMENT_BALLOON, true)
                )
            ) { data ->
                val profile = data[0] as Profile?
                val showDocumentBalloon = data[1] as Boolean

                if (profile == null) return@combine null

                val defaultLessons = addHomeworkUseCases.getDefaultLessonsUseCase()
                val defaultLessonsFiltered = defaultLessons.any { (profile as? ClassProfile)?.isDefaultLessonEnabled(it.vpId) ?: true }

                state.value.copy(
                    defaultLessons = defaultLessons.filter { (profile as? ClassProfile)?.isDefaultLessonEnabled(it.vpId) ?: true },
                    username = (profile as? ClassProfile)?.vppId?.name,
                    canUseCloud = (profile as? ClassProfile)?.vppId != null,
                    saveType = state.value.saveType ?: if ((profile as? ClassProfile)?.vppId != null) SaveType.CLOUD else SaveType.LOCAL,
                    defaultLessonsFiltered = defaultLessonsFiltered,
                    initDone = true,
                    showDocumentsBalloon = showDocumentBalloon
                )
            }.collect {
                if (it != null) state.value = it
            }
        }
    }

    fun setDefaultLesson(defaultLesson: DefaultLesson?) {
        state.value = state.value.copy(selectedDefaultLesson = defaultLesson)
    }

    /**
     * Request to save the homework, will return if not all requirements are met
     */
    fun save(onSuccess: () -> Unit) {
        viewModelScope.launch {
            if (!state.value.canSave) return@launch
            state.value = state.value.copy(isLoading = true)
            state.value = state.value.copy(
                result = addHomeworkUseCases.saveHomeworkUseCase(
                    until = state.value.until!!,
                    defaultLesson = state.value.selectedDefaultLesson,
                    tasks = state.value.tasks,
                    shareWithClass = state.value.saveType == SaveType.SHARED,
                    storeInCloud = state.value.saveType != SaveType.LOCAL,
                    documentUris = state.value.documents
                ),
                isLoading = false
            )
            if (state.value.result == HomeworkModificationResult.SUCCESS_OFFLINE || state.value.result == HomeworkModificationResult.SUCCESS_ONLINE_AND_OFFLINE) {
                onSuccess()
            }
        }
    }

    fun onUiAction(event: AddHomeworkUiEvent) {
        when (event) {
            is DeleteTask -> removeTask(event.index)
            is CreateTask -> addTask(event.content)
            is UpdateTask -> updateTask(event.index, event.content)
            is UpdateSaveType -> setSaveType(event.saveType)
            is UpdateUntil -> updateDueTo(event.until)
            is AddDocument -> addDocument(event.documentUri)
            is AddImage -> addImage(event.imageUri)
            is RemoveDocument -> removeDocument(event.documentUri)
            is SaveHomework -> save(event.onSuccess)

            HideDocumentBalloon -> viewModelScope.launch {
                addHomeworkUseCases.setBalloonUseCase(HOMEWORK_DOCUMENT_BALLOON, false)
            }
        }
    }

    private fun removeTask(index: Int) {
        state.value = state.value.copy(tasks = state.value.tasks.toMutableList().apply { removeAt(index) })
    }

    private fun addTask(content: String) {
        state.value = state.value.copy(tasks = state.value.tasks + content)
    }

    private fun updateTask(index: Int, content: String) {
        state.value = state.value.copy(tasks = state.value.tasks.toMutableList().apply { set(index, content) })
    }

    private fun setSaveType(saveType: SaveType) {
        state.value = state.value.copy(saveType = saveType)
    }

    private fun addDocument(documentUri: Uri) {
        state.value = state.value.copy(documents = state.value.documents + (documentUri to HomeworkDocumentType.PDF))
    }

    private fun addImage(imageUri: Uri) {
        state.value = state.value.copy(documents = state.value.documents + (imageUri to HomeworkDocumentType.JPG))
    }

    private fun removeDocument(documentUri: Uri) {
        state.value = state.value.copy(documents = state.value.documents.toMutableMap().apply { remove(documentUri) })
    }

    private fun updateDueTo(dueTo: LocalDate) {
        state.value = state.value.copy(until = dueTo)
    }
}

data class AddHomeworkState(
    val initDone: Boolean = false,

    val canUseCloud: Boolean = true,
    val username: String? = null,

    val defaultLessonsFiltered: Boolean = false,
    val defaultLessons: List<DefaultLesson> = emptyList(),
    val selectedDefaultLesson: DefaultLesson? = null,

    val until: LocalDate? = null,

    val saveType: SaveType? = null,

    val tasks: List<String> = emptyList(),
    val newTask: String = "",

    val result: HomeworkModificationResult? = null,
    val isLoading: Boolean = false,

    val showVppIdStorageBalloon: Boolean = false,
    val showDocumentsBalloon: Boolean = false,

    val documents: Map<Uri, HomeworkDocumentType> = emptyMap()
) {
    val canSave: Boolean
        get() = until != null && tasks.all { it.isNotBlank() } && tasks.isNotEmpty() && !isLoading && !isInvalidSaveTypeSelected

    val isInvalidSaveTypeSelected: Boolean
        get() = (saveType == SaveType.SHARED || saveType == SaveType.CLOUD) && !canUseCloud
}

enum class SaveType {
    LOCAL, CLOUD, SHARED
}

sealed class AddHomeworkUiEvent

data class DeleteTask(val index: Int): AddHomeworkUiEvent()
data class CreateTask(val content: String): AddHomeworkUiEvent()
data class UpdateTask(val index: Int, val content: String) : AddHomeworkUiEvent()
data class UpdateSaveType(val saveType: SaveType) : AddHomeworkUiEvent()

data class UpdateUntil(val until: LocalDate) : AddHomeworkUiEvent()

data class AddDocument(val documentUri: Uri) : AddHomeworkUiEvent()
data class AddImage(val imageUri: Uri) : AddHomeworkUiEvent()
data class RemoveDocument(val documentUri: Uri) : AddHomeworkUiEvent()

data class SaveHomework(val onSuccess: () -> Unit) : AddHomeworkUiEvent()

data object HideDocumentBalloon : AddHomeworkUiEvent()