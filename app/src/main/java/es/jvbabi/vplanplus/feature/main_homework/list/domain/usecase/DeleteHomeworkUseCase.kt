package es.jvbabi.vplanplus.feature.main_homework.list.domain.usecase

import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.repository.FileRepository
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.PersonalizedHomework
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.HomeworkRepository

class DeleteHomeworkUseCase(
    private val homeworkRepository: HomeworkRepository,
    private val fileRepository: FileRepository,
){

    suspend operator fun invoke(personalizedHomework: PersonalizedHomework): Boolean {
        val profile = (personalizedHomework.profile as? ClassProfile) ?: return false
        val homework = personalizedHomework.homework
        homework.documents.forEach { document ->
            fileRepository.deleteFile("homework_documents", "${document.documentId}.${document.type.extension}")
        }
        if (personalizedHomework is PersonalizedHomework.CloudHomework && profile.vppId != null) {
            homeworkRepository.deleteHomeworkCloud(personalizedHomework).value ?: return false
        }
        homeworkRepository.deleteHomeworkDb(homework)
        return true
    }
}