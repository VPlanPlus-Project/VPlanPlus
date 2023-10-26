package es.jvbabi.vplanplus.domain.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey


/**
 * @author Julius Babies
 * Represents a lesson time.
 * @param id The id of the lesson time. (automatically set by the database)
 * @param classId The id of the class.
 * @param lessonNumber The number of the lesson at this day.
 * @param start The start time of the lesson. Formatted as "HH:mm".
 * @param end The end time of the lesson. Formatted as "HH:mm".
 */
@Entity(
    tableName = "lesson_time",
    indices = [
        Index(value = ["id"], unique = true)
    ]
)
data class LessonTime(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    val classId: Int,
    val lessonNumber: Int,
    val start: String,
    val end: String,
)