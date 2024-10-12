package ru.adonixis.biometricassist.face

import android.util.Size

data class FaceQuality(
    val photoSize: Size,
    val facesCount: Int,
    val headPose: HeadPose? = null,
    val eyesDistance: Float? = null,
    val headSize: Size? = null,
    val obstruction: Boolean? = null,
    val smilingProbability: Float? = null,
    val leftEyeOpenProbability: Float? = null,
    val rightEyeOpenProbability: Float? = null,
    val illumination: Float? = null,
    val distortion: Boolean? = null,
)

data class HeadPose(
    val rotationX: Float,
    val rotationY: Float,
    val rotationZ: Float,
) {
    override fun toString(): String {
        return "HeadPose(rotationX=${"%.2f".format(rotationX)}, rotationY=${"%.2f".format(rotationY)}, rotationZ=${"%.2f".format(rotationZ)})"
    }
}
