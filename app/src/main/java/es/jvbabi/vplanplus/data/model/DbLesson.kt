package es.jvbabi.vplanplus.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import java.time.ZonedDateTime
import java.util.UUID

@Entity(
    tableName = "lesson",
    foreignKeys = [
        ForeignKey(
            entity = DbGroup::class,
            parentColumns = ["id"],
            childColumns = ["group_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = DbRoomBooking::class,
            parentColumns = ["id"],
            childColumns = ["room_booking_id"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = DbDefaultLesson::class,
            parentColumns = ["id"],
            childColumns = ["default_lesson_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    primaryKeys = ["id"],
    indices = [
        Index(value = ["id"], unique = true),
        Index(value = ["group_id"]),
        Index(value = ["default_lesson_id"]),
        Index(value = ["room_booking_id"])
    ]
)
data class DbLesson(
    @ColumnInfo("id") val id: UUID = UUID.randomUUID(),
    @ColumnInfo("lesson_number") val lessonNumber: Int,
    @ColumnInfo("changed_subject") val changedSubject: String?,
    @ColumnInfo("group_id") val groupId: Int,
    @ColumnInfo("default_lesson_id") val defaultLessonId: UUID?,
    @ColumnInfo("info") val info: String?,
    @ColumnInfo("is_room_changed") val isRoomChanged: Boolean,
    @ColumnInfo("day") val day: ZonedDateTime,
    @ColumnInfo("version") val version: Long,
    @ColumnInfo("room_booking_id") val roomBookingId: Long?
)
