package es.jvbabi.vplanplus.data.source

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import es.jvbabi.vplanplus.domain.model.Room

@Dao
abstract class RoomDao {

    @Query("SELECT * FROM room WHERE schoolId = :schoolId")
    abstract suspend fun getRooms(schoolId: String): List<Room>

    @Query("SELECT * FROM room WHERE id = :roomId")
    abstract suspend fun getRoomById(roomId: Int): Room

    @Insert
    abstract suspend fun insertRoom(room: Room)

    @Query("SELECT * FROM room WHERE schoolId = :schoolId AND name = :name")
    abstract suspend fun getRoomByName(schoolId: String, name: String): Room?
}