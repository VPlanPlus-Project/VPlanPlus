package es.jvbabi.vplanplus.data.repository

import es.jvbabi.vplanplus.data.model.DbSchoolEntity
import es.jvbabi.vplanplus.data.model.SchoolEntityType
import es.jvbabi.vplanplus.data.source.database.dao.RoomDao
import es.jvbabi.vplanplus.data.source.database.dao.SchoolEntityDao
import es.jvbabi.vplanplus.domain.model.Room
import es.jvbabi.vplanplus.domain.model.School
import es.jvbabi.vplanplus.domain.repository.RoomRepository
import java.util.UUID

class RoomRepositoryImpl(
    private val roomDao: RoomDao,
    private val schoolEntityDao: SchoolEntityDao
) : RoomRepository {
    override suspend fun getRooms(schoolId: Long): List<Room> {
        return schoolEntityDao.getSchoolEntities(schoolId, SchoolEntityType.ROOM).map { it.toRoomModel() }
    }

    override suspend fun getRoomById(roomId: UUID): Room {
        return schoolEntityDao.getSchoolEntityById(roomId).toRoomModel()
    }

    override suspend fun createRoom(room: Room) {
        schoolEntityDao.insertSchoolEntity(
            DbSchoolEntity(
                id = room.roomId,
                name = room.name,
                schoolId = room.school.schoolId,
                type = SchoolEntityType.ROOM
            )
        )
    }

    override suspend fun getRoomByName(school: School, name: String, createIfNotExists: Boolean): Room? {
        if (name == "&amp;nbsp;" || name == "&nbsp;") return null
        val room = roomDao.getRoomByName(school.schoolId, name)
        if (room == null && createIfNotExists) {
            val dbRoom = DbRoom(
                roomId = UUID.randomUUID(),
                schoolRoomRefId = school.schoolId,
                name = name
            )
            roomDao.insertRoom(dbRoom)
            return roomDao.getRoomById(dbRoom.roomId).toModel()
        }
        return room?.toModel()
    }

    override suspend fun deleteRoomsBySchoolId(schoolId: Long) {
        roomDao.deleteRoomsBySchoolId(schoolId)
    }

    override suspend fun insertRoomsByName(schoolId: Long, rooms: List<String>) {
        rooms.forEach {
            createRoom(DbRoom(schoolRoomRefId = schoolId, name = it))
        }
    }

    override suspend fun getRoomsBySchool(school: School): List<Room> {
        return roomDao.getRoomsBySchool(school.schoolId).map { it.toModel() }
    }
}