package es.jvbabi.vplanplus.feature.exams.domain.usecase.new_exam

import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.model.DefaultLesson
import es.jvbabi.vplanplus.domain.model.ExamType
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentProfileUseCase
import es.jvbabi.vplanplus.feature.exams.domain.repository.ExamRepository
import es.jvbabi.vplanplus.feature.main_homework.add.ui.SaveType
import es.jvbabi.vplanplus.feature.main_homework.add.ui.isOnline
import kotlinx.coroutines.flow.first
import java.time.LocalDate

class SaveExamUseCase(
    private val getCurrentProfileUseCase: GetCurrentProfileUseCase,
    private val examRepository: ExamRepository
) {
    suspend operator fun invoke(
        subject: DefaultLesson,
        date: LocalDate,
        type: ExamType,
        topic: String,
        details: String?,
        saveType: SaveType
    ): Boolean {
        val profile = (getCurrentProfileUseCase().first() as? ClassProfile) ?: return false

        val id: Int?
        if (saveType.isOnline()) {
            id = TODO()
        } else {
            id = null
        }
        return examRepository.upsertExamLocally(
            id = id,
            subject = subject,
            date = date,
            type = type,
            topic = topic,
            details = details,
            group = profile.group,
            author = profile.vppId,
        ).first().let { true }
    }
}