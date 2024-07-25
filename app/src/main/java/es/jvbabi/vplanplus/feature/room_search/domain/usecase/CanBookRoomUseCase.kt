package es.jvbabi.vplanplus.feature.room_search.domain.usecase

import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentProfileUseCase
import kotlinx.coroutines.flow.flow

class CanBookRoomUseCase(
    private val getCurrentProfileUseCase: GetCurrentProfileUseCase
) {
    suspend operator fun invoke() = flow {
        getCurrentProfileUseCase().collect { profile ->
            if (profile !is ClassProfile) emit(BookRoomAbility.WRONG_TYPE)
            else if (profile.vppId == null) emit(BookRoomAbility.NO_VPP_ID)
            else emit(BookRoomAbility.CAN_BOOK)
        }
    }
}

enum class BookRoomAbility {
    CAN_BOOK, WRONG_TYPE, NO_VPP_ID
}