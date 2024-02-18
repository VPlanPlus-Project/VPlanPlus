package es.jvbabi.vplanplus.feature.grades.data.source.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import es.jvbabi.vplanplus.feature.grades.data.model.DbGrade
import es.jvbabi.vplanplus.feature.grades.data.model.combined.CGrade
import kotlinx.coroutines.flow.Flow

@Dao
abstract class GradeDao {

    @Upsert
    abstract fun insert(grade: DbGrade)

    @Query("SELECT * FROM grade")
    abstract fun getAllGrades(): Flow<List<CGrade>>
}