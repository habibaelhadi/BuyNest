package com.example.buynest.navigation

import android.os.Build
import androidx.annotation.RequiresApi
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
import com.example.buynest.views.cart.CartScreen
import com.example.buynest.views.favourites.FavouriteScreen
import com.example.buynest.views.home.HomeScreen
import com.example.buynest.views.orderdetails.OrderDetailsScreen
import com.example.buynest.views.orders.OrdersHistoryScreen
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
                    mainNavController.navigate(RoutesScreens.BrandDetails.route.replace("{categoryName}", categoryName))
                },
                onCardClicked = {
                    mainNavController.navigate(RoutesScreens.Card.route)
                }
            )
        }

        composable(RoutesScreens.BrandDetails.route) { backStackEntry ->
            val categoryName = backStackEntry.arguments?.getString("categoryName")
            if (categoryName != null) {
                BrandDetailsScreen(categoryName,
                    onCardClicked = {
                        mainNavController.navigate(RoutesScreens.Card.route)
                    },
                    backClicked = {
                        mainNavController.popBackStack()
                    }
                )
            }
        }
        composable(RoutesScreens.Favourite.route) {
            FavouriteScreen()
        }
        composable(RoutesScreens.Categories.route) {
            CategoriesScreen(onCardClicked = {
                mainNavController.navigate(RoutesScreens.Card.route)
            }
            )
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
            SettingsScreen(
                gotoProfileScreen = {
                    mainNavController.navigate(RoutesScreens.Profile.route)
                },
                gotoOrdersHistoryScreen = {
                    mainNavController.navigate(RoutesScreens.OrdersHistory.route)
                }
            )
        }
        composable(RoutesScreens.Card.route) {
            CartScreen(
                onBackClicked = {
                    mainNavController.popBackStack()
                }
            )
        }
        composable(RoutesScreens.OrdersHistory.route) {
            OrdersHistoryScreen(
                backClicked = {
                    mainNavController.popBackStack()
                },
                gotoOrderDetails ={
                    mainNavController.navigate(RoutesScreens.OrderDetails.route)
                }
            )
        }
        composable(RoutesScreens.OrderDetails.route) {
            OrderDetailsScreen(
                backClicked = {
                    mainNavController.popBackStack()
                }
            )
        }
    }
}