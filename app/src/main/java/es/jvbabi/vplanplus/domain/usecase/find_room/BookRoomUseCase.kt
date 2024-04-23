package es.jvbabi.vplanplus.domain.usecase.find_room

import es.jvbabi.vplanplus.data.model.ProfileType
import es.jvbabi.vplanplus.data.repository.BookResult
import es.jvbabi.vplanplus.domain.model.Room
import es.jvbabi.vplanplus.domain.repository.RoomRepository
import es.jvbabi.vplanplus.domain.repository.VppIdRepository
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentIdentityUseCase
import kotlinx.coroutines.flow.first
import java.time.ZonedDateTime

class BookRoomUseCase(
    private val vppIdRepository: VppIdRepository,
    private val roomRepository: RoomRepository,
    private val getCurrentIdentityUseCase: GetCurrentIdentityUseCase
) {

    suspend operator fun invoke(
        room: Room,
        start: ZonedDateTime,
        end: ZonedDateTime
    ): BookResult {
        val identity = getCurrentIdentityUseCase().first() ?: return BookResult.OTHER
        if (identity.profile?.type != ProfileType.STUDENT) return BookResult.OTHER
        val vppId = identity.profile.vppId ?: return BookResult.OTHER
        val result = vppIdRepository.bookRoom(vppId, room, start, end)
        if (result == BookResult.SUCCESS) roomRepository.fetchRoomBookings(identity.school ?: return BookResult.OTHER)
        return result
    }
}