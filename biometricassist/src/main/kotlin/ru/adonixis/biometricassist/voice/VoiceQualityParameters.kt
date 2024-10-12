package ru.adonixis.biometricassist.voice

data class VoiceQualityParameters(
    val shouldCheckSignalNoise: Boolean = true,
    val shouldCheckOverloadBorder: Boolean = true,
    val shouldCheckRussianLanguage: Boolean = true,
    val shouldCheckSingleVoice: Boolean = true,
    val shouldCheckOnlyNumbers: Boolean = true,
    val signalNoiseMax: Float = 10.0f,
)
