package es.jvbabi.vplanplus.domain.model.xml

import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root
import org.simpleframework.xml.Serializer
import org.simpleframework.xml.Text
import org.simpleframework.xml.core.Persister

class WeekData(val xml: String) {
    val weekDataObject: WeekXmlSplan
    init {
        val serializer: Serializer = Persister()
        val reader = xml.reader()
        weekDataObject = serializer.read(WeekXmlSplan::class.java, reader, false)
    }
}

@Root(name = "splan")
class WeekXmlSplan {
    @field:ElementList(name = "Klassen", entry = "Kl") var classes: List<WeekXmlSchoolClass>? = null
}

@Root(name = "Kl")
class WeekXmlSchoolClass {
    @field:Element(name = "Kurz") var schoolClass: String = ""
    @field:ElementList(name = "Stunden", entry = "St") var lessons: List<WeekXmlLesson>? = null
}

@Root(name = "St")
class WeekXmlLesson {
    @field:Attribute(name = "StZeit") var from: String = ""
    @field:Attribute(name = "StZeitBis") var to: String = ""
    @field:Text var lessonNumber: Int = -1
}