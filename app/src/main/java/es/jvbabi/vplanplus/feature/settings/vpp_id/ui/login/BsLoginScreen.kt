package es.jvbabi.vplanplus.feature.settings.vpp_id.ui.login

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.View.INVISIBLE
import android.view.ViewGroup.LayoutParams
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.shared.data.VppIdServer
import es.jvbabi.vplanplus.ui.common.InfoCard
import es.jvbabi.vplanplus.ui.screens.Screen
import java.net.URLEncoder


@Composable
fun BsLoginScreen(
    navHostController: NavHostController
) {
    BsLoginContent(
        onBack = { navHostController.popBackStack() },
        onContinue = {
            navHostController.navigate(Screen.AccountAddedScreen.route + "/$it") {
                popUpTo(0)
            }
        }
    )
}

@SuppressLint("SetJavaScriptEnabled")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BsLoginContent(
    onBack: () -> Unit,
    onContinue: (token: String) -> Unit = {}
) {
    var cleanUp by remember {
        mutableStateOf<() -> Unit>({
            Log.d("VppIdLogin", "Cleaning up")
        })
    }
    var pageTitle by rememberSaveable {
        mutableStateOf("")
    }
    BackHandler {
        cleanUp()
        onBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(text = stringResource(id = R.string.vppIdLogin_title))
                        if (pageTitle.isNotBlank()) Text(text = pageTitle, style = MaterialTheme.typography.labelSmall)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {
                        cleanUp()
                        onBack()
                    }) {
                        Icon(
                            imageVector = Icons.Default.Close, contentDescription = stringResource(
                                id = R.string.close
                            )
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            var progress by rememberSaveable {
                mutableIntStateOf(0)
            }
            var bannerVisible by rememberSaveable {
                mutableStateOf(true)
            }
            if (progress < 100) LinearProgressIndicator(
                progress = { progress / 100f },
                modifier = Modifier.fillMaxWidth()
            )
            AndroidView(
                factory = { context ->
                    WebView(context).apply {
                        cleanUp = {
                            Log.d("VppIdLogin", "Cleaning up")
                            this.destroy()
                            this.visibility = INVISIBLE
                        }

                        settings.javaScriptEnabled = true
                        settings.useWideViewPort = true
                        settings.loadWithOverviewMode = true

                        layoutParams = LayoutParams(
                            LayoutParams.MATCH_PARENT,
                            LayoutParams.MATCH_PARENT
                        )

                        loadUrl(
                            "https://${VppIdServer.host}/login/link/?name=VPlanPlus%20on%20" + URLEncoder.encode(
                                Build.MODEL + " (Android " + Build.VERSION.RELEASE + ")", "UTF-8"
                            )
                        )

                        webChromeClient = object : WebChromeClient() {
                            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                                super.onProgressChanged(view, newProgress)
                                progress = newProgress
                            }

                            override fun onReceivedTitle(view: WebView?, title: String?) {
                                super.onReceivedTitle(view, title)
                                pageTitle = title ?: ""
                            }
                        }
                        webViewClient = object : WebViewClient() {
                            override fun shouldOverrideUrlLoading(
                                view: WebView?,
                                request: WebResourceRequest?
                            ): Boolean {
                                if (request != null && listOf("vpp", "mailto").contains(request.url.scheme)) {
                                    if (request.url.scheme == "vpp") onContinue(request.url.pathSegments.last())
                                    else if (request.url.scheme == "mailto") {
                                        view!!.context.startActivity(
                                            Intent(Intent.ACTION_VIEW, request.url)
                                        )
                                        return true
                                    }
                                    return true
                                }
                                return false
                            }
                        }
                    }
                })
            if (bannerVisible) InfoCard(
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.BottomCenter),
                imageVector = Icons.Outlined.Info,
                title = stringResource(id = R.string.vppIdLogin_infoTitle),
                text = stringResource(id = R.string.vppIdLogin_infoText),
                buttonText1 = stringResource(id = android.R.string.ok),
                buttonAction1 = { bannerVisible = false }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BsLoginScreenPreview() {
    BsLoginContent(
        onBack = {}
    )
}