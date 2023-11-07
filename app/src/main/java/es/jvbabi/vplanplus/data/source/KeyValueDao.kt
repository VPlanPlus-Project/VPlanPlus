package es.jvbabi.vplanplus.data.source

import androidx.room.Dao
import androidx.room.Query

@Dao
abstract class KeyValueDao {
    @Query("INSERT OR REPLACE INTO keyValue (id, value) VALUES (:key, :value)")
    abstract suspend fun set(key: String, value: String)

    @Query("SELECT value FROM keyValue WHERE id = :key")
    abstract suspend fun get(key: String): String?
}