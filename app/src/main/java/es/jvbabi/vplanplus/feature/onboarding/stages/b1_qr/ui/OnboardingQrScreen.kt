package es.jvbabi.vplanplus.feature.onboarding.stages.b1_qr.ui

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.SwitchCamera
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.feature.onboarding.ui.common.OnboardingScreen
import es.jvbabi.vplanplus.ui.common.Spacer8Dp
import es.jvbabi.vplanplus.ui.screens.Screen
import kotlinx.coroutines.delay
import java.util.concurrent.Executors

@Composable
fun OnboardingQrScreen(
    navHostController: NavHostController,
    viewModel: OnboardingQrViewModel = hiltViewModel()
) {
    val state = viewModel.state

    LaunchedEffect(key1 = Unit) {
        viewModel.init()
    }

    val onProceed = { navHostController.navigate(Screen.OnboardingNewProfileScreen()) }

    LaunchedEffect(key1 = state.qrResultState) {
        if (state.qrResultState == QrResultState.PROCEED) onProceed()
    }

    OnboardingQrScreenContent(
        state = state,
        doAction = viewModel::doAction,
        onOk = onProceed
    )
}

@Composable
private fun OnboardingQrScreenContent(
    doAction: (UiAction) -> Unit,
    state: OnboardingQrState,
    onOk: () -> Unit = {}
) {
    OnboardingScreen(
        title = stringResource(id = R.string.onboarding_qrTitle),
        text = { Text(text = stringResource(id = R.string.onboarding_qrText)) },
        buttonText = stringResource(
            id = R.string.onboarding_qrNext,
            state.qrSchoolState?.schoolId?.toLong() ?: 0L
        ),
        isLoading = state.qrResultState in listOf(QrResultState.LOADING_SCHOOL_DATA, QrResultState.CHECKING, null),
        enabled = false,
        onButtonClick = { onOk() },
        footer = {
            Text(
                text = when (state.qrResultState) {
                    QrResultState.SCHOOL_NOT_FOUND -> stringResource(id = R.string.onboarding_schoolIdNotFound)
                    QrResultState.NETWORK_ERROR -> stringResource(id = R.string.noInternet)
                    QrResultState.INVALID_QR -> stringResource(id = R.string.onboarding_invalidQr)
                    else -> return@OnboardingScreen
                }
            )
        },
        content = {
            Box(Modifier.fillMaxSize()) {
                var cameraIndex by rememberSaveable { mutableIntStateOf(0) }
                Column(Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    key(cameraIndex) {
                        AndroidView(
                            { context ->
                                val cameraExecutor = Executors.newSingleThreadExecutor()
                                val previewView = PreviewView(context).also {
                                    it.scaleType = PreviewView.ScaleType.FILL_CENTER
                                }
                                val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
                                cameraProviderFuture.addListener({
                                    val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

                                    val preview = androidx.camera.core.Preview.Builder()
                                        .build()
                                        .also {
                                            it.setSurfaceProvider(previewView.surfaceProvider)
                                        }

                                    val imageCapture = ImageCapture.Builder().build()

                                    val imageAnalyzer = ImageAnalysis.Builder()
                                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                                        .build()
                                        .also {
                                            it.setAnalyzer(cameraExecutor, QrCodeAnalyzer { result ->
                                                try {
                                                    val data = Gson().fromJson(result, QrSchoolState::class.java)
                                                    if (data.schoolId?.toLongOrNull() == null || data.username.isNullOrBlank() || data.password.isNullOrBlank()) {
                                                        doAction(OnInvalidQrScanned)
                                                        return@QrCodeAnalyzer
                                                    }
                                                    doAction(InputQrResult(data))
                                                } catch (e: JsonSyntaxException) {
                                                    doAction(OnInvalidQrScanned)
                                                }
                                            })
                                        }

                                    val cameras = listOf(CameraSelector.DEFAULT_BACK_CAMERA, CameraSelector.DEFAULT_FRONT_CAMERA)
                                    val cameraSelector = cameras[cameraIndex % cameras.size]

                                    try {
                                        // Unbind use cases before rebinding
                                        cameraProvider.unbindAll()

                                        // Bind use cases to camera
                                        cameraProvider.bindToLifecycle(
                                            context as ComponentActivity,
                                            cameraSelector,
                                            preview,
                                            imageCapture,
                                            imageAnalyzer
                                        )

                                    } catch (exc: Exception) {
                                        Log.e("DEBUG", "Use case binding failed", exc)
                                    }
                                }, ContextCompat.getMainExecutor(context))
                                previewView
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(16.dp))
                        )
                    }
                    OutlinedButton(
                        onClick = { cameraIndex++ },
                        modifier = Modifier.align(Alignment.End),
                        enabled = state.qrResultState != QrResultState.LOADING_SCHOOL_DATA && state.qrResultState != QrResultState.CHECKING
                    ) {
                        Icon(imageVector = Icons.Default.SwitchCamera, contentDescription = stringResource(id = R.string.onboarding_qrSwitchCamera))
                        Spacer8Dp()
                        Text(text = stringResource(id = R.string.onboarding_qrSwitchCamera))
                    }
                }
                androidx.compose.animation.AnimatedVisibility(
                    visible = state.qrResultState == QrResultState.LOADING_SCHOOL_DATA,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .background(MaterialTheme.colorScheme.surface.copy(alpha = .5f)),
                        contentAlignment = Alignment.Center
                    ) {
                        var showCheckmark by rememberSaveable { mutableStateOf(true) }
                        androidx.compose.animation.AnimatedVisibility(
                            visible = showCheckmark,
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(48.dp)
                            )
                        }
                        androidx.compose.animation.AnimatedVisibility(
                            visible = !showCheckmark,
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            CircularProgressIndicator()
                        }

                        LaunchedEffect(key1 = Unit) {
                            showCheckmark = true
                            delay(1000)
                            showCheckmark = false
                        }
                    }
                }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun OnboardingQrScreenPreview() {
    OnboardingQrScreenContent(
        state = OnboardingQrState(
            qrSchoolState = QrSchoolState(
                schoolId = "12345678",
                username = "username",
                password = "password"
            )
        ),
        doAction = {}
    )
}