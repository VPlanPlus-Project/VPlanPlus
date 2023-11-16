package es.jvbabi.vplanplus.domain.repository

import es.jvbabi.vplanplus.domain.model.DefaultLesson

interface DefaultLessonRepository {
    suspend fun insert(vpId: Long, subject: String, teacherId: Long?, classId: Long): Long
    suspend fun getDefaultLessonByVpId(vpId: Long): DefaultLesson?
}