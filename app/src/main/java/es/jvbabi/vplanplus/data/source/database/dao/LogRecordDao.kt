package es.jvbabi.vplanplus.data.source.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import es.jvbabi.vplanplus.domain.model.LogRecord
import kotlinx.coroutines.flow.Flow

@Dao
abstract class LogRecordDao {
    @Insert
    abstract suspend fun insert(logRecord: LogRecord)

    @Query("SELECT * FROM logRecord ORDER BY timestamp DESC")
    abstract fun getAll(): Flow<List<LogRecord>>
}