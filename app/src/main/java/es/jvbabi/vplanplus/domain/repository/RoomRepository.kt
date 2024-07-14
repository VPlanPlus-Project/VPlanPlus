package es.jvbabi.vplanplus.domain.repository

import es.jvbabi.vplanplus.domain.model.Group
import es.jvbabi.vplanplus.domain.model.Room
import es.jvbabi.vplanplus.domain.model.RoomBooking
import es.jvbabi.vplanplus.domain.model.School
import java.time.LocalDate

interface RoomRepository {

    suspend fun getRooms(schoolId: Int): List<Room>
    suspend fun getRoomById(roomId: Int): Room?
    suspend fun createRoom(room: Room)
    suspend fun getRoomByName(school: School, name: String, createIfNotExists: Boolean = false): Room?
    suspend fun deleteRoomsBySchoolId(schoolId: Int)
    suspend fun insertRoomsByName(school: School, rooms: List<String>): Boolean
    suspend fun getRoomsBySchool(school: School): List<Room>

    suspend fun getRoomBookingsByClass(group: Group, date: LocalDate = LocalDate.now()): List<RoomBooking>
    suspend fun getRoomBookings(date: LocalDate = LocalDate.now()): List<RoomBooking>
    suspend fun getRoomBookingsByRoom(room: Room, date: LocalDate = LocalDate.now()): List<RoomBooking>
    suspend fun deleteAllRoomBookings()

    suspend fun fetchRoomBookings(school: School)

    suspend fun getAll(): List<Room>
}