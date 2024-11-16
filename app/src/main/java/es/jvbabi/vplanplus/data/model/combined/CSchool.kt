package es.jvbabi.vplanplus.data.model.combined

import androidx.room.Embedded
import es.jvbabi.vplanplus.domain.model.DbSchool
import es.jvbabi.vplanplus.domain.model.School

data class CSchool(
    @Embedded val school: DbSchool
) {
    fun toModel(): School {
        return School(
            id = school.id,
            sp24SchoolId = school.sp24SchoolId,
            name = school.name,
            username = school.username,
            password = school.password,
            daysPerWeek = school.daysPerWeek,
            fullyCompatible = school.fullyCompatible,
            credentialsValid = school.credentialsValid,
            schoolDownloadMode = school.schoolDownloadMode
        )
    }
}