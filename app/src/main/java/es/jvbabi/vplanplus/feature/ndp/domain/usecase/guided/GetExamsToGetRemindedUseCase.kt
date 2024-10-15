package es.jvbabi.vplanplus.feature.ndp.domain.usecase.guided

import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.model.Exam
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentProfileUseCase
import es.jvbabi.vplanplus.feature.exams.domain.repository.ExamRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import java.time.LocalDate

class GetExamsToGetRemindedUseCase(
    private val examRepository: ExamRepository,
    private val getCurrentProfileUseCase: GetCurrentProfileUseCase
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    suspend operator fun invoke(): Flow<List<Exam>> = getCurrentProfileUseCase().flatMapLatest { profile ->
        flow {
            if (profile as? ClassProfile == null) {
                emit(emptyList())
                return@flow
            }
            examRepository.getExams(profile = profile).collect { exams ->
                emit(exams.filter { exam ->
                    val daysUntil = LocalDate.now().until(exam.date).days
                    daysUntil in exam.assessmentReminders.map { reminder -> reminder.daysBefore }
                })
            }
        }
    }
}