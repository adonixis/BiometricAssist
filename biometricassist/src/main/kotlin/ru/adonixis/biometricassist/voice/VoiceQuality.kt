package ru.adonixis.biometricassist.voice

data class VoiceQuality(
    val text: String,
    val signalNoise: Float? = null,
    val overloadBorder: Boolean? = null,
    val russianLanguage: Boolean? = null,
    val singleVoice: Boolean? = null,
    val onlyNumbers: Boolean? = null,
)
