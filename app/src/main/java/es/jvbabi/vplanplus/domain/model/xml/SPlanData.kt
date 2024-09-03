package es.jvbabi.vplanplus.domain.model.xml

import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root
import org.simpleframework.xml.core.Persister

class SPlanData(val xml: String) {
    val sPlan: SPlan
    init {
        val serializer = Persister()
        val xml = xml.reader()
        sPlan = serializer.read(SPlan::class.java, xml, false)
    }
}

@Root(name = "splan", strict = false)
class SPlan {
    @field:ElementList(name = "Klassen", entry = "Kl") var classes: List<SPlanClass>? = null
}

@Root(name = "Kl", strict = false)
class SPlanClass {
    @field:Element(name = "Kurz") var schoolClass: String = ""
    @field:ElementList(name = "Pl", entry = "Std") var lessons: List<SPlanLesson>? = null
}

@Root(name = "Std", strict = false)
class SPlanLesson {
    @field:Element(name = "PlTg") var dayOfWeek: Int = 0
    @field:Element(name = "PlSt") var lessonNumber: Int = 0
    @field:Element(name = "PlFa") var subjectShort: String = ""
    @field:Element(name = "PlLe") var teacherShort: String = ""
    @field:Element(name = "PlRa") var roomShort: String = ""
}