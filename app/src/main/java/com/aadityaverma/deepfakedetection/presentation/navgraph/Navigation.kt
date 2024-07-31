package com.aadityaverma.deepfakedetection.presentation.navgraph

import com.aadityaverma.deepfakedetection.di.AppModule
import com.aadityaverma.deepfakedetection.presentation.components.DetectScreen



import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun Navigation() {
    val navController = rememberNavController()
    var selectedItem = remember { mutableStateOf(0) }

    Scaffold(

    ) {
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            Modifier.fillMaxSize()
        ) {
            composable(Screen.Home.route) {


                var apiService= AppModule.provideDataApi(AppModule.provideRetrofit(AppModule.provideOkHttpClient()))
                DetectScreen(navController= navController,apiService = apiService)
            }

        }
    }
}