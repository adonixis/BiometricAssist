package ru.adonixis.biometricassist.sample.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ru.adonixis.biometricassist.sample.ui.screen.MainScreen
import ru.adonixis.biometricassist.sample.ui.screen.TakePhotoScreen
import ru.adonixis.biometricassist.sample.ui.screen.RecordVoiceScreen
import ru.adonixis.biometricassist.sample.ui.screen.Screen

@Composable
fun AppNavHost(
    modifier : Modifier = Modifier
) {
    val navController = rememberNavController()

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = Screen.Main.route
    ) {
        composable(Screen.Main.route) {
            MainScreen(
                onTakePhotoClick = {
                    navController.navigate(Screen.TakePhoto.route)
                },
                onRecordVoiceClick = {
                    navController.navigate(Screen.RecordVoice.route)
                }
            )
        }
        composable(Screen.TakePhoto.route) {
            TakePhotoScreen()
        }
        composable(Screen.RecordVoice.route) {
            RecordVoiceScreen()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppNavHostPreview() {
    AppNavHost()
}
