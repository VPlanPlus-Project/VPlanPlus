package es.jvbabi.vplanplus.feature.homework.shared.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import es.jvbabi.vplanplus.data.source.database.VppDatabase
import es.jvbabi.vplanplus.feature.homework.shared.data.repository.HomeworkRepositoryImpl
import es.jvbabi.vplanplus.feature.homework.shared.domain.repository.HomeworkRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object HomeworkModule {

    @Provides
    @Singleton
    fun provideHomeworkRepository(
        db: VppDatabase
    ): HomeworkRepository {
        return HomeworkRepositoryImpl(
            homeworkDao = db.homeworkDao
        )
    }
}