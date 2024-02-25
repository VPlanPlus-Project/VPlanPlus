package es.jvbabi.vplanplus.feature.homework.add.domain.usecase

import es.jvbabi.vplanplus.domain.model.DefaultLesson
import es.jvbabi.vplanplus.domain.usecase.general.GetClassByProfileUseCase
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentIdentityUseCase
import es.jvbabi.vplanplus.feature.homework.shared.domain.model.Homework
import es.jvbabi.vplanplus.feature.homework.shared.domain.model.HomeworkTask
import es.jvbabi.vplanplus.feature.homework.shared.domain.repository.HomeworkRepository
import kotlinx.coroutines.flow.first
import java.time.LocalDate

class SaveHomeworkUseCase(
    private val homeworkRepository: HomeworkRepository,
    private val getCurrentIdentityUseCase: GetCurrentIdentityUseCase,
    private val getClassByProfileUseCase: GetClassByProfileUseCase
) {
    suspend operator fun invoke(
        offline: Boolean,
        until: LocalDate,
        defaultLesson: DefaultLesson,
        tasks: List<String>,
    ) {
        val identity = getCurrentIdentityUseCase().first() ?: return
        if (offline) {
            val id = homeworkRepository.findLocalId()
            val homeworkTasks = tasks.map { content ->
                val taskId = homeworkRepository.findLocalTaskId()
                HomeworkTask(
                    id = taskId.toInt(),
                    content = content,
                    done = false
                )
            }

            val homework = Homework(
                id = id.toInt(),
                createdBy = identity.vppId,
                classes = getClassByProfileUseCase(identity.profile!!)!!,
                until = until,
                tasks = homeworkTasks,
                createdAt = LocalDate.now(),
                defaultLesson = defaultLesson
            )

            homeworkRepository.insertHomeworkLocally(homework)
        }
    }
}