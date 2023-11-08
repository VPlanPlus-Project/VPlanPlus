package es.jvbabi.vplanplus.ui.screens.home

data class Lesson(
    val id: Long,
    val className: String,
    val lessonNumber: Int,
    val subject: String,
    val teacher: List<String>,
    val room: List<String>,
    val subjectChanged: Boolean = false,
    val teacherChanged: Boolean = false,
    val roomChanged: Boolean = false,
    val start: String,
    val end: String,
    val info: String = ""
)
