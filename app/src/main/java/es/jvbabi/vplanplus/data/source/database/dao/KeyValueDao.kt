package es.jvbabi.vplanplus.data.source.database.dao

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
abstract class KeyValueDao {
    @Query("INSERT OR REPLACE INTO keyValue (id, value) VALUES (:key, :value)")
    abstract suspend fun set(key: String, value: String)

    @Query("SELECT value FROM keyValue WHERE id = :key")
    abstract suspend fun get(key: String): String?

    @Query("SELECT value FROM keyValue WHERE id = :key")
    abstract fun getFlow(key: String): Flow<String?>
}