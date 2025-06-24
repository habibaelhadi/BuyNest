package com.example.buynest.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.buynest.views.brandProducts.BrandDetailsScreen
import com.example.buynest.views.categories.CategoriesScreen
import com.example.buynest.views.categories.products.CategoryDetailsScreen
import com.example.buynest.views.favourites.FavouriteScreen
import com.example.buynest.views.home.HomeScreen
import com.example.buynest.views.profile.ProfileScreen
import com.example.buynest.views.settings.SettingsScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SetupNavHost(mainNavController: NavHostController) {
    NavHost(
        navController = mainNavController, startDestination = RoutesScreens.Home.route
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
            HomeScreen(
                onCategoryClick = { categoryName ->
                    mainNavController.navigate(RoutesScreens.CategoryDetails.route.replace("{categoryName}", categoryName))
                }
            )
        }
        composable(RoutesScreens.CategoryDetails.route) { backStackEntry ->
            val categoryName = backStackEntry.arguments?.getString("categoryName")
            if (categoryName != null) {
                CategoryDetailsScreen(categoryName)
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
        composable(RoutesScreens.Settings.route) {
            SettingsScreen()
        }
    }
}