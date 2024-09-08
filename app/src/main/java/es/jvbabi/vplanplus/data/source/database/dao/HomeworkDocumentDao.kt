package es.jvbabi.vplanplus.data.source.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import es.jvbabi.vplanplus.data.model.homework.DbHomeworkDocument
import kotlinx.coroutines.flow.Flow

@Dao
abstract class HomeworkDocumentDao {

    @Upsert
    abstract fun upsertHomeworkDocument(homeworkDocument: DbHomeworkDocument)

    @Query("SELECT * FROM homework_document")
    abstract fun getAllHomeworkDocuments(): Flow<List<DbHomeworkDocument>>

    @Query("SELECT * FROM homework_document WHERE id = :id")
    abstract fun getHomeworkDocumentById(id: Int): DbHomeworkDocument?

    @Query("DELETE FROM homework_document WHERE id = :id")
    abstract fun deleteHomeworkDocumentById(id: Int)

    @Query("UPDATE homework_document SET file_name = :fileName WHERE id = :id")
    abstract fun updateHomeworkDocumentFileName(id: Int, fileName: String)

    @Query("UPDATE homework_document SET is_downloaded = :isDownloaded WHERE id = :id")
    abstract fun updateHomeworkDocumentIsDownloaded(id: Int, isDownloaded: Boolean)

    @Query("UPDATE homework_document SET size = :size WHERE id = :id")
    abstract fun updateHomeworkDocumentSize(id: Int, size: Long)
}