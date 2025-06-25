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
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.buynest.ui.theme.*
import np.com.susanthapa.curved_bottom_navigation.CbnMenuItem
import np.com.susanthapa.curved_bottom_navigation.CurvedBottomNavigationView
import com.example.buynest.navigation.SetupNavHost
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import androidx.lifecycle.MutableLiveData
import androidx.navigation.compose.currentBackStackEntryAsState
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.buynest.model.data.remote.graphql.ApolloClient
import com.example.buynest.navigation.RoutesScreens
import com.example.buynest.repository.cart.CartRepositoryImpl
import com.example.buynest.repository.cart.datasource.CartDataSourceImpl
import com.example.buynest.ui.theme.white
import com.example.buynest.utils.SecureSharedPrefHelper
import com.example.buynest.utils.SharedPrefHelper
import com.example.buynest.utils.constant.*
import com.example.buynest.viewmodel.cart.CartManager
import kotlinx.coroutines.delay

val routIndex = MutableLiveData<Int>(0)
class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val apolloClient = ApolloClient.createApollo(
            BASE_URL = CLIENT_BASE_URL,
            ACCESS_TOKEN = BuildConfig.SHOPIFY_ACCESS_TOKEN,
            Header = CLIENT_HEADER
        )

        CartManager.setup(CartRepositoryImpl(CartDataSourceImpl(apolloClient)))

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
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    fun BuyNestStart() {
        val navController = rememberNavController()
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        val screensWithBottomBar = listOf(
            RoutesScreens.Home.route,
            RoutesScreens.Categories.route,
            RoutesScreens.Favourite.route,
            RoutesScreens.Settings.route
        )

        Scaffold(
            bottomBar = {
                if (currentRoute in screensWithBottomBar) {
                    CurvedNavBar(navController = navController)
                }
            }
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
        val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
        val currentIndex = ScreenMenuItem.menuItems.indexOfFirst { it.screen.route == currentRoute }
        val initialIndex = if (currentIndex != -1) currentIndex else 0

        androidx.compose.runtime.key(initialIndex) {
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
                        setMenuItems(cbnMenuItems.toTypedArray(), initialIndex)

                        setOnMenuItemClickListener { _, i ->
                            navController.popBackStack()
                            navController.navigate(ScreenMenuItem.menuItems[i].screen.route)
                        }
                    }
                }
            )
        }
    }


    private fun hideSystemUI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
            window.insetsController?.let {
                it.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    )
        }

        window.statusBarColor = Color.Transparent.toArgb()
        window.navigationBarColor = Color.Transparent.toArgb()
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    override fun onResume() {
        super.onResume()
        hideSystemUI()
    }
}





