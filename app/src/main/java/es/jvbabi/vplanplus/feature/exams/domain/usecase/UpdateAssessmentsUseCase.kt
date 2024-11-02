package es.jvbabi.vplanplus.feature.exams.domain.usecase

import android.util.Log
import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.model.ExamCategory
import es.jvbabi.vplanplus.domain.repository.ProfileRepository
import es.jvbabi.vplanplus.domain.repository.VppIdRepository
import es.jvbabi.vplanplus.domain.usecase.general.GetDefaultLessonByIdentifierUseCase
import es.jvbabi.vplanplus.feature.exams.domain.repository.ExamRepository
import es.jvbabi.vplanplus.feature.logs.data.repository.LogRecordRepository
import kotlinx.coroutines.flow.first
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.ZonedDateTime

class UpdateAssessmentsUseCase(
    private val examRepository: ExamRepository,
    private val profileRepository: ProfileRepository,
    private val logRepository: LogRecordRepository,
    private val vppIdRepository: VppIdRepository,
    private val getDefaultLessonByIdentifierUseCase: GetDefaultLessonByIdentifierUseCase
) {
    suspend operator fun invoke() {
        profileRepository
            .getProfiles()
            .first()
            .filterIsInstance<ClassProfile>()
            .forEach { profile ->
                val existingAssessments = examRepository.getExams(profile = profile).first()
                val data = examRepository.downloadAssessments(profile)
                if (!data.isSuccess) {
                    Log.e("UpdateAssessmentsUseCase", "Error downloading assessments", data.exceptionOrNull())
                    logRepository.log("UpdateAssessmentsUseCase", "Error downloading assessments ${data.exceptionOrNull()}")
                }
                val downloadedAssessments = data.getOrNull().orEmpty()

                existingAssessments
                    .map { it.id }
                    .toSet()
                    .minus(downloadedAssessments.map { it.id }.toSet())
                    .forEach { examRepository.deleteExamById(it, profile, onlyLocal = true) }

                downloadedAssessments
                    .forEach forEachDownloaded@{ new ->
                        val existing = existingAssessments.find { it.id == new.id }
                        examRepository.upsertExamLocally(
                            id = new.id,
                            topic = new.title,
                            details = new.description,
                            date = LocalDateTime.ofEpochSecond(new.date.toLong(), 0, ZoneOffset.UTC).toLocalDate(),
                            type = ExamCategory.of(new.type),
                            isPublic = new.isPublic,
                            createdBy = vppIdRepository.getVppId(new.createdBy.toLong(), profile.getSchool(), false),
                            createdAt = ZonedDateTime.ofInstant(Instant.ofEpochSecond(new.createdAt.toLong()), ZoneOffset.UTC),
                            profile = profile,
                            subject = getDefaultLessonByIdentifierUseCase(new.subject) ?: run {
                                logRepository.log("UpdateAssessmentsUseCase", "Cannot find default lesson ${new.subject}")
                                return@forEachDownloaded
                            },
                            remindDaysBefore = existing?.remindDaysBefore
                        )
                    }
            }
    }
}