package es.jvbabi.vplanplus.domain.usecase.home

import android.util.Log
import es.jvbabi.vplanplus.data.model.ProfileType
import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.domain.model.School
import es.jvbabi.vplanplus.domain.model.VppId
import es.jvbabi.vplanplus.domain.repository.ClassRepository
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.ProfileRepository
import es.jvbabi.vplanplus.domain.repository.VppIdRepository
import es.jvbabi.vplanplus.domain.usecase.Keys
import es.jvbabi.vplanplus.domain.usecase.profile.GetSchoolFromProfileUseCase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import java.util.UUID

class GetCurrentIdentityUseCase(
    private val vppIdRepository: VppIdRepository,
    private val classRepository: ClassRepository,
    private val keyValueRepository: KeyValueRepository,
    private val profileRepository: ProfileRepository,
    private val getSchoolFromProfileUseCase: GetSchoolFromProfileUseCase
) {
    suspend operator fun invoke() = flow {
        keyValueRepository.getFlow(Keys.ACTIVE_PROFILE).collect {
            Log.d("GetCurrentIdentityUseCase", "invoke: $it")
            if (it == null) {
                emit(null)
                return@collect
            }
            val profile = profileRepository.getProfileById(UUID.fromString(it)).first()
            if (profile == null) {
                emit(null)
                return@collect
            }
            var account: VppId? = null
            if (profile.type == ProfileType.STUDENT) {
                val `class` = classRepository.getClassById(profile.referenceId)
                account = vppIdRepository.getVppIds().first().firstOrNull { vppId ->
                    vppId.classes == `class` && vppId.isActive()
                }
            }
            emit(
                Identity(
                    school = getSchoolFromProfileUseCase(profile),
                    profile = profile,
                    vppId = account
                )
            )
        }
    }
}

data class Identity(
    val school: School? = null,
    val profile: Profile? = null,
    val vppId: VppId? = null
)