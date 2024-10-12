package ru.adonixis.biometricassist.sample.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.adonixis.biometricassist.sample.ui.theme.BiometricAssistTheme
import ru.adonixis.biometricassist.sample.ui.theme.Black
import ru.adonixis.biometricassist.sample.ui.theme.White
import ru.adonixis.biometricassist.voice.VoiceAnalyzer
import ru.adonixis.biometricassist.voice.VoiceHint
import ru.adonixis.biometricassist.voice.VoiceQuality
import ru.adonixis.biometricassist.voice.VoiceQualityParameters
import ru.adonixis.biometricassist.voice.VoiceValidator
import ru.adonixis.biometricassistsample.R

@Composable
fun RecordVoiceScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var isRecorded by remember { mutableStateOf(false) }

    var voiceQuality by remember {
        mutableStateOf<VoiceQuality?>(null)
    }

    val voiceQualityParameters = remember {
        VoiceQualityParameters()
    }
    val voiceValidator = remember {
        VoiceValidator(voiceQualityParameters)
    }
    var voiceHints by remember {
        mutableStateOf<List<VoiceHint>>(emptyList())
    }

    LaunchedEffect(voiceQuality) {
        voiceQuality?.let { quality ->
            voiceHints = voiceValidator.getVoiceHints(quality)
        }
    }

    val voiceAnalyzer = remember {
        VoiceAnalyzer(
            context = context,
            onVoiceRecognized = { quality ->
                voiceQuality = quality
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Произнесите следующий текст: \n0 1 2 3 4 5 6 7 8 9",
                modifier = Modifier
                    .fillMaxWidth(),
                fontSize = 30.sp,
                color = MaterialTheme.colorScheme.onBackground
            )

            Text(
                text = "text: ${voiceQuality?.text}",
                modifier = Modifier
                    .fillMaxWidth(),
                fontSize = 24.sp,
                color = MaterialTheme.colorScheme.onBackground
            )

            Text(
                text = "signalNoise: ${voiceQuality?.signalNoise}",
                modifier = Modifier
                    .fillMaxWidth(),
                fontSize = 24.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        if (voiceHints.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(4.dp)
                    )
            ) {
                voiceHints.forEach { hint ->
                    val textHint = when (hint) {
                        VoiceHint.SPEAK_LOUDER -> stringResource(R.string.speak_louder)
                        VoiceHint.SPEAK_QUIETLY -> stringResource(R.string.speak_quietly)
                        VoiceHint.SPEAK_RUSSIAN -> stringResource(R.string.speak_russian)
                        VoiceHint.MOVE_TO_QUIETER_PLACE -> stringResource(R.string.move_to_quieter_place)
                        VoiceHint.SPEAK_ONLY_NUMBERS -> stringResource(R.string.speak_only_numbers)
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
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                modifier = Modifier
                    .size(80.dp),
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = if (isRecorded)
                        MaterialTheme.colorScheme.secondary
                    else
                        MaterialTheme.colorScheme.primary,
                ),
                onClick = {
                    if (isRecorded) {
                        voiceAnalyzer.stopListening()
                    } else {
                        voiceAnalyzer.startListening()
                    }
                    isRecorded = !isRecorded
                }
            ) {
                Icon(
                    modifier = Modifier.size(30.dp),
                    imageVector = Icons.Default.RecordVoiceOver,
                    tint = White,
                    contentDescription = "Record"
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun RecordVoiceScreenPreview() {
    BiometricAssistTheme {
        RecordVoiceScreen(viewModel())
    }
}
