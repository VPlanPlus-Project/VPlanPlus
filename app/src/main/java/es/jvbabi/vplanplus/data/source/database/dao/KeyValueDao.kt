package es.jvbabi.vplanplus.data.source.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import es.jvbabi.vplanplus.domain.model.KeyValue
import kotlinx.coroutines.flow.Flow

@Dao
abstract class KeyValueDao {
    @Upsert
    abstract suspend fun set(keyValue: KeyValue)

    @Query("SELECT value FROM key_value WHERE id = :key")
    abstract suspend fun get(key: String): String?

    @Query("SELECT value FROM key_value WHERE id = :key")
    abstract fun getFlow(key: String): Flow<String?>

    @Query("DELETE FROM key_value WHERE id = :key")
    abstract suspend fun delete(key: String)

    @Query("SELECT value FROM key_value WHERE id = :key")
    abstract fun getOnMainThread(key: String): String?
}