package es.jvbabi.vplanplus.domain.model.xml

import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root
import org.simpleframework.xml.Text
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
    @field:ElementList(name = "Schulwochen", entry = "Sw") var schoolWeekTypes: List<SPlanSchoolWeekType>? = null
    @field:ElementList(name = "Kalenderwochen", entry = "Kw") var weeks: List<SPlanWeek>? = null
}

@Root(name = "Sw", strict = false)
class SPlanSchoolWeekType {
    @field:Attribute(name = "SwDatum") var type: String = "" // why tf is this called like that???
}

@Root(name = "Kw", strict = false)
class SPlanWeek {
    @field:Attribute(name = "KwNr") var weekNumber: Int = 0
    @field:Attribute(name = "KwDatumVon") var dateFrom: String = ""
    @field:Attribute(name = "KwDatumBis") var dateTo: String = ""
    @field:Attribute(name = "KwWoche") var type: String = ""
}

@Root(name = "Kl", strict = false)
class SPlanClass {
    @field:Element(name = "Kurz") var schoolClass: String = ""
    @field:ElementList(name = "Pl", entry = "Std") var lessons: List<SPlanLesson>? = null
    @field:ElementList(name = "Stunden", entry = "St") var lessonTimes: List<SPlanLessonTime>? = null
}

@Root(name = "St", strict = false)
class SPlanLessonTime {
    @field:Attribute(name = "StZeit") var start: String? = null
    @field:Attribute(name = "StZeitBis") var end: String? = null
    @field:Text(required = false) var lessonNumber: Int? = null
}

@Root(name = "Std", strict = false)
class SPlanLesson {
    @field:Element(name = "PlTg") var dayOfWeek: Int = 0
    @field:Element(name = "PlSt") var lessonNumber: Int = 0
    @field:Element(name = "PlFa") var subjectShort: String = ""
    @field:Element(name = "PlLe") var teacherShort: String = ""
    @field:Element(name = "PlRa") var roomShort: String = ""
    @field:Element(name = "PlWo", required = false) var weekType: String? = null
}