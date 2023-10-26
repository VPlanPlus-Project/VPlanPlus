package es.jvbabi.vplanplus.domain.repository

import es.jvbabi.vplanplus.domain.model.Room

interface RoomRepository {

    suspend fun getRooms(schoolId: String): List<Room>
    suspend fun getRoomById(roomId: Int): Room
    suspend fun createRoom(room: Room)
    suspend fun getRoomByName(schoolId: String, name: String): Room?
}