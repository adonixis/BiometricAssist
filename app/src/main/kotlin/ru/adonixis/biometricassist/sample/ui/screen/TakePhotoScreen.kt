package ru.adonixis.biometricassist.sample.ui.screen

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture.OnImageCapturedCallback
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import ru.adonixis.biometricassistsample.R
import ru.adonixis.biometricassist.sample.ui.component.CameraPreview
import ru.adonixis.biometricassist.face.FaceDebugInfo
import ru.adonixis.biometricassist.face.FaceAnalyzer
import ru.adonixis.biometricassist.face.FaceHint
import ru.adonixis.biometricassist.face.FaceQuality
import ru.adonixis.biometricassist.face.FaceQualityParameters
import ru.adonixis.biometricassist.face.FaceValidator
import ru.adonixis.biometricassist.sample.ui.theme.BiometricAssistTheme
import ru.adonixis.biometricassist.sample.ui.theme.Black
import ru.adonixis.biometricassist.sample.ui.theme.Blue
import ru.adonixis.biometricassist.sample.ui.theme.GreyBlue
import ru.adonixis.biometricassist.sample.ui.theme.White

@Composable
fun TakePhotoScreen(
    modifier: Modifier = Modifier
) {
    var showDebugInfo by remember { mutableStateOf(false) }

    val context = LocalContext.current

    val highAccuracyOpts = remember {
        FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .build()
    }
    val faceDetector = remember {
        FaceDetection.getClient(highAccuracyOpts)
    }
    var debugFaces by remember {
        mutableStateOf(emptyList<FaceDebugInfo>())
    }
    var faceQuality by remember {
        mutableStateOf<FaceQuality?>(null)
    }
    val faceQualityParameters = remember {
        FaceQualityParameters()
    }
    val faceValidator = remember {
        FaceValidator(faceQualityParameters)
    }
    var faceHints by remember {
        mutableStateOf<List<FaceHint>>(emptyList())
    }
    var photoBitmap by remember {
        mutableStateOf<Bitmap?>(null)
    }

    LaunchedEffect(faceQuality) {
        faceQuality?.let { quality ->
            faceHints = faceValidator.getFaceHints(quality)
        }
    }

    val faceAnalyzer = remember {
        FaceAnalyzer(
            faceDetector = faceDetector,
            onFaceDetected = { quality ->
                faceQuality = quality
            },
            onDebugFacesDetected = { faces ->
                debugFaces = faces
            }
        )
    }
    val controller = remember {
        LifecycleCameraController(context).apply {
            cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
            imageAnalysisBackpressureStrategy = ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
            setEnabledUseCases(
                CameraController.IMAGE_CAPTURE or
                        CameraController.IMAGE_ANALYSIS)
            setImageAnalysisAnalyzer(
                ContextCompat.getMainExecutor(context),
                faceAnalyzer
            )
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
    ) {
        CameraPreview(
            controller = controller,
            modifier = Modifier
                .fillMaxSize()
        )

        Canvas(modifier = Modifier.fillMaxSize()) {
            val circlePath = Path().apply {
                addOval(Rect(
                    left = center.x - size.minDimension / 2.8f,
                    top = center.y - (size.minDimension / 2.2f) - (size.maxDimension / 10f),
                    right = center.x + size.minDimension / 2.8f,
                    bottom = center.y + (size.minDimension / 2.2f) - (size.maxDimension / 10f)
                ))
            }
            clipPath(circlePath, clipOp = ClipOp.Difference) {
                drawRect(SolidColor(Black.copy(alpha = 0.8f)))
            }
        }

        if (showDebugInfo) {
            Column(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f))
            ) {
                if (debugFaces.isEmpty()) {
                    Text(
                        modifier = Modifier
                            .padding(8.dp),
                        fontSize = 12.sp,
                        color = Black,
                        text = "No faces detected"
                    )
                } else if (debugFaces.size > 1) {
                    val face = debugFaces.first()
                    with(face) {
                        val text = """
                            photoSize = $photoSize,
                            illumination = ${"%.2f".format(illumination)},
                            facesCount = ${debugFaces.size}
                        """.trimIndent()

                        Text(
                            modifier = Modifier
                                .padding(8.dp),
                            fontSize = 12.sp,
                            color = Black,
                            text = text
                        )
                    }
                } else {
                    val face = debugFaces.first()

                    val bounds = face.boundingBox
                    val faceWidth = bounds.width()
                    val faceHeight = bounds.height()
                    with(face) {
                        val text = """
                            photoSize = $photoSize,
                            illumination = ${"%.2f".format(illumination)},
                            facesCount = ${debugFaces.size},
                            faceBounds = $bounds,
                            faceWidth = $faceWidth, faceHeight = $faceHeight,
                            headEulerAngleX = ${"%.2f".format(headEulerAngleX)},
                            headEulerAngleY = ${"%.2f".format(headEulerAngleY)},
                            headEulerAngleZ = ${"%.2f".format(headEulerAngleZ)},
                            leftEarPos = PointF(${"%.2f".format(leftEarPos?.x)}, ${"%.2f".format(leftEarPos?.y)}),
                            rightEarPos = PointF(${"%.2f".format(rightEarPos?.x)}, ${"%.2f".format(rightEarPos?.y)}),
                            leftEyePos = PointF(${"%.2f".format(leftEyePos?.x)}, ${"%.2f".format(leftEyePos?.y)}),
                            rightEyePos = PointF(${"%.2f".format(rightEyePos?.x)}, ${"%.2f".format(rightEyePos?.y)}),
                            leftCheekPos = PointF(${"%.2f".format(leftCheekPos?.x)}, ${"%.2f".format(leftCheekPos?.y)}),
                            rightCheekPos = PointF(${"%.2f".format(rightCheekPos?.x)}, ${"%.2f".format(rightCheekPos?.y)}),
                            noseBasePos = PointF(${"%.2f".format(noseBasePos?.x)}, ${"%.2f".format(noseBasePos?.y)}),
                            mouthLeftPos = PointF(${"%.2f".format(mouthLeftPos?.x)}, ${"%.2f".format(mouthLeftPos?.y)}),
                            mouthRightPos = PointF(${"%.2f".format(mouthRightPos?.x)}, ${"%.2f".format(mouthRightPos?.y)}),
                            mouthBottomPos = PointF(${"%.2f".format(mouthBottomPos?.x)}, ${"%.2f".format(mouthBottomPos?.y)}),
                            smilingProbability = ${"%.2f".format(smilingProbability)},
                            rightEyeOpenProbability = ${"%.2f".format(rightEyeOpenProbability)},
                            leftEyeOpenProbability = ${"%.2f".format(leftEyeOpenProbability)}
                        """.trimIndent()

                        Text(
                            modifier = Modifier
                                .padding(8.dp),
                            fontSize = 12.sp,
                            color = Black,
                            text = text
                        )
                    }
                }
            }
        }

        if (faceHints.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 80.dp, start = 8.dp, end = 8.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(8.dp)
            ) {
                faceHints.forEach { hint ->
                    val textHint = when (hint) {
                        FaceHint.NO_FACES_DETECTED -> stringResource(R.string.no_faces_detected)
                        FaceHint.MULTIPLE_FACES_DETECTED -> stringResource(R.string.multiple_faces_detected)
                        FaceHint.RAISE_HEAD -> stringResource(R.string.raise_head)
                        FaceHint.LOWER_HEAD -> stringResource(R.string.lower_head)
                        FaceHint.TURN_HEAD_LEFT -> stringResource(R.string.turn_head_left)
                        FaceHint.TURN_HEAD_RIGHT -> stringResource(R.string.turn_head_right)
                        FaceHint.ROTATE_HEAD_CLOCKWISE -> stringResource(R.string.rotate_head_clockwise)
                        FaceHint.ROTATE_HEAD_COUNTERCLOCKWISE -> stringResource(R.string.rotate_head_counterclockwise)
                        FaceHint.MOVE_PHONE_CLOSER -> stringResource(R.string.move_phone_closer)
                        FaceHint.MOVE_PHONE_FARTHER -> stringResource(R.string.move_phone_farther)
                        FaceHint.FACE_FULLY_IN_FRAME -> stringResource(R.string.face_fully_in_frame)
                        FaceHint.FACE_NOT_OBSTRUCTED -> stringResource(R.string.face_not_obstructed)
                        FaceHint.NEUTRAL_EXPRESSION_REQUIRED -> stringResource(R.string.neutral_expression_required)
                        FaceHint.OPEN_EYES -> stringResource(R.string.open_eyes)
                        FaceHint.IMPROVE_ILLUMINATION -> stringResource(R.string.improve_illumination)
                        FaceHint.REDUCE_DISTORTION -> stringResource(R.string.reduce_distortion)
                    }
                    Text(
                        modifier = Modifier
                            .fillMaxWidth(),
                        fontSize = 24.sp,
                        textAlign = TextAlign.Center,
                        color = Black,
                        text = textHint
                    )
                }
            }
        }

        IconButton(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(80.dp)
                .padding(16.dp),
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = White,
                contentColor = Blue,
                disabledContainerColor = White,
                disabledContentColor = GreyBlue
            ),
            onClick = {
                switchCamera(controller)
            }
        ) {
            Icon(
                modifier = Modifier
                    .size(48.dp)
                    .background(White, shape = CircleShape)
                    .padding(8.dp),
                imageVector = Icons.Default.Cameraswitch,
                contentDescription = "Switch camera"
            )
        }

        IconButton(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .size(80.dp)
                .padding(16.dp),
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = White,
                contentColor = Blue,
                disabledContainerColor = White,
                disabledContentColor = GreyBlue
            ),
            onClick = {
                takePhoto(
                    context = context,
                    controller = controller,
                    onPhotoTaken = { bitmap ->
                        photoBitmap = bitmap
                    }
                )
            },
            enabled = faceHints.isEmpty()
        ) {
            Icon(
                modifier = Modifier
                    .size(48.dp)
                    .padding(8.dp),
                imageVector = Icons.Default.PhotoCamera,
                contentDescription = "Take photo",
            )
        }

        Switch(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            checked = showDebugInfo,
            onCheckedChange = { showDebugInfo = it }
        )

        photoBitmap?.let { bitmap ->
            Image(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        photoBitmap = null
                    },
                bitmap = bitmap.asImageBitmap(),
                contentScale = ContentScale.Crop,
                contentDescription = "Face Photo"
            )
        }
    }
}

private fun switchCamera(controller: LifecycleCameraController) {
    controller.cameraSelector =
        if (controller.cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
            CameraSelector.DEFAULT_FRONT_CAMERA
        } else {
            CameraSelector.DEFAULT_BACK_CAMERA
        }
}

private fun takePhoto(
    context: Context,
    controller: LifecycleCameraController,
    onPhotoTaken: (Bitmap) -> Unit
) {
    controller.takePicture(
        ContextCompat.getMainExecutor(context),
        object : OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
                super.onCaptureSuccess(image)

                val matrix = Matrix().apply {
                    postRotate(image.imageInfo.rotationDegrees.toFloat())
                }
                val rotatedBitmap = Bitmap.createBitmap(
                    image.toBitmap(),
                    0,
                    0,
                    image.width,
                    image.height,
                    matrix,
                    true
                )

                onPhotoTaken(rotatedBitmap)
            }

            override fun onError(exception: ImageCaptureException) {
                super.onError(exception)

                Log.e("Camera", "Couldn't take photo: ", exception)
            }

        }
    )
}

@Preview(showBackground = true)
@Composable
fun TakePhotoScreenPreview() {
    BiometricAssistTheme {
        TakePhotoScreen()
    }
}
