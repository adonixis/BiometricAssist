package ru.adonixis.biometricassist.face

import android.graphics.PointF
import android.graphics.Rect
import android.util.Log
import android.util.Size
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceLandmark
import kotlin.math.hypot

class FaceAnalyzer(
    private val faceDetector: FaceDetector,
    private val onFaceDetected: (FaceQuality) -> Unit,
    private val onDebugFacesDetected: (List<FaceDebugInfo>) -> Unit,
): ImageAnalysis.Analyzer {

    @ExperimentalGetImage
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        val rotationDegrees = imageProxy.imageInfo.rotationDegrees
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, rotationDegrees)

            faceDetector.process(image)
                .addOnSuccessListener { faces ->
                    val photoSize = if (rotationDegrees == 0 || rotationDegrees == 180) {
                        Size(
                            image.width,
                            image.height
                        )
                    } else {
                        Size(
                            image.height,
                            image.width
                        )
                    }
                    val illumination = getIllumination(image)
                    val facesDebugInfo = getFacesDebugInfo(
                        photoSize = photoSize,
                        faces = faces,
                        illumination = illumination
                    )
                    onDebugFacesDetected(facesDebugInfo)

                    val faceQuality = getFaceQuality(
                        photoSize = photoSize,
                        faces = faces,
                        illumination = illumination
                    )
                    onFaceDetected(faceQuality)
                }
                .addOnFailureListener { exception ->
                    Log.e("FaceAnalyzer", "Couldn't detect face: ", exception)
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        }
    }
}

private fun getIllumination(image: InputImage): Float? {
    return image.planes?.let { planes ->
        val plane = planes[0]
        val buffer = plane.buffer
        val rowStride = plane.rowStride
        val pixelStride = plane.pixelStride
        val width = image.width
        val height = image.height

        var totalLuminance = 0L
        var pixelCount = 0

        for (row in 0 until height) {
            val rowStart = row * rowStride
            buffer.position(rowStart)
            for (col in 0 until width) {
                val pixel = buffer.get().toInt() and 0xFF
                totalLuminance += pixel
                pixelCount++
                buffer.position(buffer.position() + pixelStride - 1)
            }
        }

        val averageLuminance = if (pixelCount > 0) (totalLuminance.toFloat() / pixelCount.toFloat()) else null
        return averageLuminance
    }
}

private fun getFaceQuality(
    photoSize: Size,
    faces: List<Face>,
    illumination: Float?,
): FaceQuality {
    val faceQuality = FaceQuality(
        photoSize = photoSize,
        facesCount = faces.size,
        illumination = illumination,
    )
    if (faces.isEmpty()) {
        return faceQuality
    }
    if (faces.size > 1) {
        return faceQuality
    }

    val face = faces.first()
    val faceBounds = face.boundingBox

    val headPose = HeadPose(
        rotationX = face.headEulerAngleX,
        rotationY = face.headEulerAngleY,
        rotationZ = face.headEulerAngleZ
    )

    val leftEyePos = face.getLandmark(FaceLandmark.LEFT_EYE)?.position
    val rightEyePos = face.getLandmark(FaceLandmark.RIGHT_EYE)?.position
    val eyesDistance = if (leftEyePos != null && rightEyePos != null) {
        distance(leftEyePos, rightEyePos)
    } else null

    val headSize = Size(
        faceBounds.width(),
        faceBounds.height()
    )

    val smilingProbability = face.smilingProbability
    val leftEyeOpenProbability = face.leftEyeOpenProbability
    val rightEyeOpenProbability = face.rightEyeOpenProbability

    return faceQuality.copy(
        headPose = headPose,
        eyesDistance = eyesDistance,
        headSize = headSize,
        obstruction = null,
        smilingProbability = smilingProbability,
        leftEyeOpenProbability = leftEyeOpenProbability,
        rightEyeOpenProbability = rightEyeOpenProbability,
        distortion = null
    )
}

private fun getFacesDebugInfo(
    photoSize: Size,
    faces: List<Face>,
    illumination: Float?
): List<FaceDebugInfo> {
    return faces.map { face ->
        with(face) {
            val leftEarPos = getLandmark(FaceLandmark.LEFT_EAR)?.position
            val rightEarPos = getLandmark(FaceLandmark.RIGHT_EAR)?.position
            val leftEyePos = getLandmark(FaceLandmark.LEFT_EYE)?.position
            val rightEyePos = getLandmark(FaceLandmark.RIGHT_EYE)?.position
            val leftCheekPos = getLandmark(FaceLandmark.LEFT_CHEEK)?.position
            val rightCheekPos = getLandmark(FaceLandmark.RIGHT_CHEEK)?.position
            val noseBasePos = getLandmark(FaceLandmark.NOSE_BASE)?.position
            val mouthLeftPos = getLandmark(FaceLandmark.MOUTH_LEFT)?.position
            val mouthRightPos = getLandmark(FaceLandmark.MOUTH_RIGHT)?.position
            val mouthBottomPos = getLandmark(FaceLandmark.MOUTH_BOTTOM)?.position

            FaceDebugInfo(
                photoSize = photoSize,
                illumination = illumination,
                boundingBox = boundingBox,
                headEulerAngleX = headEulerAngleX,
                headEulerAngleY = headEulerAngleY,
                headEulerAngleZ = headEulerAngleZ,
                leftEarPos = leftEarPos,
                rightEarPos = rightEarPos,
                leftEyePos = leftEyePos,
                rightEyePos = rightEyePos,
                leftCheekPos = leftCheekPos,
                rightCheekPos = rightCheekPos,
                noseBasePos = noseBasePos,
                mouthLeftPos = mouthLeftPos,
                mouthRightPos = mouthRightPos,
                mouthBottomPos = mouthBottomPos,
                smilingProbability = smilingProbability,
                rightEyeOpenProbability = rightEyeOpenProbability,
                leftEyeOpenProbability = leftEyeOpenProbability,
            )
        }
    }
}

private fun distance(point1: PointF, point2: PointF): Float {
    val dx = point1.x - point2.x
    val dy = point1.y - point2.y
    return hypot(dx, dy)
}
