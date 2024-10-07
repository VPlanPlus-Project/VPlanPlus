package es.jvbabi.vplanplus.data.source.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import es.jvbabi.vplanplus.data.model.DbRoom
import es.jvbabi.vplanplus.data.model.combined.CRoom
import kotlinx.coroutines.flow.Flow

@Dao
abstract class RoomDao {
    @Insert
    abstract suspend fun insertRoom(room: DbRoom)

    @Query("SELECT * FROM room WHERE id = :roomId")
    @Transaction
    abstract fun getRoomById(roomId: Int): CRoom?

    @Query("SELECT * FROM room")
    @Transaction
    abstract fun getAllRooms(): Flow<List<CRoom>>

    @Query("SELECT * FROM room WHERE school_id = :schoolId")
    @Transaction
    abstract fun getRoomsBySchoolId(schoolId: Int): Flow<List<CRoom>>

    @Query("DELETE FROM room WHERE school_id = :schoolId")
    abstract fun deleteRoomsBySchoolId(schoolId: Int)
}