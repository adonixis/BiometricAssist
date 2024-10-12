package ru.adonixis.biometricassist.voice

class VoiceValidator(
    private val voiceQualityParameters: VoiceQualityParameters
) {
    fun getVoiceHints(voiceQuality: VoiceQuality): List<VoiceHint> {
        val hints = mutableListOf<VoiceHint>()

        with(voiceQuality) {
            with(voiceQualityParameters) {
                if (shouldCheckSignalNoise) {
                    signalNoise?.let { signalNoise
                        if (signalNoise > signalNoiseMax) {
                            hints.add(VoiceHint.SPEAK_LOUDER)
                        }
                    }
                }

                if (shouldCheckOverloadBorder) {
                    if (overloadBorder == true) {
                        hints.add(VoiceHint.SPEAK_QUIETLY)
                    }
                }

                if (shouldCheckRussianLanguage) {
                    if (russianLanguage == false) {
                        hints.add(VoiceHint.SPEAK_RUSSIAN)
                    }
                }

                if (shouldCheckSingleVoice) {
                    if (singleVoice == false) {
                        hints.add(VoiceHint.MOVE_TO_QUIETER_PLACE)
                    }
                }

                if (shouldCheckOnlyNumbers) {
                    if (onlyNumbers == false) {
                        hints.add(VoiceHint.SPEAK_ONLY_NUMBERS)
                    }
                }
            }
        }
        return hints
    }
}
