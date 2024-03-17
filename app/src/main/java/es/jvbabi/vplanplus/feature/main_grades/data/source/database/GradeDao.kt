package es.jvbabi.vplanplus.feature.main_grades.data.source.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import es.jvbabi.vplanplus.feature.main_grades.data.model.DbGrade
import es.jvbabi.vplanplus.feature.main_grades.data.model.combined.CGrade
import kotlinx.coroutines.flow.Flow

@Dao
abstract class GradeDao {

    @Upsert
    abstract fun insert(grade: DbGrade)

    @Transaction
    @Query("SELECT * FROM grade")
    abstract fun getAllGrades(): Flow<List<CGrade>>

    @Transaction
    @Query("SELECT * FROM grade WHERE vppId = :vppId")
    abstract fun getGrades(vppId: Int): Flow<List<CGrade>>

    @Query("DELETE FROM grade")
    abstract fun dropAll()
}