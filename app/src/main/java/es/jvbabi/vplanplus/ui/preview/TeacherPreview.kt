package es.jvbabi.vplanplus.ui.preview

import es.jvbabi.vplanplus.domain.model.School
import es.jvbabi.vplanplus.domain.model.Teacher
import java.util.UUID

object TeacherPreview {
    fun teacher(school: School): Teacher {
        return Teacher(
            teacherId = UUID.randomUUID(),
            acronym = acronyms.random(),
            school = school,
        )
    }

    private val acronyms = listOf(
        "ABR", "BAC", "BLA", "BOS", "BRA", "DRE", "EIC", "FIS", "FRA", "GRA", "HAR", "HOF", "HUB", "JON", "KAU", "KLE", "KOH", "KRA", "LIE", "MÜL", "MEI", "NEU", "OTT", "PFE", "RIC", "SCH", "STU", "WAG", "WOL", "ZIM"
    )
}