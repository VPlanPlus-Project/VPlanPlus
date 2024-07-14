package es.jvbabi.vplanplus.ui.preview

@RequiresOptIn(
    level = RequiresOptIn.Level.ERROR,
    message = "This is a preview function and should not be used in production code."
)
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.CONSTRUCTOR)
annotation class PreviewFunction