package es.jvbabi.vplanplus.domain.repository

import es.jvbabi.vplanplus.domain.model.Room
import es.jvbabi.vplanplus.domain.model.School

interface RoomRepository {

    suspend fun getRooms(schoolId: Long): List<Room>
    suspend fun getRoomById(roomId: Long): Room
    suspend fun createRoom(room: Room)
    suspend fun getRoomByName(school: School, name: String, createIfNotExists: Boolean = false): Room?
    suspend fun deleteRoomsBySchoolId(schoolId: Long)
    suspend fun insertRoomsByName(schoolId: Long, rooms: List<String>)
}