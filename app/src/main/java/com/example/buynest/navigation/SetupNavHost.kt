package com.example.buynest.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.buynest.views.home.CategoriesScreen
import com.example.buynest.views.home.FavouriteScreen
import com.example.buynest.views.home.HomeScreen
import com.example.buynest.views.home.ProfileScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SetupNavHost(mainNavController: NavHostController) {
    NavHost(
        navController = mainNavController, startDestination = RoutesScreens.Home.route
    ) {
        composable(RoutesScreens.Home.route) {
            HomeScreen()
        }
        composable(RoutesScreens.Favourite.route) {
            FavouriteScreen()
        }
        composable(RoutesScreens.Categories.route) {
            CategoriesScreen()
        }
        composable(RoutesScreens.Profile.route) {
            ProfileScreen()
        }
    }
}