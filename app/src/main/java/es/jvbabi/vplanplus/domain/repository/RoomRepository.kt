package es.jvbabi.vplanplus.domain.repository

import es.jvbabi.vplanplus.domain.model.Classes
import es.jvbabi.vplanplus.domain.model.Room
import es.jvbabi.vplanplus.domain.model.RoomBooking
import es.jvbabi.vplanplus.domain.model.School
import java.time.LocalDate
import java.util.UUID

interface RoomRepository {

    suspend fun getRooms(schoolId: Long): List<Room>
    suspend fun getRoomById(roomId: UUID): Room?
    suspend fun createRoom(room: Room)
    suspend fun getRoomByName(school: School, name: String, createIfNotExists: Boolean = false): Room?
    suspend fun deleteRoomsBySchoolId(schoolId: Long)
    suspend fun insertRoomsByName(schoolId: Long, rooms: List<String>)
    suspend fun getRoomsBySchool(school: School): List<Room>

    suspend fun getRoomBookingsByClass(classes: Classes, date: LocalDate = LocalDate.now()): List<RoomBooking>
    suspend fun getRoomBookings(date: LocalDate = LocalDate.now()): List<RoomBooking>
    suspend fun getRoomBookingsByRoom(room: Room, date: LocalDate = LocalDate.now()): List<RoomBooking>
    suspend fun deleteAllRoomBookings()

    suspend fun fetchRoomBookings(school: School)
}