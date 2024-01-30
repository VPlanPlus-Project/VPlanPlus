package es.jvbabi.vplanplus.data.repository

import android.util.Log
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import es.jvbabi.vplanplus.data.model.DbRoomBooking
import es.jvbabi.vplanplus.data.model.DbSchoolEntity
import es.jvbabi.vplanplus.data.model.SchoolEntityType
import es.jvbabi.vplanplus.data.source.database.dao.RoomBookingDao
import es.jvbabi.vplanplus.data.source.database.dao.SchoolEntityDao
import es.jvbabi.vplanplus.domain.model.Classes
import es.jvbabi.vplanplus.domain.model.Room
import es.jvbabi.vplanplus.domain.model.RoomBooking
import es.jvbabi.vplanplus.domain.model.School
import es.jvbabi.vplanplus.domain.repository.ClassRepository
import es.jvbabi.vplanplus.domain.repository.RoomRepository
import es.jvbabi.vplanplus.domain.repository.VppIdRepository
import es.jvbabi.vplanplus.util.DateUtils
import io.ktor.client.request.headers
import io.ktor.client.request.request
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.URLProtocol
import io.ktor.http.encodedPath
import kotlinx.coroutines.flow.first
import java.util.UUID

class RoomRepositoryImpl(
    private val schoolEntityDao: SchoolEntityDao,
    private val roomBookingDao: RoomBookingDao,
    private val vppIdRepository: VppIdRepository,
    private val classRepository: ClassRepository
) : RoomRepository {
    override suspend fun getRooms(schoolId: Long): List<Room> {
        return schoolEntityDao.getSchoolEntities(schoolId, SchoolEntityType.ROOM)
            .map { it.toRoomModel() }
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

    override suspend fun getRoomByName(
        school: School,
        name: String,
        createIfNotExists: Boolean
    ): Room? {
        if (name == "&amp;nbsp;" || name == "&nbsp;") return null
        val room =
            schoolEntityDao.getSchoolEntityByName(school.schoolId, name, SchoolEntityType.ROOM)
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
        return schoolEntityDao.getSchoolEntities(school.schoolId, SchoolEntityType.ROOM)
            .map { it.toRoomModel() }
    }

    override suspend fun getRoomBookingsByClass(classes: Classes): List<RoomBooking> {
        return roomBookingDao.getRoomBookings(classes.classId).map { it.toModel() }
    }

    override suspend fun fetchRoomBookings(school: School) {
        val client = VppIdRepositoryImpl.createClient()
        val vppId = vppIdRepository.getVppIds().first().first { it.schoolId == school.schoolId }
        val token = vppIdRepository.getVppIdToken(vppId) ?: return

        try {
            val response = client.request {
                url {
                    protocol = URLProtocol.HTTPS
                    host = "id.vpp.jvbabi.es"
                    encodedPath = "/api/v1/vpp_id/booking/get_room_bookings"
                    method = HttpMethod.Get
                }
                headers {
                    set("Authorization", token)
                }
            }
            if (response.status != HttpStatusCode.OK) {
                Log.e("RoomRepositoryImpl", "Error fetching room bookings: ${response.status}\n\n${response.bodyAsText()}\n\n")
                return
            }
            val roomBookings = Gson().fromJson(
                response.bodyAsText(),
                RoomBookingResponse::class.java
            )
            val classes = classRepository.getClassesBySchool(school)
            val rooms = getRooms(school.schoolId)
            val vppIds = vppIdRepository.getVppIds().first()
            roomBookingDao.upsertAll(
                roomBookings.bookings.map { bookingResponse ->
                    DbRoomBooking(
                        roomId = rooms.first { room -> bookingResponse.roomName == room.name }.roomId,
                        bookedBy = vppIds.first { it.id == bookingResponse.bookedBy }.id,
                        from = DateUtils.getDateTimeFromTimestamp(bookingResponse.start),
                        to = DateUtils.getDateTimeFromTimestamp(bookingResponse.end),
                        `class` = classes.first { it.name == bookingResponse.`class` }.classId
                    )
                }
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

private data class RoomBookingResponse(
    val bookings: List<RoomBookingResponseItem>
)

private data class RoomBookingResponseItem(
    @SerializedName("room_name") val roomName: String,
    @SerializedName("booked_by") val bookedBy: Int,
    val start: Long,
    val end: Long,
    val `class`: String
)