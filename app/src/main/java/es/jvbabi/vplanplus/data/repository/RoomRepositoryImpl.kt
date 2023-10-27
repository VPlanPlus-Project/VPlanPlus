package es.jvbabi.vplanplus.data.repository

import es.jvbabi.vplanplus.data.source.RoomDao
import es.jvbabi.vplanplus.domain.model.Room
import es.jvbabi.vplanplus.domain.model.School
import es.jvbabi.vplanplus.domain.repository.RoomRepository

class RoomRepositoryImpl(
    private val roomDao: RoomDao
) : RoomRepository {
    override suspend fun getRooms(schoolId: Long): List<Room> {
        return roomDao.getRooms(schoolId)
    }

    override suspend fun getRoomById(roomId: Long): Room {
        return roomDao.getRoomById(roomId)
    }

    override suspend fun createRoom(room: Room) {
        roomDao.insertRoom(room)
    }

    override suspend fun getRoomByName(school: School, name: String, createIfNotExists: Boolean): Room? {
        val room = roomDao.getRoomByName(school.id!!, name)
        if (room == null && createIfNotExists) {
            val id = roomDao.insertRoom(Room(schoolId = school.id, name = name))
            return roomDao.getRoomById(id)
        }
        return room
    }
}