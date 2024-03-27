package es.jvbabi.vplanplus.feature.home_screen_v2.domain.usecase

import es.jvbabi.vplanplus.domain.repository.RoomRepository
import java.time.LocalDate

class GetRoomBookingsForTodayUseCase(
    private val roomRepository: RoomRepository
) {
    suspend operator fun invoke() = roomRepository.getRoomBookings(LocalDate.now())
}