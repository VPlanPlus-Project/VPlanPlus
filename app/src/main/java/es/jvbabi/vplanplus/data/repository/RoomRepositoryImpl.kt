package es.jvbabi.vplanplus.data.repository

import es.jvbabi.vplanplus.data.model.DbSchoolEntity
import es.jvbabi.vplanplus.data.model.SchoolEntityType
import es.jvbabi.vplanplus.data.source.database.dao.RoomBookingDao
import es.jvbabi.vplanplus.data.source.database.dao.SchoolEntityDao
import es.jvbabi.vplanplus.domain.model.Classes
import es.jvbabi.vplanplus.domain.model.Room
import es.jvbabi.vplanplus.domain.model.RoomBooking
import es.jvbabi.vplanplus.domain.model.School
import es.jvbabi.vplanplus.domain.repository.RoomRepository
import java.util.UUID

class RoomRepositoryImpl(
    private val schoolEntityDao: SchoolEntityDao,
    private val roomBookingDao: RoomBookingDao
) : RoomRepository {
    override suspend fun getRooms(schoolId: Long): List<Room> {
        return schoolEntityDao.getSchoolEntities(schoolId, SchoolEntityType.ROOM).map { it.toRoomModel() }
    }

    override suspend fun getRoomById(roomId: UUID): Room? {
        return schoolEntityDao.getSchoolEntityById(roomId)?.toRoomModel()
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
        val room = schoolEntityDao.getSchoolEntityByName(school.schoolId, name, SchoolEntityType.ROOM)
        if (room == null && createIfNotExists) {
            val dbRoom = DbSchoolEntity(
                id = UUID.randomUUID(),
                schoolId = school.schoolId,
                name = name,
                type = SchoolEntityType.ROOM
            )
            schoolEntityDao.insertSchoolEntity(dbRoom)
            return schoolEntityDao.getSchoolEntityById(dbRoom.id)!!.toRoomModel()
        }
        return room?.toRoomModel()
    }

    override suspend fun deleteRoomsBySchoolId(schoolId: Long) {
        schoolEntityDao.deleteSchoolEntitiesBySchoolId(schoolId, SchoolEntityType.ROOM)
    }

    override suspend fun insertRoomsByName(schoolId: Long, rooms: List<String>) {
        schoolEntityDao.insertSchoolEntities(
            rooms.map {
                DbSchoolEntity(
                    id = UUID.randomUUID(),
                    schoolId = schoolId,
                    name = it,
                    type = SchoolEntityType.ROOM
                )
            }
        )
    }

    override suspend fun getRoomsBySchool(school: School): List<Room> {
        return schoolEntityDao.getSchoolEntities(school.schoolId, SchoolEntityType.ROOM).map { it.toRoomModel() }
    }

    override suspend fun getRoomBookingsByClass(classes: Classes): List<RoomBooking> {
        return roomBookingDao.getRoomBookings(classes.classId).map { it.toModel() }
    }
}