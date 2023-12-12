package es.jvbabi.vplanplus.domain.repository

import es.jvbabi.vplanplus.domain.model.Room
import es.jvbabi.vplanplus.domain.model.School
import java.util.UUID

interface RoomRepository {

    suspend fun getRooms(schoolId: Long): List<Room>
    fun getRoomById(roomId: UUID): Room
    suspend fun createRoom(room: DbRoom)
    suspend fun getRoomByName(school: School, name: String, createIfNotExists: Boolean = false): Room?
    suspend fun deleteRoomsBySchoolId(schoolId: Long)
    suspend fun insertRoomsByName(schoolId: Long, rooms: List<String>)
    suspend fun getRoomsBySchool(school: School): List<Room>
}