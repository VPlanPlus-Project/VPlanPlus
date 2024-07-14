package es.jvbabi.vplanplus.feature.main_grades.view.data.source.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import es.jvbabi.vplanplus.feature.main_grades.view.data.model.DbSubject
import kotlinx.coroutines.flow.Flow

@Dao
abstract class SubjectDao {
    @Upsert
    abstract fun insert(subject: DbSubject)

    @Query("SELECT * FROM grade_subject")
    abstract fun getSubjects(): Flow<List<DbSubject>>
}