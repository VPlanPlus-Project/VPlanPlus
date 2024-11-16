package es.jvbabi.vplanplus.data.repository

import android.util.Log
import com.google.gson.annotations.SerializedName
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.data.model.DbRoom
import es.jvbabi.vplanplus.data.model.DbRoomBooking
import es.jvbabi.vplanplus.data.source.database.converter.ZonedDateTimeConverter
import es.jvbabi.vplanplus.data.source.database.dao.RoomBookingDao
import es.jvbabi.vplanplus.data.source.database.dao.RoomDao
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

class RoomRepositoryImpl(
    private val roomDao: RoomDao,
    private val roomBookingDao: RoomBookingDao,
    private val vppIdRepository: VppIdRepository,
    private val vppIdNetworkRepository: VppIdNetworkRepository,
    private val groupRepository: GroupRepository,
    private val profileRepository: ProfileRepository,
    private val notificationRepository: NotificationRepository,
    private val stringRepository: StringRepository
) : RoomRepository {
    override suspend fun getRooms(schoolId: Int): List<Room> {
        return roomDao.getRoomsBySchoolId(schoolId).first().map { it.toModel() }
    }

    override suspend fun getRoomById(roomId: Int): Room? {
        return roomDao.getRoomById(roomId)?.toModel()
    }

    override suspend fun createRoom(room: Room) {
        roomDao.insertRoom(
            DbRoom(
                id = room.roomId,
                name = room.name,
                schoolId = room.school.id,
            )
        )
    }

    override suspend fun getRoomByName(
        school: School,
        name: String,
        createIfNotExists: Boolean
    ): Room? {
        if (name == "&amp;nbsp;" || name == "&nbsp;") return null
        val getRoom: suspend () -> Room? = { roomDao.getAllRooms().first().firstOrNull { it.school.school.id == school.id && it.room.name == name }?.toModel() }
        val room = getRoom()
        if (room == null && createIfNotExists) {
            if (!insertRoomsByName(school, listOf(name))) return null
            return getRoom()
        }
        return room
    }

    override suspend fun getRoomBookings(date: LocalDate): List<RoomBooking> {
        return roomBookingDao.getAll().mapNotNull {
            if (it.roomBooking.from.toLocalDate().isEqual(date) || it.roomBooking.to.toLocalDate().isEqual(date)) it.toModel()
            else null
        }
    }

    override suspend fun deleteRoomsBySchoolId(schoolId: Int) {
        roomDao.deleteRoomsBySchoolId(schoolId)
    }

    override suspend fun insertRoomsByName(school: School, rooms: List<String>): Boolean {
        rooms.forEach { room ->
            vppIdNetworkRepository.authentication = school.buildAccess().buildVppAuthentication()
            val response = vppIdNetworkRepository.doRequest(
                path = "/api/${API_VERSION}/school/${school.id}/room/by-name/$room",
                requestMethod = HttpMethod.Get,
                queries = mapOf("allow_creation" to "true")
            )
            if (response.response != HttpStatusCode.OK || response.data == null) return false
            val data = ResponseDataWrapper.fromJson<RoomLookupResponse>(response.data)!!
            val dbRoom = DbRoom(
                id = data.id,
                schoolId = school.id,
                name = data.name,
            )
            roomDao.insertRoom(dbRoom)
        }
        return true
    }

    override suspend fun getRoomsBySchool(school: School): List<Room> {
        return roomDao.getRoomsBySchoolId(school.id).first().map { it.toModel() }
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
            "/api/${API_VERSION}/school/${school.id}/room/booking",
            HttpMethod.Get,
            null
        )

        if (response.response != HttpStatusCode.OK || response.data == null) {
            Log.e(
                "RoomRepositoryImpl",
                "Error fetching room bookings: ${response.response}\n\n${response.data}\n\n"
            )
            return
        }

        val roomBookings = ResponseDataWrapper.fromJson<List<RoomBookingResponseItem>>(response.data)!!

        val classes = groupRepository.getGroupsBySchool(school)

        // cache vpp.IDs if necessary
        roomBookings
            .map { it.bookedBy }
            .forEach { vppId -> vppIdRepository.getVppId(vppId.toLong(), school, false) }

        val existingBookings = roomBookingDao.getAllRoomBookings().map { it.roomBooking.id }

        // insert new bookings
        roomBookingDao.deleteAll()
        roomBookingDao.upsertAll(
            roomBookings.mapNotNull { bookingResponse ->
                DbRoomBooking(
                    id = bookingResponse.id,
                    roomId = getRoomById(bookingResponse.roomId)?.roomId ?: return@mapNotNull null,
                    bookedBy = bookingResponse.bookedBy,
                    from = ZonedDateTimeConverter().timestampToZonedDateTime(bookingResponse.start),
                    to = ZonedDateTimeConverter().timestampToZonedDateTime(bookingResponse.end),
                )
            }
        )

        // send notifications for bookings that are relevant to the user
        val profileClasses = profileRepository
            .getProfiles().first()
            .filterIsInstance<ClassProfile>()
            .mapNotNull { profile -> classes.firstOrNull { it.groupId == profile.group.groupId }?.name }

        roomBookings
            .filter profileClass@{ booking -> vppIdRepository.getVppId(booking.bookedBy)?.group?.name in profileClasses }
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
                        getRoomById(booking.roomId)?.name ?: "unknown",
                        DateUtils.getDateTimeFromTimestamp(booking.start).toLocalTime().format(
                            DateTimeFormatter.ofPattern("HH:mm")
                        ),
                        DateUtils.getDateTimeFromTimestamp(booking.end).toLocalTime().plusSeconds(1).format(
                            DateTimeFormatter.ofPattern("HH:mm")
                        )
                    ),
                    icon = R.drawable.vpp,
                )
            }
    }

    override suspend fun getAll(): List<Room> {
        return roomDao.getAllRooms().first().map { it.toModel() }
    }

    override suspend fun deleteAllRoomBookings() {
        roomBookingDao.deleteAll()
    }
}

private data class RoomBookingResponseItem(
    @SerializedName("id") val id: Long,
    @SerializedName("room_id") val roomId: Int,
    @SerializedName("booked_by") val bookedBy: Int,
    @SerializedName("start") val start: Long,
    @SerializedName("end") val end: Long
)

private data class RoomLookupResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String
)