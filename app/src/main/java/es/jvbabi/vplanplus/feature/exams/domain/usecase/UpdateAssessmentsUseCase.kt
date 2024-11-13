package es.jvbabi.vplanplus.feature.exams.domain.usecase

import android.util.Log
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.model.Exam
import es.jvbabi.vplanplus.domain.model.ExamCategory
import es.jvbabi.vplanplus.domain.repository.NotificationRepository
import es.jvbabi.vplanplus.domain.repository.OpenScreenTask
import es.jvbabi.vplanplus.domain.repository.ProfileRepository
import es.jvbabi.vplanplus.domain.repository.StringRepository
import es.jvbabi.vplanplus.domain.repository.VppIdRepository
import es.jvbabi.vplanplus.domain.usecase.general.GetDefaultLessonByIdentifierUseCase
import es.jvbabi.vplanplus.feature.exams.domain.repository.ExamRepository
import es.jvbabi.vplanplus.feature.logs.data.repository.LogRecordRepository
import es.jvbabi.vplanplus.ui.NotificationDestination
import es.jvbabi.vplanplus.ui.screens.Screen
import es.jvbabi.vplanplus.ui.stringResource
import es.jvbabi.vplanplus.util.addNotNull
import kotlinx.coroutines.flow.first
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class UpdateAssessmentsUseCase(
    private val examRepository: ExamRepository,
    private val profileRepository: ProfileRepository,
    private val logRepository: LogRecordRepository,
    private val vppIdRepository: VppIdRepository,
    private val notificationRepository: NotificationRepository,
    private val stringRepository: StringRepository,
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
                    .filter {
                        val defaultLesson = getDefaultLessonByIdentifierUseCase(it.subject) ?: run {
                            logRepository.log("UpdateAssessmentsUseCase", "Cannot find default lesson ${it.subject}")
                            return@filter false
                        }
                        profile.isDefaultLessonEnabled(defaultLesson.vpId)
                    }

                existingAssessments
                    .map { it.id }
                    .toSet()
                    .minus(downloadedAssessments.map { it.id }.toSet())
                    .forEach { examRepository.deleteExamById(it, profile, onlyLocal = true) }

                val newExams = mutableListOf<Exam>()

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
                        if (existing == null) {
                            newExams.addNotNull(examRepository.getExamById(new.id, profile).first())
                        }
                    }

                newExams
                    .filterIsInstance<Exam.Cloud>()
                    .filter { it.createdBy != profile.vppId }
                    .filter { it.date.isAfter(LocalDate.now()) }
                    .let {
                        if (it.isEmpty()) return@let
                        if (it.size == 1) {
                            // one notification
                            notificationRepository.sendNotification(
                                channelId = NotificationRepository.CHANNEL_ID_ASSESSMENTS,
                                id = NotificationRepository.CHANNEL_DEFAULT_ASSESSMENTS_ID.hashCode() + profile.id.hashCode(),
                                title = stringRepository.getString(R.string.notification_assessmentsNewTitle),
                                subtitle = profile.displayName,
                                message = stringRepository.getString(
                                    R.string.notification_assessmentsNewContent,
                                    it.first().createdBy.name,
                                    stringRepository.getString(it.first().type.stringResource()),
                                    it.first().date.format(DateTimeFormatter.ofPattern("EEEE, d. MMMM yyyy", Locale.getDefault()))
                                ),
                                icon = R.drawable.vpp,
                                onClickTask = OpenScreenTask(destination = Json.encodeToString(
                                    NotificationDestination(
                                        screen = "exam/item",
                                        profileId = profile.id.toString(),
                                        payload = Json.encodeToString(Screen.ExamDetailsScreen(it.first().id))
                                    )
                                )),
                            )
                            logRepository.log("UpdateAssessmentsUseCase", "Sending single notification for ${it.first().id}")
                            Log.d("UpdateAssessmentsUseCase", "Sending single notification for ${it.first().id}")
                            return@let
                        }

                        notificationRepository.sendNotification(
                            channelId = NotificationRepository.CHANNEL_ID_ASSESSMENTS,
                            id = NotificationRepository.CHANNEL_DEFAULT_ASSESSMENTS_ID.hashCode() + profile.id.hashCode(),
                            title = stringRepository.getString(R.string.notification_assessmentsMultipleNewTitle),
                            subtitle = profile.displayName,
                            message = stringRepository.getPlural(
                                R.plurals.notification_assessmentsNewContent,
                                it.size,
                                it.size
                            ),
                            icon = R.drawable.vpp,
                            onClickTask = OpenScreenTask(destination = Json.encodeToString(
                                NotificationDestination(
                                    screen = "home",
                                    profileId = profile.id.toString(),
                                    payload = null
                                )
                            )),
                        )

                        logRepository.log("UpdateAssessmentsUseCase", "Sending notification for ${it.size} assessments")
                        Log.d("UpdateAssessmentsUseCase", "Sending notification for ${it.size} assessments")

                        // many notifications
                    }
            }
    }
}