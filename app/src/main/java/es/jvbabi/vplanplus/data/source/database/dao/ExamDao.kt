package es.jvbabi.vplanplus.data.source.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import es.jvbabi.vplanplus.data.model.combined.CExam
import es.jvbabi.vplanplus.data.model.exam.DbExam
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ExamDao {
    @Query("SELECT MIN(id) FROM exams")
    abstract suspend fun getCurrentLocalExamId(): Int

    @Upsert
    abstract suspend fun saveExam(exam: DbExam)

    @Query("SELECT * FROM exams WHERE id = :id")
    abstract fun getExam(id: Int): Flow<CExam>
}