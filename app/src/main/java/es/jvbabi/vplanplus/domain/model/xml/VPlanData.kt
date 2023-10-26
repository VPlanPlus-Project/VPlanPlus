package es.jvbabi.vplanplus.domain.model.xml

import es.jvbabi.vplanplus.domain.model.DefaultLesson
import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root
import org.simpleframework.xml.Serializer
import org.simpleframework.xml.Text
import org.simpleframework.xml.core.Persister
import java.time.LocalDate

class VPlanData(val xml: String) {
    val holidays = mutableListOf<VPlanDataHoliday>()
    val defaultLessons = mutableListOf<DefaultLesson>()
    init {
        val serializer: Serializer = Persister()
        val modified = xml.replace("<Kurse/>", "")
        val reader = modified.reader()
        val rootObject: WplanVp = serializer.read(WplanVp::class.java, reader, false)
        println(rootObject)
        1+1
    }
}

data class VPlanDataHoliday(val date: LocalDate, val isPublicHoliday: Boolean)

@Root(name = "WplanVp", strict = false)
private class WplanVp {
    @field:Element(name = "Kopf")
    var head: WplanVpHead? = null

    @field:ElementList(name = "FreieTage", entry = "ft") var holidays: List<String>? = null
    @field:ElementList(name = "Klassen") var classes: List<WplanSchoolClass>? = null
}

@Root(name = "Kopf", strict = false)
private class WplanVpHead {
    @field:Element(name = "zeitstempel") var timestampString: String? = null
}

@Root(name = "Klasse")
class WplanSchoolClass {
    @field:Element(name = "Kurz") var schoolClass: String = ""
    @field:ElementList(name = "Pl") var lessons: List<WplanLesson>? = null
}

@Root(name = "Unterricht")
class WplanLesson {
    @field:Element(name = "St") var lesson: Int = 0
    @field:Element(name = "Fa") var subject: WplanSubject = WplanSubject()
    @field:Element(name = "Le") var teacher: WplanTeacher = WplanTeacher()
    @field:Element(name = "Ra") var room: WplanRoom = WplanRoom()
    @field:Element(name = "If", required = false) var info: String = ""
}

@Root(name = "Fa")
class WplanSubject {
    @field:Text
    var subject: String = ""
    @field:Attribute(name = "FaAe", required = false) var subjectChanged: String = ""
}

@Root(name = "Le")
class WplanTeacher {
    @field:Text var teacher: String = ""
    @field:Attribute(name = "LeAe", required = false) var teacherChanged: String = ""
}

@Root(name = "Ra")
class WplanRoom {
    @field:Text var room: String = ""
    @field:Attribute(name = "RaAe", required = false) var roomChanged: String = ""
}