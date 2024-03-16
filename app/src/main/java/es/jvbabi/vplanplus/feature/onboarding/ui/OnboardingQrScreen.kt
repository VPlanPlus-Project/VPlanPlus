package es.jvbabi.vplanplus.feature.onboarding.ui

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.google.gson.Gson
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.ui.screens.Screen
import es.jvbabi.vplanplus.feature.onboarding.ui.common.OnboardingScreen
import es.jvbabi.vplanplus.feature.onboarding.ui.extra.QrCodeAnalyzer
import java.util.concurrent.Executors

@Composable
fun OnboardingQrScreen(
    navHostController: NavHostController,
    viewModel: OnboardingViewModel,
) {
    val state = viewModel.state.value

    LaunchedEffect(key1 = state.stage) {
        if (state.stage == Stage.PROFILE_TYPE) {
            viewModel.newScreen()
            navHostController.navigate(Screen.OnboardingFirstProfileScreen.route) {
                if (state.onboardingCause == OnboardingCause.NEW_PROFILE) popUpTo(Screen.SettingsProfileScreen.route) {
                    inclusive = true
                }
            }
        }
    }

    Box {
        OnboardingQrScreenContent(
            latestResult = state.qrResult,
            state = state,
            onOk = {
                viewModel.useQrResult()
            }
        )
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
                                viewModel.saveQrResult(Gson().fromJson(result, QrResult::class.java))
                            })
                        }

                    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

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
                .align(Alignment.Center)
                .padding(16.dp)
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(16.dp))
        )
    }

    BackHandler {
        viewModel.closeQr()
        navHostController.navigateUp()
    }
}

@Composable
private fun OnboardingQrScreenContent(
    latestResult: QrResult? = null,
    state: OnboardingState,
    onOk: () -> Unit = {}
) {
    OnboardingScreen(
        title = stringResource(id = R.string.onboarding_qrTitle),
        text = { Text(text = stringResource(id = R.string.onboarding_qrText)) },
        buttonText = stringResource(
            id = R.string.onboarding_qrNext,
            latestResult?.schoolId?.toLong() ?: 0L
        ),
        isLoading = latestResult == null || state.isLoading,
        enabled = latestResult != null,
        onButtonClick = { onOk() },
        footer = {},
        content = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun OnboardingQrScreenPreview() {
    OnboardingQrScreenContent(
        latestResult = QrResult(
            schoolId = "12345678",
            username = "username",
            password = "password"
        ),
        state = OnboardingState()
    )
}