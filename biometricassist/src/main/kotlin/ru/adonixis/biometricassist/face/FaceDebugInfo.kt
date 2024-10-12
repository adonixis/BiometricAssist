package ru.adonixis.biometricassist.face

import android.graphics.PointF
import android.graphics.Rect
import android.util.Size

data class FaceDebugInfo(
    val photoSize: Size,
    val illumination: Float? = null,
    val boundingBox: Rect,
    val headEulerAngleX: Float,
    val headEulerAngleY: Float,
    val headEulerAngleZ: Float,
    val leftEarPos: PointF?,
    val rightEarPos: PointF?,
    val leftEyePos: PointF?,
    val rightEyePos: PointF?,
    val leftCheekPos: PointF?,
    val rightCheekPos: PointF?,
    val noseBasePos: PointF?,
    val mouthLeftPos: PointF?,
    val mouthRightPos: PointF?,
    val mouthBottomPos: PointF?,
    val smilingProbability: Float?,
    val rightEyeOpenProbability: Float?,
    val leftEyeOpenProbability: Float?,
)
