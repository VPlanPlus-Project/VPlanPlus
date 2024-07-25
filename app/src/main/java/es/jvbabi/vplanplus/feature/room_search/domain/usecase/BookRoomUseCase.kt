package es.jvbabi.vplanplus.feature.room_search.domain.usecase

import es.jvbabi.vplanplus.data.repository.BookResult
import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.model.Room
import es.jvbabi.vplanplus.domain.repository.RoomRepository
import es.jvbabi.vplanplus.domain.repository.VppIdRepository
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentProfileUseCase
import kotlinx.coroutines.flow.first
import java.time.ZonedDateTime

class BookRoomUseCase(
    private val vppIdRepository: VppIdRepository,
    private val roomRepository: RoomRepository,
    private val getCurrentProfileUseCase: GetCurrentProfileUseCase
) {

    suspend operator fun invoke(
        room: Room,
        start: ZonedDateTime,
        end: ZonedDateTime
    ): BookResult {
        val profile = getCurrentProfileUseCase().first() ?: return BookResult.OTHER
        if (profile !is ClassProfile) return BookResult.OTHER
        val vppId = profile.vppId ?: return BookResult.OTHER
        val result = vppIdRepository.bookRoom(vppId, room, start, end)
        if (result == BookResult.SUCCESS) roomRepository.fetchRoomBookings(profile.getSchool())
        return result
    }
}