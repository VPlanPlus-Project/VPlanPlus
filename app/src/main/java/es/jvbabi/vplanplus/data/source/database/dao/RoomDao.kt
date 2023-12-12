package es.jvbabi.vplanplus.data.source.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import es.jvbabi.vplanplus.data.model.DbSchoolEntity
import es.jvbabi.vplanplus.data.model.combined.CSchoolEntity
import java.util.UUID

@Dao
abstract class RoomDao {

    @Transaction
    @Query("SELECT * FROM school_entity WHERE schoolId = :schoolId")
    abstract suspend fun getRooms(schoolId: Long): List<CSchoolEntity>

    @Transaction
    @Query("SELECT * FROM school_entity WHERE id = :roomId")
    abstract fun getRoomById(roomId: UUID): CSchoolEntity

    @Insert
    abstract suspend fun insertRoom(room: DbSchoolEntity): Long

    @Transaction
    @Query("SELECT * FROM school_entity WHERE schoolId = :schoolId AND name = :name")
    abstract suspend fun getRoomByName(schoolId: Long, name: String): CSchoolEntity?

    @Query("DELETE FROM school_entity WHERE schoolId = :schoolId")
    abstract suspend fun deleteRoomsBySchoolId(schoolId: Long)

    @Transaction
    @Query("SELECT * FROM school_entity WHERE schoolId = :schoolId")
    abstract suspend fun getRoomsBySchool(schoolId: Long): List<CSchoolEntity>
}