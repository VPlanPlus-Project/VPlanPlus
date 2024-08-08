package es.jvbabi.vplanplus.domain.model.xml

import es.jvbabi.vplanplus.util.sanitizeXml
import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root
import org.simpleframework.xml.Serializer
import org.simpleframework.xml.Text
import org.simpleframework.xml.core.Persister

class VPlanData(val xml: String, val sp24SchoolId: Int) {
    val wPlanDataObject: VpMobilVpXml
    init {
        val serializer: Serializer = Persister()

        // Angry checkpoint: There are things in the XML data that you'd never expect to be there.
        var modified = sanitizeXml(xml).replace("<Kurse/>", "")
        modified = modified.replace("+</Nr>", "</Nr>") // HOW DID THIS + EVEN GET HERE
        while (!modified.startsWith("<")) modified = modified.drop(1)

        val reader = modified.reader()
        wPlanDataObject = serializer.read(VpMobilVpXml::class.java, reader, false)
    }
}


@Root(name = "WplanVp", strict = false)
class VpMobilVpXml {
    @field:Element(name = "Kopf")
    var head: WplanVpXmlHead? = null

    @field:ElementList(name = "Klassen") var classes: List<WplanVpXmlSchoolClass>? = null
    @field:ElementList(name = "ZusatzInfo", required = false, entry = "ZiZeile") var info: List<String?>? = null
}

@Root(name = "Kopf", strict = false)
class WplanVpXmlHead {
    @field:Element(name = "zeitstempel") var timestampString: String? = null
    @field:Element(name = "DatumPlan") var date: String? = null
}

@Root(name = "Kl")
class WplanVpXmlSchoolClass {
    @field:Element(name = "Kurz") var schoolClass: String = ""
    @field:ElementList(name = "Pl") var lessons: List<WplanVpXmlLesson>? = null
    @field:ElementList(name = "Unterricht", entry = "Ue") var defaultLessons: List<WplanVpXmlDefaultLessonWrapper>? = null
}

@Root(name = "Ue")
class WplanVpXmlDefaultLessonWrapper {
    @field:Element(name = "UeNr") var defaultLesson: WplanVpXmlDefaultLesson? = null
}

@Root(name = "UeNr")
class WplanVpXmlDefaultLesson {
    @field:Attribute(name = "UeLe", required = false) var teacherShort: String? = null
    @field:Attribute(name = "UeFa") var subjectShort: String? = null
    @field:Attribute(name = "UeGr", required = false) var courseGroup: String? = null
    @field:Text var lessonId: Int? = null
}

@Root(name = "Std")
class WplanVpXmlLesson {
    @field:Element(name = "St") var lesson: Int = 0
    @field:Element(name = "Nr", required = false) var defaultLessonVpId: Int? = null
    @field:Element(name = "Fa") var subject: WplanVpXmlSubject = WplanVpXmlSubject()
    @field:Element(name = "Le") var teacher: VpMobilVpXmlTeacher = VpMobilVpXmlTeacher()
    @field:Element(name = "Ra") var room: VpMobilVpXmlRoom = VpMobilVpXmlRoom()
    @field:Element(name = "If", required = false) var info: String = ""
}

@Root(name = "Fa")
class WplanVpXmlSubject {
    @field:Text(required = false)
    var subject: String = ""
    @field:Attribute(name = "FaAe", required = false) var subjectChanged: String = ""
}

@Root(name = "Le")
class VpMobilVpXmlTeacher {
    @field:Text(required = false) var teacher: String = ""
}

@Root(name = "Ra")
class VpMobilVpXmlRoom {
    @field:Text(required = false) var room: String = ""
    @field:Attribute(name = "RaAe", required = false) var roomChanged: String = ""
}
