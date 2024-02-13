package es.jvbabi.vplanplus.feature.news.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import es.jvbabi.vplanplus.feature.news.data.repository.NewsRepositoryImpl
import es.jvbabi.vplanplus.data.source.database.VppDatabase
import es.jvbabi.vplanplus.domain.repository.MessageRepository
import es.jvbabi.vplanplus.domain.repository.NotificationRepository
import es.jvbabi.vplanplus.shared.data.NewsNetworkRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MessageModule {

    @Provides
    @Singleton
    fun provideNewsNetworkRepository(): NewsNetworkRepository {
        return NewsNetworkRepository()
    }

    @Provides
    @Singleton
    fun provideMessageRepository(
        db: VppDatabase,
        newsNetworkRepository: NewsNetworkRepository,
        @ApplicationContext context: Context,
        notificationRepository: NotificationRepository
    ): MessageRepository {
        return NewsRepositoryImpl(
            networkRepository = newsNetworkRepository,
            messageDao = db.messageDao,
            context = context,
            notificationRepository = notificationRepository
        )
    }
}