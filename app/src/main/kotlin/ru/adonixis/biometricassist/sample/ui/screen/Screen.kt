package ru.adonixis.biometricassist.sample.ui.screen

sealed class Screen(val route: String) {
    data object Main : Screen("main")
    data object TakePhoto : Screen("takePhoto")
    data object RecordVoice : Screen("recordVoice")
}
