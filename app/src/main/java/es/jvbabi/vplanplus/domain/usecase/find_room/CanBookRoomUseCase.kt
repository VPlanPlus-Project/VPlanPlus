package es.jvbabi.vplanplus.domain.usecase.find_room

import es.jvbabi.vplanplus.data.model.ProfileType
import es.jvbabi.vplanplus.domain.repository.ClassRepository
import es.jvbabi.vplanplus.domain.repository.VppIdRepository
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentProfileUseCase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class CanBookRoomUseCase(
    private val getCurrentProfileUseCase: GetCurrentProfileUseCase,
    private val classRepository: ClassRepository,
    private val vppIdRepository: VppIdRepository
) {
    suspend operator fun invoke() = flow {
        getCurrentProfileUseCase().collect { profile ->
            if (profile == null || profile.type != ProfileType.STUDENT) {
                emit(BookRoomAbility.WRONG_TYPE)
                return@collect
            }
            emit(if (vppIdRepository.getVppIds().first().any {
                it.className == (classRepository.getClassById(profile.referenceId)?.name?:"-1")
            }) BookRoomAbility.CAN_BOOK else BookRoomAbility.NO_VPP_ID)
        }
    }
}

enum class BookRoomAbility {
    CAN_BOOK, WRONG_TYPE, NO_VPP_ID
}