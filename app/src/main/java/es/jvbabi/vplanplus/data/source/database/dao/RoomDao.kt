package es.jvbabi.vplanplus.data.source.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import es.jvbabi.vplanplus.domain.model.Room

@Dao
abstract class RoomDao {

    @Query("SELECT * FROM room WHERE schoolId = :schoolId")
    abstract suspend fun getRooms(schoolId: Long): List<Room>

    @Query("SELECT * FROM room WHERE id = :roomId")
    abstract suspend fun getRoomById(roomId: Long): Room

    @Insert
    abstract suspend fun insertRoom(room: Room): Long

    @Query("SELECT * FROM room WHERE schoolId = :schoolId AND name = :name")
    abstract suspend fun getRoomByName(schoolId: Long, name: String): Room?

    @Query("DELETE FROM room WHERE schoolId = :schoolId")
    abstract suspend fun deleteRoomsBySchoolId(schoolId: Long)

    @Query("SELECT * FROM room WHERE schoolId = :schoolId")
    abstract suspend fun getRoomsBySchool(schoolId: Long): List<Room>
}