package es.jvbabi.vplanplus.data.source

import androidx.room.Dao
import androidx.room.Query
import es.jvbabi.vplanplus.domain.model.School
import kotlinx.coroutines.flow.Flow

@Dao
abstract class SchoolDao {

    @Query("SELECT * FROM school")
    abstract fun getAll(): Flow<List<School>>
}