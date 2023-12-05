package es.jvbabi.vplanplus.domain.model

import java.util.UUID

data class Classes(
    val classId: UUID = UUID.randomUUID(),
    val name: String,
    val school: School
)