package es.jvbabi.vplanplus.domain.model

data class Teacher(
    val teacherId: Long = 0,
    val acronym: String,
    val school: School
)