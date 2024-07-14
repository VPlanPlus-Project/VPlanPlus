package es.jvbabi.vplanplus.ui.preview

import es.jvbabi.vplanplus.domain.model.School
import kotlin.random.Random

object GroupPreview {
    fun generateGroup(school: School? = null): es.jvbabi.vplanplus.domain.model.Group {
        return es.jvbabi.vplanplus.domain.model.Group(
            groupId = Random.nextInt(),
            name = classNames.random(),
            school = school ?: SchoolPreview.generateRandomSchools(1).first(),
            isClass = true
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