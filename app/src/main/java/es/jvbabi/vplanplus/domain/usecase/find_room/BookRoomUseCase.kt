package es.jvbabi.vplanplus.domain.usecase.find_room

import es.jvbabi.vplanplus.data.repository.BookResult
import es.jvbabi.vplanplus.domain.model.Room
import es.jvbabi.vplanplus.domain.repository.ClassRepository
import es.jvbabi.vplanplus.domain.repository.RoomRepository
import es.jvbabi.vplanplus.domain.repository.VppIdRepository
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentProfileUseCase
import kotlinx.coroutines.flow.first
import java.time.LocalDateTime

class BookRoomUseCase(
    private val vppIdRepository: VppIdRepository,
    private val classRepository: ClassRepository,
    private val getCurrentProfileUseCase: GetCurrentProfileUseCase,
    private val roomRepository: RoomRepository
) {

    suspend operator fun invoke(
        room: Room,
        start: LocalDateTime,
        end: LocalDateTime
    ): BookResult {
        val profile = getCurrentProfileUseCase().first() ?: return BookResult.OTHER
        val `class` = classRepository.getClassById(profile.referenceId) ?: return BookResult.OTHER
        val vppId = vppIdRepository.getVppIds().first().first {
            it.className == `class`.name
        }
        val result = vppIdRepository.bookRoom(vppId, room, start, end)
        if (result == BookResult.SUCCESS) roomRepository.fetchRoomBookings(`class`.school)
        return result
    }
}