package es.jvbabi.vplanplus.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore
import androidx.room.Index
import es.jvbabi.vplanplus.data.model.DbSchoolEntity
import es.jvbabi.vplanplus.util.DateUtils.atBeginningOfTheWorld
import es.jvbabi.vplanplus.util.DateUtils.atDate
import es.jvbabi.vplanplus.util.DateUtils.atStartOfDay
import es.jvbabi.vplanplus.util.TimeSpan
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
    @ColumnInfo(name = "start") val from: Long,
    @ColumnInfo(name = "end") val to: Long,
) {

    @get:Ignore
    val end: ZonedDateTime
        get() = ZonedDateTime.now().withZoneSameLocal(ZoneId.of("Europe/Berlin")).atStartOfDay().atBeginningOfTheWorld().plusSeconds(this.to)


    @get:Ignore
    val start: ZonedDateTime
        get() = ZonedDateTime.now().withZoneSameLocal(ZoneId.of("Europe/Berlin")).atStartOfDay().atBeginningOfTheWorld().plusSeconds(this.from)

    fun toTimeSpan(withDate: ZonedDateTime? = null): TimeSpan {
        return TimeSpan(run {
            if (withDate != null) start.atDate(withDate)
            else start
        }, run {
            if (withDate != null) end.atDate(withDate)
            else end
        })
    }
}