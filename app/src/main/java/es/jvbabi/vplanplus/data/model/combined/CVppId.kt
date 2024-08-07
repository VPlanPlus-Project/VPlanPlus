package es.jvbabi.vplanplus.data.model.combined

import androidx.room.Embedded
import androidx.room.Relation
import es.jvbabi.vplanplus.data.model.DbGroup
import es.jvbabi.vplanplus.data.model.DbVppId
import es.jvbabi.vplanplus.data.model.DbVppIdToken
import es.jvbabi.vplanplus.domain.model.DbSchool
import es.jvbabi.vplanplus.domain.model.State
import es.jvbabi.vplanplus.domain.model.VppId
import java.time.ZoneId
import java.time.ZonedDateTime

data class CVppId(
    @Embedded val vppId: DbVppId,
    @Relation(
        parentColumn = "school_id",
        entityColumn = "id",
        entity = DbSchool::class
    ) val school: CSchool,
    @Relation(
        parentColumn = "group_id",
        entityColumn = "id",
        entity = DbGroup::class
    ) val group: CGroup?,
    @Relation(
        parentColumn = "id",
        entityColumn = "vpp_id",
        entity = DbVppIdToken::class
    ) val tokens: List<DbVppIdToken> = emptyList()
) {
    fun toModel(): VppId {
        if (vppId.state == State.ACTIVE) {
            return VppId.ActiveVppId(
                id = vppId.id,
                name = vppId.name,
                schoolId = vppId.schoolId,
                school = school.toModel(),
                groupName = vppId.groupName,
                group = group?.toModel(),
                state = vppId.state,
                email = vppId.email,
                cachedAt = vppId.cachedAt ?: ZonedDateTime.of(1970, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC")),
                vppIdToken = tokens.first().accessToken,
                schulverwalterToken = tokens.first().bsToken ?: ""
            )
        }
        return VppId(
            id = vppId.id,
            name = vppId.name,
            schoolId = vppId.schoolId,
            school = school.toModel(),
            groupName = vppId.groupName,
            group = group?.toModel(),
            state = vppId.state,
            email = vppId.email,
            cachedAt = vppId.cachedAt ?: ZonedDateTime.of(1970, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC"))
        )
    }
}