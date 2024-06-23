package es.jvbabi.vplanplus.data.source.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import es.jvbabi.vplanplus.data.model.homework.DbHomework
import es.jvbabi.vplanplus.data.model.homework.DbHomeworkTask
import es.jvbabi.vplanplus.data.model.combined.CHomework
import kotlinx.coroutines.flow.Flow
import java.time.ZonedDateTime
import java.util.UUID

@Dao
abstract class HomeworkDao {

    @Query("SELECT * FROM homework")
    @Transaction
    abstract fun getAll(): Flow<List<CHomework>>

    @Query("SELECT * FROM homework WHERE group_id = :groupId")
    @Transaction
    abstract fun getByGroupId(groupId: Int): Flow<List<CHomework>>

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

    @Query("UPDATE homework SET is_public = :isPublic WHERE id = :homeworkId")
    abstract suspend fun changePublic(homeworkId: Long, isPublic: Boolean)

    @Query("UPDATE homework SET is_hidden = :hidden WHERE id = :homeworkId")
    abstract suspend fun changeHidden(homeworkId: Long, hidden: Boolean)

    @Query("UPDATE homework SET until = :newDate WHERE id = :homeworkId")
    abstract suspend fun updateDueDate(homeworkId: Long, newDate: ZonedDateTime)

    @Query("DELETE FROM homework_task WHERE homework_id = :homeworkId")
    abstract suspend fun deleteTasksForHomework(homeworkId: Long)

    @Query("DELETE FROM homework WHERE id > 0")
    abstract suspend fun deleteAllCloud()
}