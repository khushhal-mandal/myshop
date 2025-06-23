package com.example.myshoppinguserapp.presentation.screen.splash

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import com.example.myshoppinguserapp.presentation.navigation.App
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MyShop(auth: FirebaseAuth) {
    var showSplash by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        delay(1000)
        showSplash = false
    }

    if (showSplash) {
        SplashScreen()
    } else {
        App(auth = auth)
    }
}
