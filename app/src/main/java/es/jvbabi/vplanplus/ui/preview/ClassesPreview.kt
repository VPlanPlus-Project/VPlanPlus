package es.jvbabi.vplanplus.ui.preview

object ClassesPreview {
    fun generateClass(school: es.jvbabi.vplanplus.domain.model.School? = null): es.jvbabi.vplanplus.domain.model.Classes {
        return es.jvbabi.vplanplus.domain.model.Classes(
            classId = java.util.UUID.randomUUID(),
            name = classNames.random(),
            school = school ?: School.generateRandomSchools(1).first(),
        )
    }

    val classNames = listOf(
        "5a",
        "5b",
        "5c",
        "6a",
        "6b",
        "6c",
        "7a",
        "7b",
        "7c",
        "8a",
        "8b",
        "8c",
        "9a",
        "9b",
        "9c",
        "10a",
        "10b",
        "10c",
        "JG11",
        "JG12",
    )
}