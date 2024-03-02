package es.jvbabi.vplanplus.domain.usecase.settings.profiles

import es.jvbabi.vplanplus.domain.model.Classes
import es.jvbabi.vplanplus.domain.model.VppId
import es.jvbabi.vplanplus.domain.repository.VppIdRepository
import kotlinx.coroutines.flow.first

class GetVppIdByClassUseCase(
    private val vppIdRepository: VppIdRepository
) {
    suspend operator fun invoke(classes: Classes): VppId? {
        return vppIdRepository
            .getVppIds().first()
            .firstOrNull { it.classes?.classId == classes.classId && it.isActive() }
    }
}