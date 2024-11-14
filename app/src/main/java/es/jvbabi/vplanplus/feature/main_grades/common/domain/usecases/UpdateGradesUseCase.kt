package es.jvbabi.vplanplus.feature.main_grades.common.domain.usecases

import android.util.Log
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.model.VppId
import es.jvbabi.vplanplus.domain.repository.NotificationAction
import es.jvbabi.vplanplus.domain.repository.NotificationRepository
import es.jvbabi.vplanplus.domain.repository.NotificationRepository.Companion.CHANNEL_ID_SYSTEM
import es.jvbabi.vplanplus.domain.repository.NotificationRepository.Companion.ID_GRADE_NEW
import es.jvbabi.vplanplus.domain.repository.OpenLinkTask
import es.jvbabi.vplanplus.domain.repository.OpenScreenTask
import es.jvbabi.vplanplus.domain.repository.ProfileRepository
import es.jvbabi.vplanplus.domain.repository.SchulverwalterTokenResponse
import es.jvbabi.vplanplus.domain.repository.StringRepository
import es.jvbabi.vplanplus.domain.repository.VppIdRepository
import es.jvbabi.vplanplus.domain.usecase.general.GetVppIdServerUseCase
import es.jvbabi.vplanplus.feature.main_grades.view.data.repository.BsUnauthorizedException
import es.jvbabi.vplanplus.feature.main_grades.view.domain.model.Grade
import es.jvbabi.vplanplus.feature.main_grades.view.domain.model.Interval
import es.jvbabi.vplanplus.feature.main_grades.view.domain.model.Year
import es.jvbabi.vplanplus.feature.main_grades.view.domain.repository.GradeRepository
import es.jvbabi.vplanplus.feature.main_grades.view.domain.repository.SchulverwalterResponse
import es.jvbabi.vplanplus.ui.NotificationDestination
import es.jvbabi.vplanplus.util.MathTools.cantor
import kotlinx.coroutines.flow.first
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class UpdateGradesUseCase(
    private val profileRepository: ProfileRepository,
    private val vppIdRepository: VppIdRepository,
    private val gradeRepository: GradeRepository,
    private val notificationRepository: NotificationRepository,
    private val stringRepository: StringRepository,
    private val getVppIdServerUseCase: GetVppIdServerUseCase
) {
    suspend operator fun invoke() {
        val unhealthyProfiles = mutableListOf<ClassProfile>()
        profileRepository
            .getProfiles().first()
            .filterIsInstance<ClassProfile>()
            .filter { it.vppId != null }
            .forEach forEachProfile@{ profile ->
                var vppId = profile.vppId ?: return@forEachProfile
                var isDone = false

                Log.d("SyncGradesUseCase", "${profile.toLogString()}: Syncing grades")

                val refreshTokenAndUpdateDb: suspend () -> VppId.ActiveVppId? = {
                    val (response, token) = vppIdRepository.requestCurrentSchulverwalterToken(vppId)
                    when (response) {
                        SchulverwalterTokenResponse.NO_TOKENS -> {
                            unhealthyProfiles.add(profile)
                            Log.e("SyncGradesUseCase", "${profile.toLogString()}: No tokens")
                            null
                        }
                        SchulverwalterTokenResponse.NETWORK_ERROR -> null
                        SchulverwalterTokenResponse.SUCCESS -> {
                            vppIdRepository.setSchulverwalterToken(vppId, token!!)
                            Log.i("SyncGradesUseCase", "${profile.toLogString()}: Token refreshed")
                            vppIdRepository.getVppId(vppId.id.toLong(), vppId.school!!, false) as VppId.ActiveVppId
                        }
                    }
                }

                val newGrades = mutableListOf<Grade>()

                repeat(2) updateGrades@{ cycle ->
                    if (isDone) return@updateGrades
                    Log.d("SyncGradesUseCase", "${profile.toLogString()}:   Cycle $cycle")
                    when (vppIdRepository.testSchulverwalterToken(vppId.schulverwalterToken ?: "")) {
                        false -> vppId = refreshTokenAndUpdateDb() ?: return@updateGrades
                        null -> return@forEachProfile
                        else -> Unit
                    }

                    Log.d("SyncGradesUseCase", "${profile.toLogString()}:   Updating years")
                    val years = try {
                        updateYears(vppId)
                    } catch (e: BsUnauthorizedException) {
                        return@updateGrades
                    }

                    Log.d("SyncGradesUseCase", "${profile.toLogString()}:   Downloading grades")
                    val (rawGradesCode, rawGrades) = gradeRepository.downloadGrades(vppId)
                    when (rawGradesCode) {
                        SchulverwalterResponse.OTHER, SchulverwalterResponse.NO_INTERNET -> return@forEachProfile
                        SchulverwalterResponse.UNAUTHORIZED -> {
                            vppId = refreshTokenAndUpdateDb() ?: return@forEachProfile
                            return@updateGrades
                        }
                        SchulverwalterResponse.SUCCESS -> rawGrades!!
                    }

                    val existing = gradeRepository.getGradesByUser(vppId).first()
                    Log.d("SyncGradesUseCase", "${profile.toLogString()}:   Existing grades: ${existing.size}, new grades: ${rawGrades.size}")
                    val builtGrades = rawGrades.map { grade ->
                        val (year, intervals) = years.filter { (_, intervals) ->
                            intervals.any { it.id.toInt() == grade.intervalId }
                        }.toList().first()
                        val builtGrade = grade.toGrade(year, intervals.first { it.id.toInt() == grade.intervalId })
                        gradeRepository.upsertGrade(builtGrade)
                        builtGrade
                    }

                    existing.filter { it !in builtGrades }.forEach {
                        gradeRepository.deleteGrade(it)
                    }

                    newGrades.addAll(builtGrades.filter { it !in existing })
                    isDone = true
                }

                if (!isDone) {
                    unhealthyProfiles.add(profile)
                    Log.e("SyncGradesUseCase", "${profile.toLogString()}: Not done")
                    return@forEachProfile
                }

                if (newGrades.isNotEmpty()) {
                    if (newGrades.size == 1) {
                        notificationRepository.sendNotification(
                            channelId = NotificationRepository.CHANNEL_ID_GRADES,
                            id = cantor(ID_GRADE_NEW, vppId.id),
                            icon = R.drawable.vpp,
                            title = stringRepository.getString(R.string.notification_newGradesTitle),
                            message = stringRepository.getString(R.string.notification_newGradeText, "${newGrades.first().value.toInt()}${newGrades.first().modifier.char}", newGrades.first().subject.name),
                            onClickTask = OpenScreenTask(
                                destination = Json.encodeToString(
                                    NotificationDestination(
                                        screen = "grades",
                                        profileId = profileRepository.getProfiles().first().firstOrNull { it is ClassProfile && it.vppId == vppId }?.id.toString()
                                    )
                                )
                            )
                        )
                    }
                    Log.i("SyncGradesUseCase", "New grades: ${newGrades.size}")
                }
            }

        unhealthyProfiles.forEach {
            Log.e("SyncGradesUseCase", "Unhealthy profile: $it")
            if (it.vppId == null) return@forEach
            val link = getVppIdServerUseCase().first().uiHost + "/app/schulverwalterconnection/connectionrestored?forcelogout"
            notificationRepository.sendNotification(
                CHANNEL_ID_SYSTEM,
                id = cantor(80000, it.vppId.id),
                icon = R.drawable.vpp,
                title = stringRepository.getString(R.string.notification_gradesUnhealthyProfileTitle),
                message = stringRepository.getString(R.string.notification_gradesUnhealthyProfileText),
                onClickTask = OpenLinkTask(link),
                actions = listOf(
                    NotificationAction(
                        title = stringRepository.getString(R.string.fix),
                        task = OpenLinkTask(link)
                    )
                )
            )
        }
    }

    private suspend fun updateYears(vppId: VppId.ActiveVppId): Map<Year, List<Interval>> {
        return gradeRepository
            .downloadYears(vppId)
            .onEach { (year, intervals) ->
                gradeRepository.upsertYear(year)
                intervals.forEach { gradeRepository.upsertInterval(year, it) }
            }
    }
}