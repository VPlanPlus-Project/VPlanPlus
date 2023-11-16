package es.jvbabi.vplanplus.domain.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey


/**
 * @author Julius Babies
 * Represents a lesson time.
 * @param lessonTimeId The id of the lesson time. (automatically set by the database)
 * @param classLessonTimeRefId The id of the class.
 * @param lessonNumber The number of the lesson at this day.
 * @param start The start time of the lesson. Formatted as "HH:mm".
 * @param end The end time of the lesson. Formatted as "HH:mm".
 */
@Entity(
    tableName = "lesson_time",
    foreignKeys = [
        ForeignKey(
            entity = Classes::class,
            parentColumns = ["classId"],
            childColumns = ["classLessonTimeRefId"],
            onDelete = ForeignKey.CASCADE
        )

    ]
)
data class LessonTime(
    @PrimaryKey(autoGenerate = true) val lessonTimeId: Long = 0,
    val classLessonTimeRefId: Long,
    val lessonNumber: Int,
    val start: String,
    val end: String,
)