package es.jvbabi.vplanplus.ui.screens.home

data class Lesson(
    val subject: String,
    val teacher: String,
    val room: String,
    val progress: Double = 0.0,
    val subjectChanged: Boolean = false,
    val teacherChanged: Boolean = false,
    val roomChanged: Boolean = false,
)
