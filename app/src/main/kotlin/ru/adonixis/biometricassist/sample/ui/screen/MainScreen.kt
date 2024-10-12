package ru.adonixis.biometricassist.sample.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.adonixis.biometricassist.sample.ui.theme.BiometricAssistTheme

@Composable
fun MainScreen(
    onTakePhotoClick: () -> Unit,
    onRecordVoiceClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = onTakePhotoClick,
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .padding(8.dp)
        ) {
            Text(text = "Сделать селфи")
        }
        Button(
            onClick = onRecordVoiceClick,
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .padding(8.dp)
        ) {
            Text(text = "Записать голос")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    BiometricAssistTheme {
        MainScreen(
            onTakePhotoClick = {},
            onRecordVoiceClick = {}
        )
    }
}
