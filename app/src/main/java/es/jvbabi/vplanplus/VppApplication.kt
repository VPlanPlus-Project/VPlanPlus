package es.jvbabi.vplanplus

import android.app.Application
import android.content.Context
import androidx.work.Configuration
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import dagger.hilt.android.HiltAndroidApp
import es.jvbabi.vplanplus.domain.repository.CalendarRepository
import es.jvbabi.vplanplus.domain.repository.LogRecordRepository
import es.jvbabi.vplanplus.domain.repository.RoomRepository
import es.jvbabi.vplanplus.domain.repository.TeacherRepository
import es.jvbabi.vplanplus.domain.usecase.ClassUseCases
import es.jvbabi.vplanplus.domain.usecase.KeyValueUseCases
import es.jvbabi.vplanplus.domain.usecase.LessonUseCases
import es.jvbabi.vplanplus.domain.usecase.ProfileUseCases
import es.jvbabi.vplanplus.domain.usecase.SchoolUseCases
import es.jvbabi.vplanplus.domain.usecase.VPlanUseCases
import es.jvbabi.vplanplus.worker.SyncWorker
import javax.inject.Inject

@HiltAndroidApp
class VppApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var profileUseCases: ProfileUseCases

    @Inject
    lateinit var vPlanUseCases: VPlanUseCases

    @Inject
    lateinit var schoolUseCases: SchoolUseCases

    @Inject
    lateinit var logRecordRepository: LogRecordRepository

    @Inject
    lateinit var keyValueUseCases: KeyValueUseCases

    @Inject
    lateinit var lessonUseCases: LessonUseCases

    @Inject
    lateinit var classUseCases: ClassUseCases

    @Inject
    lateinit var roomRepository: RoomRepository

    @Inject
    lateinit var teacherRepository: TeacherRepository

    @Inject
    lateinit var calendarRepository: CalendarRepository

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(
                SyncWorkerFactory(
                    profileUseCases = profileUseCases,
                    vPlanUseCases = vPlanUseCases,
                    schoolUseCases = schoolUseCases,
                    keyValueUseCases = keyValueUseCases,
                    logRecordRepository = logRecordRepository,
                    lessonUseCases = lessonUseCases,
                    classUseCases = classUseCases,
                    roomRepository = roomRepository,
                    teacherRepository = teacherRepository,
                    calendarRepository = calendarRepository
                )
            )
            .build()
}

class SyncWorkerFactory @Inject constructor(
    private val profileUseCases: ProfileUseCases,
    private val vPlanUseCases: VPlanUseCases,
    private val schoolUseCases: SchoolUseCases,
    private val keyValueUseCases: KeyValueUseCases,
    private val lessonUseCases: LessonUseCases,
    private val classUseCases: ClassUseCases,
    private val logRecordRepository: LogRecordRepository,
    private val roomRepository: RoomRepository,
    private val teacherRepository: TeacherRepository,
    private val calendarRepository: CalendarRepository,
    ) : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): SyncWorker {
        return SyncWorker(
            context = appContext,
            params = workerParameters,
            profileUseCases = profileUseCases,
            vPlanUseCases = vPlanUseCases,
            schoolUseCases = schoolUseCases,
            logRecordRepository = logRecordRepository,
            keyValueUseCases = keyValueUseCases,
            lessonUseCases = lessonUseCases,
            calendarRepository = calendarRepository,
            roomRepository = roomRepository,
            classUseCases = classUseCases,
            teacherRepository = teacherRepository
        )
    }
}