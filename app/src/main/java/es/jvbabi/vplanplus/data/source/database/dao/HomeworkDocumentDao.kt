package es.jvbabi.vplanplus.data.source.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import es.jvbabi.vplanplus.data.model.homework.DbHomeworkDocument
import kotlinx.coroutines.flow.Flow

@Dao
abstract class HomeworkDocumentDao {

    @Insert
    abstract fun insertHomeworkDocument(homeworkDocument: DbHomeworkDocument)

    @Query("SELECT * FROM homework_document")
    abstract fun getAllHomeworkDocuments(): Flow<List<DbHomeworkDocument>>

    @Query("SELECT * FROM homework_document WHERE id = :id")
    abstract fun getHomeworkDocumentById(id: Int): DbHomeworkDocument?

    @Query("SELECT * FROM homework_document WHERE homework_id = :homeworkId")
    abstract fun getHomeworkDocumentsByHomeworkId(homeworkId: Long): Flow<List<DbHomeworkDocument>>

    @Query("DELETE FROM homework_document WHERE id = :id")
    abstract fun deleteHomeworkDocumentById(id: Int)
}