package com.example.buynest.navigation

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.buynest.utils.SharedPrefHelper
import com.example.buynest.views.address.AddressScreen
import com.example.buynest.views.authentication.forgotpassword.ForgotPasswordScreen
import com.example.buynest.views.authentication.login.LoginScreen
import com.example.buynest.views.authentication.signup.SignUpScreen
import com.example.buynest.views.categories.CategoriesScreen
import com.example.buynest.views.brandProducts.BrandDetailsScreen
import com.example.buynest.views.cart.CartScreen
import com.example.buynest.views.favourites.FavouriteScreen
import com.example.buynest.views.home.HomeScreen
import com.example.buynest.views.map.MapScreen
import com.example.buynest.views.map.MapSearchScreen
import com.example.buynest.views.orderdetails.OrderDetailsScreen
import com.example.buynest.views.orders.OrdersHistoryScreen
import com.example.buynest.views.productInfo.ProductInfoScreen
import com.example.buynest.views.profile.ProfileScreen
import com.example.buynest.views.settings.SettingsScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SetupNavHost(mainNavController: NavHostController) {
    val context = LocalContext.current
    val isLoggedIn = SharedPrefHelper.getLogIn(context)
    val startDestination = if (isLoggedIn) RoutesScreens.Home.route else RoutesScreens.Login.route
    NavHost(
        navController = mainNavController, startDestination = startDestination
    ) {
        composable(RoutesScreens.Home.route) {
            HomeScreen(
                onCategoryClick = { categoryName,brandId ->
                    mainNavController.navigate(
                        RoutesScreens.BrandDetails.route
                            .replace("{categoryName}", categoryName)
                            .replace("{brandID}", brandId)
                    )
                },
                onCardClicked = {
                    mainNavController.navigate(RoutesScreens.Cart.route)
                }
            )
        }

        composable(RoutesScreens.BrandDetails.route) { backStackEntry ->
            val categoryName = backStackEntry.arguments?.getString("categoryName")
            val brandId = backStackEntry.arguments?.getString("brandID").toString()
            if (categoryName != null) {
                BrandDetailsScreen(brandID = brandId ,categoryName = categoryName,
                    onCartClicked = {
                        mainNavController.navigate(RoutesScreens.Cart.route)
                    },
                    backClicked = {
                        mainNavController.popBackStack()
                    },
                    onProductClicked = { productId ->
                        mainNavController.navigate(RoutesScreens.ProductInfo.route
                            .replace("{productId}", productId))
                    }
                )
            }
        }
        composable(RoutesScreens.Favourite.route) {
            FavouriteScreen(
                onCartClicked = {
                    mainNavController.navigate(RoutesScreens.Cart.route)
                }
            )
        }
        composable(RoutesScreens.Categories.route) {
            CategoriesScreen(onCartClicked = {
                mainNavController.navigate(RoutesScreens.Cart.route)
            },
                onProductClicked = {
                    mainNavController.navigate(RoutesScreens.ProductInfo.route)
                }
            )
        }
        composable(RoutesScreens.Profile.route) {
            ProfileScreen(
                onBackClicked = {
                    mainNavController.popBackStack()
                }
            )
        }
        composable(RoutesScreens.Login.route) {
            LoginScreen(navigateToHome = {
                mainNavController.navigate(RoutesScreens.Home.route) {
                    popUpTo(RoutesScreens.Login.route) {
                        inclusive = true
                    }
                }
            }, navigateToSignUp = {
                mainNavController.navigate(RoutesScreens.SignUp.route)
            }, navigateToForgotPassword = {
                mainNavController.navigate(RoutesScreens.ForgotPassword.route)
            })
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
                },
                gotoAddressScreen = {
                    mainNavController.navigate(RoutesScreens.Address.route)
                }
            )
        }
        composable(RoutesScreens.Address.route) {
            AddressScreen(
                onBackClicked = {
                    mainNavController.popBackStack()
                },
                onMapClicked = {
                    mainNavController.navigate(RoutesScreens.Map.route)
                }
            )
        }
        composable(RoutesScreens.Cart.route) {
            CartScreen(
                onBackClicked = {
                    mainNavController.popBackStack()
                }
            )
        }
        composable(RoutesScreens.ForgotPassword.route) {
            ForgotPasswordScreen(
                onBackToLogin = {
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
        composable(
            route = RoutesScreens.ProductInfo.route,
            ){ backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId")
            ProductInfoScreen(
                backClicked = {
                    mainNavController.popBackStack()
                },
                navigateToCart = {
                    mainNavController.navigate(RoutesScreens.Cart.route)
                },
                productId = productId ?: ""
            )
        }
        composable(RoutesScreens.Map.route) {
            MapScreen(
                backClicked = {
                    mainNavController.popBackStack()
                },
                onMapSearchClicked = {
                    mainNavController.navigate(RoutesScreens.MapSearch.route)
                }
            )
        }
        composable(RoutesScreens.MapSearch.route) {
            MapSearchScreen(
                onBack = {
                    mainNavController.popBackStack()
                },
                onPlaceSelected = { geoPoint, name ->
                    Log.d("MapSearchScreen", "Selected place: $name, $geoPoint")
                    mainNavController.popBackStack()
                }
            )
        }
    }
}