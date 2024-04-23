package es.jvbabi.vplanplus.feature.main_grades.domain.usecase

import es.jvbabi.vplanplus.data.model.ProfileType
import es.jvbabi.vplanplus.domain.repository.VppIdRepository
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentIdentityUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class IsEnabledUseCase(
    private val getCurrentIdentityUseCase: GetCurrentIdentityUseCase,
    private val vppIdRepository: VppIdRepository,
) {

    operator fun invoke(): Flow<GradeUseState> = flow {
        getCurrentIdentityUseCase().collect identity@{ identity ->
            if (identity == null) {
                emit(GradeUseState.NO_VPP_ID)
                return@identity
            }
            vppIdRepository.getVppIds().collect vppId@{ rawVppIds ->
                val vppIds = rawVppIds.filter { it.isActive() }
                if (vppIds.isEmpty()) {
                    emit(GradeUseState.NO_VPP_ID)
                    return@vppId
                }
                if (identity.profile?.vppId == null || identity.profile.type != ProfileType.STUDENT) {
                    emit(GradeUseState.WRONG_PROFILE_SELECTED)
                    return@vppId
                }
                val bsToken = vppIdRepository.getBsToken(identity.profile.vppId)
                if (bsToken == null) {
                    emit(GradeUseState.NOT_ENABLED)
                } else {
                    emit(GradeUseState.ENABLED)
                }
            }
        }
    }
}

enum class GradeUseState {
    ENABLED,
    NOT_ENABLED,
    WRONG_PROFILE_SELECTED,
    NO_VPP_ID
}