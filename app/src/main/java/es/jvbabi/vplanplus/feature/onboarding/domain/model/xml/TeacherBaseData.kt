package es.jvbabi.vplanplus.feature.onboarding.domain.model.xml

import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root

class TeacherBaseData(val xml: String) {
    val teacherShorts = mutableListOf<String>()

    init {
        val serializer = org.simpleframework.xml.core.Persister()
        val reader = xml.reader()
        val rootObject: LehrerSplan = serializer.read(LehrerSplan::class.java, reader, false)
        teacherShorts.addAll(rootObject.list.map { it.short!! })

    }
}

@Root(name = "splan", strict = false)
private class LehrerSplan {
    @field:ElementList(name = "Lehrer", entry = "Le") var list = mutableListOf<Lehrer>()
}

@Root(name = "Le", strict = false)
private class Lehrer {
    @field:Element(name = "Kurz") var short: String? = null
}