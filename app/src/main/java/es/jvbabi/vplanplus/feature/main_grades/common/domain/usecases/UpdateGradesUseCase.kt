package es.jvbabi.vplanplus.feature.main_grades.common.domain.usecases

import android.util.Log
import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.model.VppId
import es.jvbabi.vplanplus.domain.repository.ProfileRepository
import es.jvbabi.vplanplus.domain.repository.SchulverwalterTokenResponse
import es.jvbabi.vplanplus.domain.repository.VppIdRepository
import es.jvbabi.vplanplus.feature.main_grades.view.domain.model.Grade
import es.jvbabi.vplanplus.feature.main_grades.view.domain.model.Interval
import es.jvbabi.vplanplus.feature.main_grades.view.domain.model.Year
import es.jvbabi.vplanplus.feature.main_grades.view.domain.repository.GradeRepository
import es.jvbabi.vplanplus.feature.main_grades.view.domain.repository.SchulverwalterResponse
import kotlinx.coroutines.flow.first

class UpdateGradesUseCase(
    private val profileRepository: ProfileRepository,
    private val vppIdRepository: VppIdRepository,
    private val gradeRepository: GradeRepository
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
                            Log.i("SyncGradesUseCase", "${profile.toLogString()}: Token refreshed")
                            vppIdRepository.setSchulverwalterToken(vppId, token!!)
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
                    val years = updateYears(vppId)

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

                    (existing - builtGrades.toSet()).forEach delete@{ gradeRepository.deleteGrade(it) }

                    newGrades.addAll(builtGrades - existing.toSet())
                    isDone = true
                }

                if (newGrades.isNotEmpty()) {
                    Log.i("SyncGradesUseCase", "New grades: ${newGrades.size}")
                }
            }

        unhealthyProfiles.forEach {
            Log.e("SyncGradesUseCase", "Unhealthy profile: $it")
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