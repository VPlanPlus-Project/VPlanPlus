package es.jvbabi.vplanplus.data.source.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import es.jvbabi.vplanplus.data.model.DbRoom
import es.jvbabi.vplanplus.data.model.combined.CRoom
import java.util.UUID

@Dao
abstract class RoomDao {

    @Transaction
    @Query("SELECT * FROM room WHERE schoolRoomRefId = :schoolId")
    abstract suspend fun getRooms(schoolId: Long): List<CRoom>

    @Transaction
    @Query("SELECT * FROM room WHERE roomId = :roomId")
    abstract fun getRoomById(roomId: UUID): CRoom

    @Insert
    abstract suspend fun insertRoom(room: DbRoom): Long

    @Transaction
    @Query("SELECT * FROM room WHERE schoolRoomRefId = :schoolId AND name = :name")
    abstract suspend fun getRoomByName(schoolId: Long, name: String): CRoom?

    @Query("DELETE FROM room WHERE schoolRoomRefId = :schoolId")
    abstract suspend fun deleteRoomsBySchoolId(schoolId: Long)

    @Transaction
    @Query("SELECT * FROM room WHERE schoolRoomRefId = :schoolId")
    abstract suspend fun getRoomsBySchool(schoolId: Long): List<CRoom>
}