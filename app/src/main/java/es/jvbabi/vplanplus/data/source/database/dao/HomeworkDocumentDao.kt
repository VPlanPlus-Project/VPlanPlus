package es.jvbabi.vplanplus.data.source.database.dao

import androidx.room.Dao
import androidx.room.Insert
import es.jvbabi.vplanplus.data.model.homework.DbHomeworkDocument

@Dao
abstract class HomeworkDocumentDao {

    @Insert
    abstract fun insertHomeworkDocument(homeworkDocument: DbHomeworkDocument)
}