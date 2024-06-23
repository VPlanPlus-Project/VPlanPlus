package es.jvbabi.vplanplus.data.repository

import android.util.Log
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.data.model.DbRoomBooking
import es.jvbabi.vplanplus.data.model.DbSchoolEntity
import es.jvbabi.vplanplus.data.model.SchoolEntityType
import es.jvbabi.vplanplus.data.source.database.converter.ZonedDateTimeConverter
import es.jvbabi.vplanplus.data.source.database.dao.RoomBookingDao
import es.jvbabi.vplanplus.data.source.database.dao.SchoolEntityDao
import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.model.Group
import es.jvbabi.vplanplus.domain.model.Room
import es.jvbabi.vplanplus.domain.model.RoomBooking
import es.jvbabi.vplanplus.domain.model.School
import es.jvbabi.vplanplus.domain.repository.GroupRepository
import es.jvbabi.vplanplus.domain.repository.NotificationRepository
import es.jvbabi.vplanplus.domain.repository.ProfileRepository
import es.jvbabi.vplanplus.domain.repository.RoomRepository
import es.jvbabi.vplanplus.domain.repository.StringRepository
import es.jvbabi.vplanplus.domain.repository.VppIdRepository
import es.jvbabi.vplanplus.shared.data.API_VERSION
import es.jvbabi.vplanplus.shared.data.VppIdNetworkRepository
import es.jvbabi.vplanplus.util.DateUtils
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

class RoomRepositoryImpl(
    private val schoolEntityDao: SchoolEntityDao,
    private val roomBookingDao: RoomBookingDao,
    private val vppIdRepository: VppIdRepository,
    private val vppIdNetworkRepository: VppIdNetworkRepository,
    private val groupRepository: GroupRepository,
    private val profileRepository: ProfileRepository,
    private val notificationRepository: NotificationRepository,
    private val stringRepository: StringRepository
) : RoomRepository {
    override suspend fun getRooms(schoolId: Int): List<Room> {
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
                schoolId = room.school.id,
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
            schoolEntityDao.getSchoolEntityByName(school.id, name, SchoolEntityType.ROOM)
        if (room == null && createIfNotExists) {
            val dbRoom = DbSchoolEntity(
                id = UUID.randomUUID(),
                schoolId = school.id,
                name = name,
                type = SchoolEntityType.ROOM
            )
            schoolEntityDao.insertSchoolEntity(dbRoom)
            return schoolEntityDao.getSchoolEntityById(dbRoom.id)!!.toRoomModel()
        }
        return room?.toRoomModel()
    }

    override suspend fun getRoomBookings(date: LocalDate): List<RoomBooking> {
        return roomBookingDao.getAll().mapNotNull {
            if (it.roomBooking.from.toLocalDate().isEqual(date) || it.roomBooking.to.toLocalDate().isEqual(date)) it.toModel()
            else null
        }
    }

    override suspend fun deleteRoomsBySchoolId(schoolId: Int) {
        schoolEntityDao.deleteSchoolEntitiesBySchoolId(schoolId, SchoolEntityType.ROOM)
    }

    override suspend fun insertRoomsByName(schoolId: Int, rooms: List<String>) {
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
        return schoolEntityDao.getSchoolEntities(school.id, SchoolEntityType.ROOM)
            .map { it.toRoomModel() }
    }

    override suspend fun getRoomBookingsByClass(
        group: Group,
        date: LocalDate
    ): List<RoomBooking> {
        return roomBookingDao.getRoomBookingsByGroup(group.groupId).map { it.toModel() }
            .filter { it.from.toLocalDate().isEqual(date) }
    }

    override suspend fun getRoomBookingsByRoom(room: Room, date: LocalDate): List<RoomBooking> {
        return roomBookingDao.getRoomBookingsByRoom(room.roomId).map { it.toModel() }
            .filter { it.from.toLocalDate().isEqual(date) }
    }

    override suspend fun fetchRoomBookings(school: School) {
        vppIdNetworkRepository.authentication = school.buildAccess().buildVppAuthentication()

        val response = vppIdNetworkRepository.doRequest(
            "/api/${API_VERSION}/school/${school.id}/booking",
            HttpMethod.Get,
            null
        )

        if (response.response != HttpStatusCode.OK) {
            Log.e(
                "RoomRepositoryImpl",
                "Error fetching room bookings: ${response.response}\n\n${response.data}\n\n"
            )
            return
        }

        val roomBookings = Gson().fromJson(
            response.data,
            RoomBookingResponse::class.java
        )

        val classes = groupRepository.getGroupsBySchool(school)
        val rooms = getRooms(school.id)

        // cache vpp.IDs if necessary
        roomBookings
            .bookings
            .map { it.bookedBy }
            .forEach { vppId -> vppIdRepository.getVppId(vppId.toLong(), school, false) }

        val existingBookings = roomBookingDao.getAllRoomBookings().map { it.roomBooking.id }

        // insert new bookings
        roomBookingDao.deleteAll()
        roomBookingDao.upsertAll(
            roomBookings.bookings.mapNotNull { bookingResponse ->
                DbRoomBooking(
                    id = bookingResponse.id,
                    roomId = rooms.firstOrNull { room -> bookingResponse.roomName == room.name }?.roomId ?: return@mapNotNull null,
                    bookedBy = bookingResponse.bookedBy,
                    from = ZonedDateTimeConverter().timestampToZonedDateTime(bookingResponse.start),
                    to = ZonedDateTimeConverter().timestampToZonedDateTime(bookingResponse.end),
                    groupId = classes.firstOrNull { it.name == bookingResponse.`class` }?.groupId ?: return@mapNotNull null
                )
            }
        )

        // send notifications for bookings that are relevant to the user
        val profileClasses = profileRepository
            .getProfiles().first()
            .filterIsInstance<ClassProfile>()
            .mapNotNull { profile -> classes.firstOrNull { it.groupId == profile.group.groupId }?.name }

        roomBookings.bookings
            .filter profileClass@{ booking -> booking.`class` in profileClasses }
            .filter notInPast@{ booking -> DateUtils.getDateTimeFromTimestamp(booking.end).isAfter(LocalDateTime.now()) }
            .filter notBookedByCurrentUser@{ booking -> !(vppIdRepository.getVppId(booking.bookedBy.toLong(), school, false)?.isActive() ?: false) }
            .filter isNewInDatabase@{ booking -> !existingBookings.contains(booking.id) }
            .forEach sendNotification@{ booking ->
                notificationRepository.sendNotification(
                    channelId = NotificationRepository.CHANNEL_ID_ROOM_BOOKINGS,
                    id = 5000 + booking.id.toInt(),
                    title = stringRepository.getString(R.string.notification_roomBookingTitle),
                    message = stringRepository.getString(
                        R.string.notification_roomBookingContent,
                        vppIdRepository.getVppId(booking.bookedBy.toLong(), school, false)?.name ?: stringRepository.getString(R.string.unknownVppId),
                        booking.roomName,
                        DateUtils.getDateTimeFromTimestamp(booking.start).toLocalTime().format(
                            DateTimeFormatter.ofPattern("HH:mm")
                        ),
                        DateUtils.getDateTimeFromTimestamp(booking.end).toLocalTime().plusSeconds(1).format(
                            DateTimeFormatter.ofPattern("HH:mm")
                        )
                    ),
                    icon = R.drawable.vpp,
                    pendingIntent = null
                )
            }
    }

    override suspend fun getAll(): List<Room> {
        return schoolEntityDao.getSchoolEntitiesByType(SchoolEntityType.ROOM).map { it.toRoomModel() }
    }

    override suspend fun deleteAllRoomBookings() {
        roomBookingDao.deleteAll()
    }
}

private data class RoomBookingResponse(
    @SerializedName("bookings") val bookings: List<RoomBookingResponseItem>
)

private data class RoomBookingResponseItem(
    @SerializedName("id") val id: Long,
    @SerializedName("school_class") val `class`: String,
    @SerializedName("room_name") val roomName: String,
    @SerializedName("booked_by") val bookedBy: Int,
    @SerializedName("from") val start: Long,
    @SerializedName("to") val end: Long
)