package es.jvbabi.vplanplus.feature.main_calendar.home.ui.components.exam.new_exam

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.domain.model.ExamCategory
import es.jvbabi.vplanplus.ui.common.Option
import es.jvbabi.vplanplus.ui.common.OptionTextTitle
import es.jvbabi.vplanplus.ui.stringResource
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExamCategorySection(
    selectedCategory: ExamCategory?,
    onCategorySelected: (category: ExamCategory) -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()
    var isCategoryModalOpen by rememberSaveable { mutableStateOf(false) }
    val categorySheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    if (isCategoryModalOpen) {
        LaunchedEffect(Unit) { focusManager.clearFocus() }
        ModalBottomSheet(
            onDismissRequest = { isCategoryModalOpen = false },
            sheetState = categorySheetState,
        ) {
            Column(Modifier.padding(bottom = WindowInsets.systemBars.asPaddingValues().calculateBottomPadding())) {
                Text(
                    text = stringResource(id = R.string.examsNew_category),
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp),
                    style = MaterialTheme.typography.headlineSmall
                )
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                        .verticalScroll(rememberScrollState())
                ) {
                    ExamCategory.values.forEach { category ->
                        Option(
                            title = OptionTextTitle(stringResource(category.stringResource())),
                            icon = null,
                            state = selectedCategory == category,
                            enabled = true,
                            modifier = Modifier.border(width = .25.dp, color = MaterialTheme.colorScheme.outline),
                        ) {
                            onCategorySelected(category)
                            scope.launch { categorySheetState.hide(); isCategoryModalOpen = false }
                        }
                    }
                }
            }
        }
    }
    AddExamItem(
        icon = {
            AnimatedContent(
                targetState = selectedCategory,
                label = "category"
            ) { category ->
                Icon(
                    imageVector = if (category == null) Icons.Outlined.Category else Icons.Filled.Category,
                    contentDescription = null,
                    tint = if (category == null) Color.Gray else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    ) {
        Box(
            modifier = Modifier
                .height(48.dp)
                .clip(RoundedCornerShape(16.dp))
                .clickable { isCategoryModalOpen = true }
                .padding(start = 8.dp)
                .fillMaxWidth(),
            contentAlignment = Alignment.CenterStart
        ) {
            AnimatedContent(
                targetState = selectedCategory,
                label = "category"
            ) { category ->
                if (category == null) Text(
                    text = stringResource(R.string.examsNew_category),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                ) else Text(
                    text = stringResource(id = category.stringResource()),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun AddExamCategorySectionPreview() {
    AddExamCategorySection(null) {}
}

@Composable
@Preview(showBackground = true)
private fun AddExamCategorySectionPreview2() {
    AddExamCategorySection(ExamCategory.ShortTest) {}
}