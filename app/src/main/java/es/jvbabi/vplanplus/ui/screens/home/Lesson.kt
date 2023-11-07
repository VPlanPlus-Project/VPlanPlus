package es.jvbabi.vplanplus.ui.screens.home

data class Lesson(
    val className: String,
    val lessonNumber: Int,
    val subject: String,
    val teacher: String,
    val room: String,
    val subjectChanged: Boolean = false,
    val teacherChanged: Boolean = false,
    val roomChanged: Boolean = false,
    val start: String,
    val end: String,
    val info: String = ""
)
