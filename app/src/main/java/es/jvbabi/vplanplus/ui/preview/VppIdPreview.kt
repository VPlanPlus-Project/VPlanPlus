package es.jvbabi.vplanplus.ui.preview

import es.jvbabi.vplanplus.domain.model.Classes
import es.jvbabi.vplanplus.domain.model.VppId

object VppIdPreview {

    fun generateVppId(c: Classes?): VppId {
        val school = c?.school ?: School.generateRandomSchools(1).first()
        val classes = c ?: ClassesPreview.generateClass(school)
        return VppId(
            id = 1,
            name = "John Doe",
            classes = classes,
            school = school,
            schoolId = school.schoolId,
            className = classes.name,
            email = "john.doe@mail.com"
        )
    }
}