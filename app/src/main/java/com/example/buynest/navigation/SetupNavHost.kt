package com.example.buynest.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.buynest.utils.sharedPreferences.SharedPreferencesImpl
import com.example.buynest.views.authentication.login.LoginScreen
import com.example.buynest.views.authentication.signup.SignUpScreen
import com.example.buynest.views.categories.CategoriesScreen
import com.example.buynest.views.brandProducts.BrandDetailsScreen
import com.example.buynest.views.favourites.FavouriteScreen
import com.example.buynest.views.home.HomeScreen
import com.example.buynest.views.profile.ProfileScreen
import com.example.buynest.views.settings.SettingsScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SetupNavHost(mainNavController: NavHostController) {
    val context = LocalContext.current
    val isLoggedIn = SharedPreferencesImpl.getLogIn(context)
    val startDestination = if (isLoggedIn) RoutesScreens.Home.route else RoutesScreens.Login.route
    NavHost(
        navController = mainNavController, startDestination = startDestination
    ) {
        composable(RoutesScreens.Home.route) {
            HomeScreen(
                onCategoryClick = { categoryName ->
                    mainNavController.navigate(RoutesScreens.CategoryDetails.route.replace("{categoryName}", categoryName))
                }
            )
        }
        composable(RoutesScreens.CategoryDetails.route) { backStackEntry ->
            val categoryName = backStackEntry.arguments?.getString("categoryName")
            if (categoryName != null) {
                BrandDetailsScreen(categoryName)
            }
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
        composable(RoutesScreens.Login.route) {
            LoginScreen(navigateToHome = {
                mainNavController.navigate(RoutesScreens.Home.route) {
                    popUpTo(RoutesScreens.Login.route) {
                        inclusive = true
                    }
                }
            }){
                mainNavController.navigate(RoutesScreens.SignUp.route)
            }
        }
        composable(RoutesScreens.SignUp.route) {
            SignUpScreen{
                mainNavController.popBackStack()
            }
        }
        composable(RoutesScreens.Settings.route) {
            SettingsScreen()
        }
    }
}