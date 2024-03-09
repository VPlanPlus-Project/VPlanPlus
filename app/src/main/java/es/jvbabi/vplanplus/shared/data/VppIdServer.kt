package es.jvbabi.vplanplus.shared.data

object VppIdServer {
    private val environment: ServerEnvironment = ServerEnvironment()
    val host = environment.host
    const val API_VERSION = "v2"
}

private open class ServerEnvironment(
    open val host: String = "id.vpp.jvbabi.es"
)

@Suppress("unused")
private class JuliusServerEnvironment : ServerEnvironment(
    "vppid-development.test.jvbabi.es"
)