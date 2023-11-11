package es.jvbabi.vplanplus.domain.model

import es.jvbabi.vplanplus.domain.model.xml.BaseDataSchoolWeek


data class XmlBaseData(
    val classNames: List<String>,
    val teacherShorts: List<String>,
    val roomNames: List<String>,
    val schoolName: String,
    val daysPerWeek: Int,
    val holidays: List<Holiday>,
    val weeks: List<BaseDataSchoolWeek>,
    val lessonTimes: Map<String, Map<Int, Pair<String, String>>>
)