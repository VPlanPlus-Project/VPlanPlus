package es.jvbabi.vplanplus.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import es.jvbabi.vplanplus.domain.model.School

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
data class DbClass(
    @PrimaryKey(autoGenerate = true) val classId: Long = 0,
    val schoolClassRefId: Long,
    val className: String,
) {
    override fun toString(): String {
        return "Classes(id=$classId, schoolId='$schoolClassRefId', className='$className')"
    }
}