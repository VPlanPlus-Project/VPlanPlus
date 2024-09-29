package es.jvbabi.vplanplus.feature.main_grades.view.data.source.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import es.jvbabi.vplanplus.feature.main_grades.view.data.model.DbGrade
import es.jvbabi.vplanplus.feature.main_grades.view.data.model.combined.CGrade
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
abstract class GradeDao {

    @Upsert
    abstract fun upsert(grade: DbGrade)

    @Transaction
    @Query("SELECT * FROM grade")
    abstract fun getAllGrades(): Flow<List<CGrade>>

    @Transaction
    @Query("SELECT * FROM grade WHERE vpp_id = :vppId")
    abstract fun getGradesByUser(vppId: Int): Flow<List<CGrade>>

    @Transaction
    @Query("SELECT * FROM grade WHERE vpp_id = :vppId AND given_at = :givenAt")
    abstract fun getGradesByUserAndGivenAt(vppId: Int, givenAt: LocalDate): Flow<List<CGrade>>

    @Transaction
    @Query("SELECT * FROM grade WHERE id = :id")
    abstract fun getGradeById(id: Long): Flow<CGrade>

    @Query("DELETE FROM grade")
    abstract fun dropAll()

    @Query("DELETE FROM grade WHERE id = :id")
    abstract fun deleteById(id: Long)
}