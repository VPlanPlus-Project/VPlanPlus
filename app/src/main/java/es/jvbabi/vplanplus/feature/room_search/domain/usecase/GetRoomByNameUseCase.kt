package es.jvbabi.vplanplus.feature.room_search.domain.usecase

import es.jvbabi.vplanplus.domain.model.Room
import es.jvbabi.vplanplus.domain.model.School
import es.jvbabi.vplanplus.domain.repository.RoomRepository

class GetRoomByNameUseCase(
    private val roomRepository: RoomRepository
) {
    suspend operator fun invoke(name: String, school: School): Room {
        return roomRepository.getRoomByName(school, name, false)!!
    }
}