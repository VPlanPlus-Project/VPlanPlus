package es.jvbabi.vplanplus.data.source.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import es.jvbabi.vplanplus.data.model.combined.CHomework
import es.jvbabi.vplanplus.data.model.homework.DbHomework
import es.jvbabi.vplanplus.data.model.homework.DbHomeworkTask
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

    @Query("INSERT OR REPLACE INTO homework_task_done (task_id, profile_id, is_done) VALUES (:taskId, :profileId, :done)")
    abstract suspend fun insertTaskDone(taskId: Int, profileId: UUID, done: Boolean)

    @Upsert
    abstract suspend fun insertTask(task: DbHomeworkTask)

    @Query("DELETE FROM homework WHERE id = :homeworkId")
    abstract suspend fun deleteHomework(homeworkId: Int)

    @Query("DELETE FROM homework_task WHERE id = :taskId")
    abstract suspend fun deleteTask(taskId: Int)

    @Query("UPDATE homework SET is_public = :isPublic WHERE id = :homeworkId")
    abstract suspend fun changePublic(homeworkId: Int, isPublic: Boolean)

    @Query("INSERT OR REPLACE INTO homework_profile_data (homework_id, profile_id, is_hidden) VALUES (:homeworkId, :profileId, :hidden)")
    abstract suspend fun changeHidden(homeworkId: Int, profileId: UUID, hidden: Boolean)

    @Query("UPDATE homework SET until = :newDate WHERE id = :homeworkId")
    abstract suspend fun updateDueDate(homeworkId: Int, newDate: ZonedDateTime)

    @Query("DELETE FROM homework_task WHERE homework_id = :homeworkId")
    abstract suspend fun deleteTasksForHomework(homeworkId: Long)

    @Query("DELETE FROM homework WHERE id > 0")
    abstract suspend fun deleteAllCloud()
}