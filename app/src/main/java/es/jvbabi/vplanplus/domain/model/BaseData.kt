package es.jvbabi.vplanplus.domain.model


/**
 * This class represents the base data of a school.
 * @param classNames The list of class names.
 * @param teacherShorts The list of teacher shorts, null if school doesn't allow getting them.
 * @param roomNames The list of room names, null if school doesn't allow getting them.
 * @param schoolName The name of the school.
 * @param daysPerWeek The number of days per week.
 * @param holidays The list of holidays.
 * @param lessonTimes The map of lesson times, with the class name as key and the lesson number as key of the inner map.
 */
data class XmlBaseData(
    val classNames: List<String>,
    val teacherShorts: List<String>?,
    val roomNames: List<String>?,
    val schoolName: String,
    val daysPerWeek: Int,
    val holidays: List<Holiday>,
    val lessonTimes: Map<String, Map<Int, Pair<String, String>>>
)