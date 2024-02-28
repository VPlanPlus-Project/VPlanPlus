package es.jvbabi.vplanplus.data.source.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import es.jvbabi.vplanplus.data.model.DbHomework
import es.jvbabi.vplanplus.data.model.DbHomeworkTask
import es.jvbabi.vplanplus.data.model.combined.CHomework
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
abstract class HomeworkDao {

    @Query("SELECT * FROM homework")
    @Transaction
    abstract fun getAll(): Flow<List<CHomework>>

    @Query("SELECT * FROM homework WHERE classes = :classId")
    @Transaction
    abstract fun getByClassId(classId: UUID): Flow<List<CHomework>>

    @Query("SELECT * FROM homework WHERE id = :homeworkId")
    @Transaction
    abstract fun getById(homeworkId: Int): Flow<CHomework?>

    @Query("SELECT * FROM homework_task WHERE id = :taskId")
    abstract fun getHomeworkTaskById(taskId: Int): Flow<DbHomeworkTask>

    @Upsert
    abstract suspend fun insert(homework: DbHomework)

    @Upsert
    abstract suspend fun insertTask(task: DbHomeworkTask)

    @Query("DELETE FROM homework WHERE id = :homeworkId")
    abstract suspend fun deleteHomework(homeworkId: Long)

    @Query("DELETE FROM homework_task WHERE id = :taskId")
    abstract suspend fun deleteTask(taskId: Long)

    @Query("UPDATE homework SET isPublic = :isPublic WHERE id = :homeworkId")
    abstract suspend fun changeVisibility(homeworkId: Long, isPublic: Boolean)
}