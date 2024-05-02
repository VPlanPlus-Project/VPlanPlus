package es.jvbabi.vplanplus.shared.data

import es.jvbabi.vplanplus.domain.model.Classes
import es.jvbabi.vplanplus.domain.model.Room
import es.jvbabi.vplanplus.domain.model.RoomBooking
import es.jvbabi.vplanplus.domain.model.School
import es.jvbabi.vplanplus.domain.repository.RoomRepository
import java.time.LocalDate
import java.util.UUID

class FakeRoomRepository(
    private val fakeSchoolRepository: FakeSchoolRepository
) : RoomRepository {
    private val rooms = mutableListOf<Room>()

    override suspend fun getRooms(schoolId: Long): List<Room> {
        return rooms.filter { it.school.schoolId == schoolId }
    }

    override suspend fun getRoomById(roomId: UUID): Room? {
        return rooms.firstOrNull { it.roomId == roomId }
    }

    override suspend fun createRoom(room: Room) {
        rooms.add(room)
    }

    override suspend fun getRoomByName(
        school: School,
        name: String,
        createIfNotExists: Boolean
    ): Room? {
        return rooms.firstOrNull { it.school == school && it.name == name }
    }

    override suspend fun deleteRoomsBySchoolId(schoolId: Long) {
        rooms.removeIf { it.school.schoolId == schoolId }
    }

    override suspend fun insertRoomsByName(schoolId: Long, rooms: List<String>) {
        val school = fakeSchoolRepository.getSchoolFromId(schoolId)!!
        rooms.forEach { createRoom(Room(UUID.randomUUID(), it, school)) }
    }

    override suspend fun getRoomsBySchool(school: School): List<Room> {
        return rooms.filter { it.school == school }
    }

    override suspend fun getRoomBookingsByClass(
        classes: Classes,
        date: LocalDate
    ): List<RoomBooking> {
        TODO("Not yet implemented")
    }

    override suspend fun getRoomBookings(date: LocalDate): List<RoomBooking> {
        TODO("Not yet implemented")
    }

    override suspend fun getRoomBookingsByRoom(room: Room, date: LocalDate): List<RoomBooking> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAllRoomBookings() {
        TODO("Not yet implemented")
    }

    override suspend fun fetchRoomBookings(school: School) {
        TODO("Not yet implemented")
    }

    override suspend fun getAll(): List<Room> {
        TODO("Not yet implemented")
    }

    companion object {
        val roomNames = listOf(
            "005a",
            "010a",
            "010b",
            "05b",
            "06a",
            "06b",
            "106a",
            "106b",
            "109a",
            "109b",
            "110a",
            "110b",
            "111a",
            "111b",
            "114a",
            "114b",
            "115a",
            "115b",
            "118a",
            "118b",
            "17a",
            "17b",
            "18a",
            "18b",
            "206a",
            "206b",
            "209a",
            "209b",
            "210a",
            "210b",
            "211a",
            "211b",
            "214a",
            "214b",
            "215a",
            "215b",
            "218a",
            "218b",
            "306a",
            "306b",
            "309a",
            "309b",
            "310a",
            "310b",
            "311a",
            "311b",
            "313a",
            "313b",
            "314a",
            "314b",
            "317a",
            "317b",
            "TH1",
            "TH2",
            "TH3",
        )
    }
}