package es.jvbabi.vplanplus.data.source.database.crossover

import androidx.room.Entity
import androidx.room.ForeignKey
import es.jvbabi.vplanplus.data.model.DbProfile
import es.jvbabi.vplanplus.domain.model.DefaultLesson

@Entity(
    tableName = "profile_selected_default_lesson_crossover",
    primaryKeys = ["psdlcProfileId", "psdlcDefaultLessonVpId"],
    foreignKeys = [
        ForeignKey(
            entity = DbProfile::class,
            parentColumns = ["id"],
            childColumns = ["psdlcProfileId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = DefaultLesson::class,
            parentColumns = ["vpId"],
            childColumns = ["psdlcDefaultLessonVpId"],
            onDelete = ForeignKey.NO_ACTION,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class ProfileSelectedDefaultLessonCrossover(
    val psdlcProfileId: Long,
    val psdlcDefaultLessonVpId: Long
)
