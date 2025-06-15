package com.example.buynest.navigation

import com.example.buynest.R
import kotlinx.serialization.Serializable

@Serializable
sealed class RoutesScreens (val route: String,  val icon: Int){

    @Serializable
    object Home : RoutesScreens("home", R.drawable.baseline_home_24)
    object Favourite : RoutesScreens("favourite", R.drawable.baseline_favorite_24)
    object Categories : RoutesScreens("categories", R.drawable.baseline_category_24)
    object Profile : RoutesScreens("profile", R.drawable.baseline_person_24)
    object Login : RoutesScreens("login", 0)
    object SignUp : RoutesScreens("signup", 0)
}