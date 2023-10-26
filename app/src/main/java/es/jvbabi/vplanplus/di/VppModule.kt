package es.jvbabi.vplanplus.di

import android.app.Application
import androidx.room.Room
import androidx.room.RoomDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import es.jvbabi.vplanplus.data.repository.BaseDataRepositoryImpl
import es.jvbabi.vplanplus.data.repository.ClassRepositoryImpl
import es.jvbabi.vplanplus.data.repository.DefaultLessonRepositoryImpl
import es.jvbabi.vplanplus.data.repository.HolidayRepositoryImpl
import es.jvbabi.vplanplus.data.repository.KeyValueRepositoryImpl
import es.jvbabi.vplanplus.data.repository.LessonRepositoryImpl
import es.jvbabi.vplanplus.data.repository.LessonTimeRepositoryImpl
import es.jvbabi.vplanplus.data.repository.ProfileRepositoryImpl
import es.jvbabi.vplanplus.data.repository.RoomRepositoryImpl
import es.jvbabi.vplanplus.data.repository.SchoolRepositoryImpl
import es.jvbabi.vplanplus.data.repository.TeacherRepositoryImpl
import es.jvbabi.vplanplus.data.repository.VPlanRepositoryImpl
import es.jvbabi.vplanplus.data.repository.WeekRepositoryImpl
import es.jvbabi.vplanplus.data.source.VppDatabase
import es.jvbabi.vplanplus.domain.repository.BaseDataRepository
import es.jvbabi.vplanplus.domain.repository.ClassRepository
import es.jvbabi.vplanplus.domain.repository.DefaultLessonRepository
import es.jvbabi.vplanplus.domain.repository.HolidayRepository
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.LessonRepository
import es.jvbabi.vplanplus.domain.repository.LessonTimeRepository
import es.jvbabi.vplanplus.domain.repository.ProfileRepository
import es.jvbabi.vplanplus.domain.repository.RoomRepository
import es.jvbabi.vplanplus.domain.repository.SchoolRepository
import es.jvbabi.vplanplus.domain.repository.TeacherRepository
import es.jvbabi.vplanplus.domain.repository.VPlanRepository
import es.jvbabi.vplanplus.domain.repository.WeekRepository
import es.jvbabi.vplanplus.domain.usecase.BaseDataUseCases
import es.jvbabi.vplanplus.domain.usecase.ClassUseCases
import es.jvbabi.vplanplus.domain.usecase.HolidayUseCases
import es.jvbabi.vplanplus.domain.usecase.KeyValueUseCases
import es.jvbabi.vplanplus.domain.usecase.LessonUseCases
import es.jvbabi.vplanplus.domain.usecase.OnboardingUseCases
import es.jvbabi.vplanplus.domain.usecase.ProfileUseCases
import es.jvbabi.vplanplus.domain.usecase.SchoolUseCases
import es.jvbabi.vplanplus.domain.usecase.VPlanUseCases
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object VppModule {

    // Database
    @Provides
    @Singleton
    fun provideVppDatabase(app: Application): VppDatabase {
        return Room.databaseBuilder(
            app,
            VppDatabase::class.java,
            "vpp.db"
        )
            .fallbackToDestructiveMigration() // TODO: Remove for production
            .setJournalMode(RoomDatabase.JournalMode.TRUNCATE)
            .build()
    }

    // Repositories
    @Provides
    @Singleton
    fun provideSchoolRepository(db: VppDatabase): SchoolRepository {
        return SchoolRepositoryImpl(db.schoolDao)
    }

    @Provides
    @Singleton
    fun provideKeyValueRepository(db: VppDatabase): KeyValueRepository {
        return KeyValueRepositoryImpl(db.keyValueDao)
    }

    @Provides
    @Singleton
    fun provideProfileRepository(db: VppDatabase): ProfileRepository {
        return ProfileRepositoryImpl(db.profileDao)
    }

    @Provides
    @Singleton
    fun provideClassRepository(db: VppDatabase): ClassRepository {
        return ClassRepositoryImpl(db.classDao)
    }

    @Provides
    @Singleton
    fun provideHolidayRepository(db: VppDatabase): HolidayRepository {
        return HolidayRepositoryImpl(db.holidayDao)
    }

    @Provides
    @Singleton
    fun provideWeekRepository(db: VppDatabase): WeekRepository {
        return WeekRepositoryImpl(db.weekDao)
    }

    @Provides
    @Singleton
    fun provideLessonTimeRepository(db: VppDatabase): LessonTimeRepository {
        return LessonTimeRepositoryImpl(db.lessonTimeDao)
    }

    @Provides
    @Singleton
    fun provideBaseDataRepository(
        db: VppDatabase,
        classRepository: ClassRepository,
        lessonTimeRepository: LessonTimeRepository
    ): BaseDataRepository {
        return BaseDataRepositoryImpl(classRepository, lessonTimeRepository)
    }

    @Provides
    @Singleton
    fun provideDefaultLessonRepository(db: VppDatabase): DefaultLessonRepository {
        return DefaultLessonRepositoryImpl(db.defaultLessonDao)
    }

    @Provides
    @Singleton
    fun provideVPlanRepository(): VPlanRepository {
        return VPlanRepositoryImpl()
    }

    @Provides
    @Singleton
    fun provideTeacherRepository(db: VppDatabase): TeacherRepository {
        return TeacherRepositoryImpl(db.teacherDao)
    }

    @Provides
    @Singleton
    fun provideLessonRepository(db: VppDatabase): LessonRepository {
        return LessonRepositoryImpl(db.defaultLessonDao, db.lessonDao)
    }

    @Provides
    @Singleton
    fun provideRoomRepository(db: VppDatabase): RoomRepository {
        return RoomRepositoryImpl(db.roomDao)
    }

    // Use cases

    @Provides
    @Singleton
    fun provideSchoolUseCases(repository: SchoolRepository): SchoolUseCases {
        return SchoolUseCases(repository)
    }

    @Provides
    @Singleton
    fun provideKeyValueUseCases(repository: KeyValueRepository): KeyValueUseCases {
        return KeyValueUseCases(repository)
    }

    @Provides
    @Singleton
    fun provideProfileUseCases(
        repository: ProfileRepository,
        keyValueRepository: KeyValueRepository
    ): ProfileUseCases {
        return ProfileUseCases(repository, keyValueRepository)
    }

    @Provides
    @Singleton
    fun provideOnboardingUseCases(repository: ProfileRepository): OnboardingUseCases {
        return OnboardingUseCases(repository)
    }

    @Provides
    @Singleton
    fun provideClassUseCases(repository: ClassRepository): ClassUseCases {
        return ClassUseCases(repository)
    }

    @Provides
    @Singleton
    fun provideHolidayUseCases(repository: HolidayRepository): HolidayUseCases {
        return HolidayUseCases(repository)
    }

    @Provides
    @Singleton
    fun provideBaseDataUseCases(
        baseDataRepository: BaseDataRepository,
        weekRepository: WeekRepository
    ): BaseDataUseCases {
        return BaseDataUseCases(baseDataRepository, weekRepository)
    }

    @Provides
    @Singleton
    fun provideVPlanUseCases(
        vPlanRepository: VPlanRepository,
        lessonRepository: LessonRepository,
        defaultLessonRepository: DefaultLessonRepository,
        classRepository: ClassRepository,
        teacherRepository: TeacherRepository,
        roomRepository: RoomRepository
    ): VPlanUseCases {
        return VPlanUseCases(
            vPlanRepository = vPlanRepository,
            lessonRepository = lessonRepository,
            defaultLessonRepository = defaultLessonRepository,
            classRepository = classRepository,
            teacherReository = teacherRepository,
            roomRepository = roomRepository
        )
    }

    @Provides
    @Singleton
    fun provideLessonUseCases(lessonRepository: LessonRepository): LessonUseCases {
        return LessonUseCases(lessonRepository)
    }
}