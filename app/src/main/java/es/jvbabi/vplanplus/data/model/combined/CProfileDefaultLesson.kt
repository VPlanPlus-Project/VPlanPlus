package es.jvbabi.vplanplus.data.model.combined

import androidx.room.Embedded
import androidx.room.Relation
import es.jvbabi.vplanplus.data.model.DbDefaultLesson
import es.jvbabi.vplanplus.data.model.DbProfileDefaultLesson

data class CProfileDefaultLesson(
    @Embedded val profileDefaultLesson: DbProfileDefaultLesson,
    @Relation(
        parentColumn = "vp_id",
        entityColumn = "vp_id",
        entity = DbDefaultLesson::class
    ) val defaultLessons: List<CDefaultLesson>
)
