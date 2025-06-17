package com.example.buynest.navigation

import com.example.buynest.R
import kotlinx.serialization.Serializable

@Serializable
sealed class RoutesScreens (val route: String,  val icon: Int){

    @Serializable
    data object Home : RoutesScreens("home", R.drawable.baseline_home_24)
    data object Favourite : RoutesScreens("favourite", R.drawable.baseline_favorite_24)
    data object Categories : RoutesScreens("categories", R.drawable.baseline_category_24)
    data object Profile : RoutesScreens("profile", R.drawable.baseline_person_24)
    data object BrandDetails : RoutesScreens("categoryDetails/{categoryName}", 0)
    data object Settings : RoutesScreens("settings", R.drawable.baseline_settings_24)
    data object Login : RoutesScreens("login", 0)
    data object SignUp : RoutesScreens("signup", 0)
    data object Card : RoutesScreens("card", 0)
    data object OrdersHistory : RoutesScreens("ordersHistory", 0)
    data object OrderDetails : RoutesScreens("orderDetails",0)
}