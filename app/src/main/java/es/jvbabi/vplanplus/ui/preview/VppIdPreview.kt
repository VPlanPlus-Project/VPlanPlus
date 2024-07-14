package es.jvbabi.vplanplus.ui.preview

import es.jvbabi.vplanplus.domain.model.Group
import es.jvbabi.vplanplus.domain.model.VppId

object VppIdPreview {

    fun generateVppId(group: Group?): VppId {
        val school = group?.school ?: SchoolPreview.generateRandomSchools(1).first()
        val classes = group ?: GroupPreview.generateGroup(school)
        return VppId(
            id = 1,
            name = "John Doe",
            group = classes,
            school = school,
            schoolId = school.id,
            groupName = classes.name,
            email = "john.doe@mail.com"
        )
    }
}