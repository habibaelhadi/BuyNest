package com.example.buynest

import androidx.annotation.DrawableRes
import com.example.buynest.navigation.RoutesScreens

data class ScreenMenuItem(
    val screen: RoutesScreens,
    @DrawableRes val icon: Int,
    @DrawableRes val selectedIcon: Int,
    val id: Int
){
    companion object{
        val menuItems = listOf(
            ScreenMenuItem(RoutesScreens.Home, RoutesScreens.Home.icon, R.drawable.ic_baseline_home_avd, 1),
            ScreenMenuItem(RoutesScreens.Categories, RoutesScreens.Categories.icon, R.drawable.baseline_category_avd_24, 3),
            ScreenMenuItem(RoutesScreens.Favourite, RoutesScreens.Favourite.icon, R.drawable.baseline_favorite_avd_24, 2),
            ScreenMenuItem(RoutesScreens.Profile, RoutesScreens.Profile.icon, R.drawable.baseline_person_avd_24, 4)
        )

    }
}