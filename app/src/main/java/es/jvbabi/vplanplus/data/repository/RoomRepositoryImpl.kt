package es.jvbabi.vplanplus.data.repository

import es.jvbabi.vplanplus.data.source.RoomDao
import es.jvbabi.vplanplus.domain.model.Room
import es.jvbabi.vplanplus.domain.repository.RoomRepository

class RoomRepositoryImpl(
    private val roomDao: RoomDao
) : RoomRepository {
    override suspend fun getRooms(schoolId: String): List<Room> {
        return roomDao.getRooms(schoolId)
    }

    override suspend fun getRoomById(roomId: Int): Room {
        return roomDao.getRoomById(roomId)
    }

    override suspend fun createRoom(room: Room) {
        roomDao.insertRoom(room)
    }

    override suspend fun getRoomByName(schoolId: String, name: String): Room? {
        return roomDao.getRoomByName(schoolId, name)
    }
}