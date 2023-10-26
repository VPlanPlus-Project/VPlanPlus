package es.jvbabi.vplanplus.domain.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "lesson",
    indices = [
        Index(value = ["id"], unique = true)
    ]
)
data class Lesson(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    val defaultLessonId: Int?,
    val lesson: Int,
    val classId: Int,
    val changedSubject: String?,
    val changedTeacherId: Int?,
    val roomId: Int?,
    val changedInfo: String,
    val roomIsChanged: Boolean,
    val timestamp: Long
)