package es.jvbabi.vplanplus.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import es.jvbabi.vplanplus.domain.model.Classes
import java.time.LocalDate

@Entity(
    tableName = "lesson",
    foreignKeys = [
        ForeignKey(
            entity = Classes::class,
            parentColumns = ["id"],
            childColumns = ["classId"],
            onDelete = ForeignKey.CASCADE
        ),
    ]
)
data class DbLesson(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val lessonNumber: Int,
    val changedSubject: String?,
    val classId: Long,
    val defaultLessonId: Long?,
    val info: String?,
    val roomIsChanged: Boolean,
    val day: LocalDate,
)
