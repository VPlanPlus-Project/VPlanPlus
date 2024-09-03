package es.jvbabi.vplanplus.domain.model.xml

import es.jvbabi.vplanplus.util.sanitizeXml
import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root
import org.simpleframework.xml.core.Persister

class WPlanSPlanData(xml: String) {
    val sPlan: WPlanSPlan
    init {
        val serializer = Persister()
        val reader = sanitizeXml(xml).reader()
        sPlan = serializer.read(WPlanSPlan::class.java, reader, false)
    }
}

@Root(name = "splan", strict = false)
class WPlanSPlan {
    @field:ElementList(name = "Schulwochen", entry = "Sw") var schoolWeeks: List<WPlanSchoolWeek>? = null
    @field:ElementList(name = "Klassen", entry = "Kl") var classes: List<WPlanClass>? = null
}

@Root(name = "Sw", strict = false)
class WPlanSchoolWeek {
    @field:Attribute(name = "SwDatumVon") var dateFrom: String = ""
    @field:Attribute(name = "SwDatumBis") var dateTo: String = ""
    @field:Attribute(name = "SwKw") var weekNumber: Int = 0
    @field:Attribute(name = "SwWo") var weekType: String = "" // why tf is this called like that
}

@Root(name = "Kl", strict = false)
class WPlanClass {
    @field:Element(name = "Kurz") var schoolClass: String = ""
    @field:ElementList(name = "Pl", entry = "Std") var lessons: List<WPlanSPlanLesson>? = null
}

@Root(name = "Std", strict = false)
class WPlanSPlanLesson {
    @field:Element(name = "PlTg") var dayOfWeek: Int = 0
    @field:Element(name = "PlSt") var lessonNumber: Int = 0
    @field:Element(name = "PlFa") var subjectShort: String = ""
    @field:Element(name = "PlLe") var teacherShort: String = ""
    @field:Element(name = "PlRa") var roomShort: String = ""
    @field:Element(name = "PlWo", required = false) var weekType: String? = null
}