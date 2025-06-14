package com.example.buynest

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.buynest.ui.theme.primaryColor
import com.example.buynest.ui.theme.white
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var showSplash by remember { mutableStateOf(true) }
            Surface(
                modifier = Modifier.fillMaxSize()
            ) {
                if (showSplash) {
                    SplashScreen {
                        showSplash = false
                    }
                } else {
                    ScaffoldSample()
                }
            }
        }
    }

    @Composable
    fun SplashScreen(
        onAnimationComplete: () -> Unit
    ) {
        val phenomenaFontFamily = FontFamily(
            Font(R.font.phenomena_bold)
        )

        val composition by rememberLottieComposition(
            spec = LottieCompositionSpec.RawRes(R.raw.splash)
        )

        val progress by animateLottieCompositionAsState(
            composition = composition,
            iterations = LottieConstants.IterateForever
        )

        LaunchedEffect(Unit) {
            delay(5000)
            onAnimationComplete()
        }

        Column (
            modifier = Modifier
                .fillMaxSize()
                .background(primaryColor),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            LottieAnimation(
                composition = composition,
                progress = { progress },
                modifier = Modifier.height(300.dp)
                    .padding(bottom = 0.dp)
            )

            Text(
                text = "BuyNest",
                fontFamily = phenomenaFontFamily,
                fontSize = 62.sp,
                color = white
            )
        }
    }

    @Composable
    fun ScaffoldSample(){
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Hello World!")
        }
    }
}

