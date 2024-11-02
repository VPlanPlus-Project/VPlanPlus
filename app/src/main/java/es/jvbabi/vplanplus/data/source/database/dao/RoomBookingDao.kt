package es.jvbabi.vplanplus.data.source.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import es.jvbabi.vplanplus.data.model.DbRoomBooking
import es.jvbabi.vplanplus.data.model.combined.CRoomBooking

@Dao
abstract class RoomBookingDao {

    @Upsert
    abstract suspend fun upsert(roomBooking: DbRoomBooking)

    @Transaction
    @Query("SELECT * FROM room_booking")
    abstract suspend fun getAll(): List<CRoomBooking>

    @Query("DELETE FROM room_booking")
    abstract suspend fun deleteAll()

    @Transaction
    @Query("SELECT * FROM room_booking INNER JOIN vpp_id ON room_booking.booked_by = vpp_id.id WHERE vpp_id.group_id = :classId")
    abstract suspend fun getRoomBookingsByGroup(classId: Int): List<CRoomBooking>

    @Transaction
    @Query("SELECT * FROM room_booking WHERE room_id = :roomId")
    abstract suspend fun getRoomBookingsByRoom(roomId: Int): List<CRoomBooking>

    @Transaction
    open suspend fun upsertAll(roomBookings: List<DbRoomBooking>) {
        roomBookings.forEach { upsert(it) }
    }

    @Query("DELETE FROM room_booking WHERE id = :id")
    abstract suspend fun deleteById(id: Long)

    @Transaction
    @Query("SELECT * FROM room_booking")
    abstract suspend fun getAllRoomBookings(): List<CRoomBooking>
}