package es.jvbabi.vplanplus.feature.main_home.ui.components.next

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.PersonalizedHomework
import es.jvbabi.vplanplus.ui.common.Spacer4Dp
import es.jvbabi.vplanplus.ui.common.Spacer8Dp
import java.time.LocalDate

@Composable
fun HomeworkSection(
    homework: List<PersonalizedHomework>,
    onOpenHomework: (homeworkId: Int) -> Unit,
    currentProfile: Profile?
) {
    if (homework.isNotEmpty() && currentProfile is ClassProfile) {
        Spacer8Dp()
        Text(
            text = stringResource(R.string.home_homeworkTitle),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .fillMaxWidth()
        )
        Spacer4Dp()
        es.jvbabi.vplanplus.feature.main_calendar.home.ui.components.homework.HomeworkSection(
            contextDate = LocalDate.now(),
            homework = homework,
            onOpenHomeworkScreen = onOpenHomework,
            currentProfile = currentProfile,
            showSection = true,
            includeTitle = false,
            includeUntil = true
        )
    }
}