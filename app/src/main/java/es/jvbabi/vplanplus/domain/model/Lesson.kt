package es.jvbabi.vplanplus.domain.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "lesson",
    foreignKeys = [
        ForeignKey(
            entity = Classes::class,
            parentColumns = ["id"],
            childColumns = ["classId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Teacher::class,
            parentColumns = ["id"],
            childColumns = ["originalTeacherId"],
            onDelete = ForeignKey.CASCADE
        ),
    ],
    indices = [
        Index(value = ["classId"]),
        Index(value = ["originalTeacherId"]),
        Index(value = ["changedTeacherId"]),
    ]
)
data class Lesson(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    val lesson: Int,
    val classId: Long,
    val originalSubject: String,
    val originalTeacherId: Long?,
    val changedSubject: String?,
    val changedTeacherId: Long?,
    val roomIsChanged: Boolean,
    val info: String,
    val dayTimestamp: Long
) {
    @Ignore
    var rooms: List<Room> = listOf()

    fun withRooms(rooms: List<Room>): Lesson {
        this.rooms = rooms
        return this
    }
}