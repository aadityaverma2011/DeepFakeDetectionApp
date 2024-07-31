package com.aadityaverma.deepfakedetection.presentation.navgraph

sealed class Screen(val route: String) {
    object Home : Screen("home")
}