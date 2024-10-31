package es.jvbabi.vplanplus.feature.exams.domain.usecase.new_exam

import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.model.DefaultLesson
import es.jvbabi.vplanplus.domain.model.ExamCategory
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
        type: ExamCategory,
        topic: String,
        details: String?,
        saveType: SaveType,
        remindDaysBefore: Set<Int>?
    ): Boolean {
        val profile = (getCurrentProfileUseCase().first() as? ClassProfile) ?: return false

        val id = if (saveType.isOnline()) {
            examRepository.insertExamCloud(
                subject = subject,
                date = date,
                type = type,
                topic = topic,
                details = details,
                profile = profile,
                isPublic = saveType == SaveType.SHARED,
            ).getOrNull() ?: return false
        } else null

        return examRepository.upsertExamLocally(
            id = id,
            subject = subject,
            date = date,
            type = type,
            topic = topic,
            details = details,
            profile = profile,
            remindDaysBefore = remindDaysBefore
        ).first().let { true }
    }
}