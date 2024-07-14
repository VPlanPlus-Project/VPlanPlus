package es.jvbabi.vplanplus.feature.main_grades.domain.usecase

import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.repository.VppIdRepository
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentProfileUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class IsEnabledUseCase(
    private val getCurrentProfileUseCase: GetCurrentProfileUseCase,
    private val vppIdRepository: VppIdRepository,
) {

    operator fun invoke(): Flow<GradeUseState> = flow {
        getCurrentProfileUseCase().collect profile@{ profile ->
            if (profile == null) {
                emit(GradeUseState.NO_VPP_ID)
                return@profile
            }
            vppIdRepository.getVppIds().collect vppId@{ rawVppIds ->
                val vppIds = rawVppIds.filter { it.isActive() }
                if (vppIds.isEmpty()) {
                    emit(GradeUseState.NO_VPP_ID)
                    return@vppId
                }
                if (profile !is ClassProfile || profile.vppId == null) {
                    emit(GradeUseState.WRONG_PROFILE_SELECTED)
                    return@vppId
                }
                val bsToken = vppIdRepository.getBsToken(profile.vppId)
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