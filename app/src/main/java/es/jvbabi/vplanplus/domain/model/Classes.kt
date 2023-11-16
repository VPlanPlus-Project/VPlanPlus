package es.jvbabi.vplanplus.domain.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "classes",
    foreignKeys = [
        ForeignKey(
            entity = School::class,
            parentColumns = ["schoolId"],
            childColumns = ["schoolClassRefId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Classes(
    @PrimaryKey(autoGenerate = true) val classId: Long = 0,
    val schoolClassRefId: Long,
    val className: String,
) {
    override fun toString(): String {
        return "Classes(id=$classId, schoolId='$schoolClassRefId', className='$className')"
    }
}