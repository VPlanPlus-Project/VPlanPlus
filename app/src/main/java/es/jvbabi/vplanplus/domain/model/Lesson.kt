package es.jvbabi.vplanplus.domain.model

import androidx.room.Entity
import androidx.room.ForeignKey
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
        ForeignKey(
            entity = Room::class,
            parentColumns = ["id"],
            childColumns = ["roomId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Lesson(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    val lesson: Int,
    val classId: Long,
    val originalSubject: String,
    val originalTeacherId: Long?,
    val roomId: Long?,
    val changedSubject: String?,
    val changedTeacherId: Long?,
    val roomIsChanged: Boolean,
    val info: String,
    val dayTimestamp: Long
)