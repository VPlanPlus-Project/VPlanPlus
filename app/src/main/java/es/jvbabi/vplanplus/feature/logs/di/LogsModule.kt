package es.jvbabi.vplanplus.feature.logs.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import es.jvbabi.vplanplus.data.repository.LogRepositoryImpl
import es.jvbabi.vplanplus.data.source.database.VppDatabase
import es.jvbabi.vplanplus.feature.logs.data.repository.LogRecordRepository
import es.jvbabi.vplanplus.feature.logs.domain.usecase.DeleteAllLogsUseCase
import es.jvbabi.vplanplus.feature.logs.domain.usecase.GetLogsUseCase
import es.jvbabi.vplanplus.feature.logs.domain.usecase.LogsUseCases
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LogsModule {

    @Singleton
    @Provides
    fun provideLogsUseCases(
        logRecordRepository: LogRecordRepository
    ): LogsUseCases {
        return LogsUseCases(
            getLogsUseCase = GetLogsUseCase(logRecordRepository),
            deleteAllLogsUseCase = DeleteAllLogsUseCase(logRecordRepository)
        )
    }

    @Provides
    @Singleton
    fun provideLogRepository(db: VppDatabase): LogRecordRepository {
        return LogRepositoryImpl(db.logRecordDao)
    }
}