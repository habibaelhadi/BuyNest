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
    data object CategoryDetails : RoutesScreens("categoryDetails/{categoryName}", 0)
}