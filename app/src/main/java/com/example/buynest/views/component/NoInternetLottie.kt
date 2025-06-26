package com.example.buynest.views.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.buynest.R
import com.example.buynest.ui.theme.MainColor
import com.example.buynest.ui.theme.white
import com.example.buynest.views.home.phenomenaBold

@Composable
fun NoInternetLottie(){
    val composition by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(R.raw.no_internet)
    )
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(white),
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
                text = "No Internet Connection",
                fontFamily = phenomenaBold,
                fontSize = 20.sp,
                color = MainColor
            )
    }
}

