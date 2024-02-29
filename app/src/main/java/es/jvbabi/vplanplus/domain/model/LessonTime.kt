package es.jvbabi.vplanplus.domain.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import es.jvbabi.vplanplus.data.model.DbSchoolEntity
import java.time.ZonedDateTime
import java.util.UUID


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
            entity = DbSchoolEntity::class,
            parentColumns = ["id"],
            childColumns = ["classLessonTimeRefId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    primaryKeys = ["lessonTimeId"],
    indices = [
        Index(value = ["lessonTimeId"], unique = true),
        Index(value = ["classLessonTimeRefId"]),
    ]
)
data class LessonTime(
    val lessonTimeId: UUID = UUID.randomUUID(),
    val classLessonTimeRefId: UUID,
    val lessonNumber: Int,
    val start: ZonedDateTime,
    val end: ZonedDateTime,
) {
    init {
        require(start.dayOfYear == 1 && start.year == 1970) { "Start date needs to be 1970-01-01" }
        require(end.dayOfYear == 1 && end.year == 1970) { "End date needs to be 1970-01-01" }
    }
}