package es.jvbabi.vplanplus.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore
import androidx.room.Index
import es.jvbabi.vplanplus.data.model.DbSchoolEntity
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.UUID


/**
 * @author Julius Babies
 * Represents a lesson time.
 * @param lessonTimeId The id of the lesson time. (automatically set by the database)
 * @param classLessonTimeRefId The id of the class.
 * @param lessonNumber The number of the lesson at this day.
 * @param from The start time of the lesson in Europe/Berlin timezone
 * @param to The end time of the lesson in Europe/Berlin timezone
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
    @ColumnInfo(name = "start") val from: ZonedDateTime,
    @ColumnInfo(name = "end") val to: ZonedDateTime,
) {
    init {
        require(from.dayOfYear == 1 && from.year == 1970) { "Start date needs to be 1970-01-01" }
        require(to.dayOfYear == 1 && to.year == 1970) { "End date needs to be 1970-01-01" }
    }

    @get:Ignore
    val end: ZonedDateTime
        get() = this.to.withZoneSameInstant(ZoneId.of("Europe/Berlin"))


    @get:Ignore
    val start: ZonedDateTime
        get() = this.from.withZoneSameInstant(ZoneId.of("Europe/Berlin"))
}