package es.jvbabi.vplanplus.ui.screens.news.detail

import android.text.util.Linkify
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.android.material.textview.MaterialTextView
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.ui.preview.News
import es.jvbabi.vplanplus.util.DateUtils

@Composable
fun NewsDetailScreen(
    navHostController: NavHostController,
    messageId: String,
    newsDetailViewModel: NewsDetailViewModel = hiltViewModel()
) {

    LaunchedEffect(key1 = messageId, block = {
        newsDetailViewModel.init(messageId)
    })

    NewsDetailScreenContent(
        state = newsDetailViewModel.state.value,
        goBack = { navHostController.popBackStack() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NewsDetailScreenContent(
    state: NewsDetailState,
    goBack: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = if (state.message == null) stringResource(id = R.string.loadingData) else state.message.title) },
                navigationIcon = {
                    IconButton(onClick = { goBack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = stringResource(
                                id = R.string.back
                            )
                        )
                    }
                },
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 8.dp)
        ) {
            if (state.message == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
                return@Scaffold
            }

            val colorScheme = MaterialTheme.colorScheme
            Text(text = DateUtils.localizedRelativeDate(LocalContext.current, state.message.date.toLocalDate()))

            AndroidView(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                factory = {
                    MaterialTextView(it).apply {
                        // links
                        autoLinkMask = Linkify.WEB_URLS
                        linksClickable = true
                        setLinkTextColor(colorScheme.primary.toArgb())
                    }
                },
                update = {
                    it.text = HtmlCompat.fromHtml(state.message.content, 0)
                }
            )
        }
    }
}

@Preview
@Composable
private fun NewsDetailScreenPreview() {
    NewsDetailScreenContent(
        state = NewsDetailState(message = News.generateNews().first())
    )
}