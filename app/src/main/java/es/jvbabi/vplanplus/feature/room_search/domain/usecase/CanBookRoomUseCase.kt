package es.jvbabi.vplanplus.feature.room_search.domain.usecase

import es.jvbabi.vplanplus.data.model.ProfileType
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentIdentityUseCase
import kotlinx.coroutines.flow.flow

class CanBookRoomUseCase(
    private val getCurrentIdentityUseCase: GetCurrentIdentityUseCase
) {
    suspend operator fun invoke() = flow {
        getCurrentIdentityUseCase().collect { identity ->
            if (identity?.profile?.type != ProfileType.STUDENT) emit(BookRoomAbility.WRONG_TYPE)
            else if (identity.profile.vppId == null) emit(BookRoomAbility.NO_VPP_ID)
            else emit(BookRoomAbility.CAN_BOOK)
        }
    }
}

enum class BookRoomAbility {
    CAN_BOOK, WRONG_TYPE, NO_VPP_ID
}