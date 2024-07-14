package es.jvbabi.vplanplus.feature.main_grades.common.domain.usecases

import android.util.Log
import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.model.VppId
import es.jvbabi.vplanplus.domain.repository.ProfileRepository
import es.jvbabi.vplanplus.domain.repository.SchulverwalterTokenResponse
import es.jvbabi.vplanplus.domain.repository.VppIdRepository
import es.jvbabi.vplanplus.feature.main_grades.view.domain.model.Grade
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

                val refreshTokenAndUpdateDb: suspend () -> VppId.ActiveVppId? = {
                    val (response, token) = vppIdRepository.requestCurrentSchulverwalterToken(vppId)
                    when (response) {
                        SchulverwalterTokenResponse.NO_TOKENS -> {
                            unhealthyProfiles.add(profile)
                            null
                        }
                        SchulverwalterTokenResponse.NETWORK_ERROR -> null
                        SchulverwalterTokenResponse.SUCCESS -> {
                            vppIdRepository.setSchulverwalterToken(vppId, token!!)
                            vppIdRepository.getVppId(vppId.id.toLong(), vppId.school!!, false) as VppId.ActiveVppId
                        }
                    }
                }

                val newGrades = mutableListOf<Grade>()

                repeat(2) updateGrades@{
                    if (isDone) return@updateGrades
                    when (vppIdRepository.testSchulverwalterToken(vppId.schulverwalterToken ?: "")) {
                        false -> vppId = refreshTokenAndUpdateDb() ?: return@updateGrades
                        null -> return@forEachProfile
                        else -> Unit
                    }
                    updateYears(vppId)
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
                    rawGrades.forEach { gradeRepository.upsertGrade(it) }

                    (existing - rawGrades).forEach delete@{ gradeRepository.deleteGrade(it) }

                    newGrades.addAll(rawGrades - existing.toSet())
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

    private suspend fun updateYears(vppId: VppId.ActiveVppId) {
        val years = gradeRepository.downloadYears(vppId)
        years.forEach { year -> gradeRepository.upsertYear(year) }
    }
}