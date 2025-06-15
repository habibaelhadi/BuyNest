package com.example.buynest

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.buynest.ui.theme.BuyNestTheme
import com.example.buynest.ui.theme.*
import np.com.susanthapa.curved_bottom_navigation.CbnMenuItem
import np.com.susanthapa.curved_bottom_navigation.CurvedBottomNavigationView
import com.example.buynest.navigation.SetupNavHost
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.buynest.ui.theme.white
import com.example.buynest.views.authentication.login.LoginScreen
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
                hideSystemUI()
                if (showSplash) {
                    SplashScreen {
                        showSplash = false
                    }
                } else {
                    BuyNestStart()
                    //LoginScreen()
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    fun BuyNestStart() {
        val navController = rememberNavController()
        Scaffold(
            bottomBar = { CurvedNavBar(navController = navController) }
        ) { innerPadding ->
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)

            ) {
                SetupNavHost(mainNavController = navController)
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MainColor),
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
    fun CurvedNavBar(navController: NavHostController) {
        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .background(White),
            factory = { context ->
                CurvedBottomNavigationView(context).apply {

                    unSelectedColor = White.toArgb()
                    selectedColor = MainColor.toArgb()
                    navBackgroundColor = MainColor.toArgb()

                    val cbnMenuItems = ScreenMenuItem.menuItems.map { screen ->
                        CbnMenuItem(
                            icon = screen.icon,
                            avdIcon = screen.selectedIcon,
                            destinationId = screen.id
                        )
                    }
                    layoutDirection = View.LAYOUT_DIRECTION_LTR
                    setMenuItems(cbnMenuItems.toTypedArray(), 0)
                    setOnMenuItemClickListener { cbnMenuItem, i ->
                        navController.popBackStack()
                        navController.navigate(ScreenMenuItem.menuItems[i].screen.route)
                    }
                }
            }
        )
    }

    fun hideSystemUI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.let {
                it.hide(WindowInsets.Type.navigationBars()) // Hide only navigation bar
                it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    )
        }
        window.navigationBarColor = White.toArgb()
    }
}





