package es.jvbabi.vplanplus.domain.model

import java.util.UUID

data class Teacher(
    val teacherId: UUID = UUID.randomUUID(),
    val acronym: String,
    val school: School
)