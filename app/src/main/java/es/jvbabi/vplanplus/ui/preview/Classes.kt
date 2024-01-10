package es.jvbabi.vplanplus.ui.preview

object Classes {
    fun generateClass(school: es.jvbabi.vplanplus.domain.model.School? = null): es.jvbabi.vplanplus.domain.model.Classes {
        return es.jvbabi.vplanplus.domain.model.Classes(
            classId = java.util.UUID.randomUUID(),
            name = "7c",
            school = school ?: School.generateRandomSchools(1).first(),
        )
    }
}