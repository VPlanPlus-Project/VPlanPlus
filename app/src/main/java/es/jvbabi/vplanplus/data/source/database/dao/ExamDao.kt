package es.jvbabi.vplanplus.data.source.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import es.jvbabi.vplanplus.data.model.combined.CExam
import es.jvbabi.vplanplus.data.model.exam.DbExam
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.util.UUID

@Dao
abstract class ExamDao {
    @Query("SELECT MIN(id) FROM exams")
    abstract suspend fun getCurrentLocalExamId(): Int

    @Upsert
    abstract suspend fun saveExam(exam: DbExam)

    @Transaction
    @Query("SELECT * FROM exams WHERE id = :id")
    abstract fun getExam(id: Int): Flow<CExam?>

    @Transaction
    @Query("SELECT * FROM exams WHERE (date = :date OR :date IS NULL) AND (group_id = :groupId OR :groupId IS NULL)")
    abstract fun getExams(date: LocalDate?, groupId: Int?): Flow<List<CExam>>

    @Query("INSERT INTO exam_reminders (exam_id, profile_id, days_before) VALUES (:examId, :profileId, :daysBefore)")
    abstract suspend fun insertExamReminder(examId: Int, profileId: UUID, daysBefore: Int)

    @Query("DELETE FROM exam_reminders WHERE exam_id = :examId")
    abstract suspend fun deleteExamReminders(examId: Int)

    @Query("DELETE FROM exams WHERE id = :examId")
    abstract suspend fun deleteExamById(examId: Int)
}