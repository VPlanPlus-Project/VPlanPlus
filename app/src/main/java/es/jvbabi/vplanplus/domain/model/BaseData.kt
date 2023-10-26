package es.jvbabi.vplanplus.domain.model

import es.jvbabi.vplanplus.domain.model.xml.BaseDataParserStudents
import es.jvbabi.vplanplus.domain.model.xml.WeekData

data class BaseData(
    val students: BaseDataParserStudents,
    val weekData: WeekData
)