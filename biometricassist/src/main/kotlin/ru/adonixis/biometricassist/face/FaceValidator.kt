package ru.adonixis.biometricassist.face

class FaceValidator(
    private val faceQualityParameters: FaceQualityParameters
) {

    fun getFaceHints(faceQuality: FaceQuality): List<FaceHint> {
        val hints = mutableListOf<FaceHint>()

        with(faceQuality) {
            with(faceQualityParameters) {
                if (shouldCheckFacesCount) {
                    if (facesCount <= 0) {
                        hints.add(FaceHint.NO_FACES_DETECTED)
                    } else if (facesCount > 1) {
                        hints.add(FaceHint.MULTIPLE_FACES_DETECTED)
                    }
                }

                if (shouldCheckHeadPose) {
                    headPose?.let { headPose ->
                        if (headPose.rotationX < headPoseRotationXMin) {
                            hints.add(FaceHint.RAISE_HEAD)
                        }
                        if (headPose.rotationX > headPoseRotationXMax) {
                            hints.add(FaceHint.LOWER_HEAD)
                        }
                        if (headPose.rotationY < headPoseRotationYMin) {
                            hints.add(FaceHint.TURN_HEAD_LEFT)
                        }
                        if (headPose.rotationY > headPoseRotationYMax) {
                            hints.add(FaceHint.TURN_HEAD_RIGHT)
                        }
                        if (headPose.rotationZ < headPoseRotationZMin) {
                            hints.add(FaceHint.ROTATE_HEAD_CLOCKWISE)
                        }
                        if (headPose.rotationZ > headPoseRotationZMax) {
                            hints.add(FaceHint.ROTATE_HEAD_COUNTERCLOCKWISE)
                        }
                    }
                }

                if (shouldCheckEyesDistance) {
                    eyesDistance?.let { eyesDistance ->
                        val eyesDistanceRatio = eyesDistance / photoSize.width.toFloat()
                        if (eyesDistanceRatio < eyesDistanceRatioMin) {
                            hints.add(FaceHint.MOVE_PHONE_CLOSER)
                        }
                    }
                }

                if (shouldCheckHeadSize) {
                    headSize?.let { headSize ->
                        val headSizeRatio = headSize.width / photoSize.width.toFloat()
                        if (headSizeRatio > headSizeRatioMax) {
                            hints.add(FaceHint.FACE_FULLY_IN_FRAME)
                        }
                    }
                }

                if (shouldCheckObstruction) {
                    if (obstruction == true) {
                        hints.add(FaceHint.FACE_NOT_OBSTRUCTED)
                    }
                }

                if (shouldCheckNotNeutral) {
                    smilingProbability?.let { smilingProbability ->
                        if (smilingProbability >= smilingProbabilityMax) {
                            hints.add(FaceHint.NEUTRAL_EXPRESSION_REQUIRED)
                        }
                    }
                }

                if (shouldCheckEyesClosed) {
                    val eyesClosed = if (leftEyeOpenProbability == null || rightEyeOpenProbability == null) {
                        null
                    } else {
                        leftEyeOpenProbability < eyeOpenProbabilityMin || rightEyeOpenProbability < eyeOpenProbabilityMin
                    }
                    if (eyesClosed == true) {
                        hints.add(FaceHint.OPEN_EYES)
                    }
                }

                if (shouldCheckIllumination) {
                    illumination?.let { illumination ->
                        if (illumination < illuminationMin) {
                            hints.add(FaceHint.IMPROVE_ILLUMINATION)
                        }
                    }
                }

                if (shouldCheckDistortion) {
                    if (distortion == true) {
                        hints.add(FaceHint.REDUCE_DISTORTION)
                    }
                }
            }
        }
        return hints
    }
}
