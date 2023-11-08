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
    ],
    indices = [
        Index(value = ["classId"]),
    ]
)
data class Lesson(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    val lesson: Int,
    val classId: Long,
    val originalSubject: String,
    val changedSubject: String?,
    val roomIsChanged: Boolean,
    val teacherIsChanged: Boolean,
    val info: String,
    val dayTimestamp: Long
) {
    @Ignore
    var rooms: List<Room> = listOf()

    @Ignore
    var teachers: List<Teacher> = listOf()

    fun withRooms(rooms: List<Room>): Lesson {
        this.rooms = rooms
        return this
    }

    fun withTeachers(teachers: List<Teacher>): Lesson {
        this.teachers = teachers
        return this
    }

    override fun toString(): String {
        return "Lesson(id=$id, lesson=$lesson, classId=$classId, originalSubject='$originalSubject', changedSubject=$changedSubject, roomIsChanged=$roomIsChanged, info='$info', dayTimestamp=$dayTimestamp, rooms=${
            rooms.joinToString(", ") { it.name }
        }, teachers=${
            teachers.joinToString(", ") { it.acronym }
        })"
    }
}