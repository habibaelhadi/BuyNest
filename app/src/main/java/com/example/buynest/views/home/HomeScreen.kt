package com.example.buynest.views.home

import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.example.buynest.R
import com.example.buynest.views.component.AdsSection
import com.example.buynest.views.component.ForYouSection
import com.example.buynest.views.component.SearchBar
import com.example.buynest.views.component.TopBrandsSection


data class OfferModel(
    val title: String,
    val subtitle: String,
    val buttonText: String,
    val imageRes: Int
)
val offers = listOf(
    OfferModel(
        title = "UP TO 25% OFF",
        subtitle = "For all Headphones & AirPods",
        buttonText = "Shop Now",
        imageRes = R.drawable.ad_background
    ),
    OfferModel(
        title = "Buy 1 Get 1 Free",
        subtitle = "Selected fashion items",
        buttonText = "Shop Now",
        imageRes = R.drawable.ad_background
    ),
    OfferModel(
        title = "Big Summer Sale",
        subtitle = "Electronics & more",
        buttonText = "Shop Now",
        imageRes = R.drawable.ad_background
    )
)
data class ItemModel(
    val name: String,
    val imageRes: Int
)
val categoriesList = listOf(
    ItemModel("Women's fashion", R.drawable.cat_test),
    ItemModel("Men's fashion", R.drawable.cat_test),
    ItemModel("Laptops & Electronics", R.drawable.cat_test),
    ItemModel("Baby Toys", R.drawable.cat_test),
    ItemModel("Beauty", R.drawable.cat_test),
    ItemModel("Headphones", R.drawable.cat_test),
    ItemModel("Skincare", R.drawable.cat_test),
    ItemModel("Camera", R.drawable.cat_test)
)
val brandsList = listOf(
    ItemModel("Samsung", R.drawable.cat_test),
    ItemModel("Apple", R.drawable.cat_test),
    ItemModel("Sony", R.drawable.cat_test),
    ItemModel("LG", R.drawable.cat_test),
    ItemModel("Huawei", R.drawable.cat_test),
    ItemModel("Lenovo", R.drawable.cat_test)
)

val phenomenaBold = FontFamily(
    Font(R.font.phenomena_bold)
)
@Composable
fun HomeScreen(onCategoryClick: (String) -> Unit ,onCardClicked:()->Unit, onSearchClicked: () -> Unit) {
    val activity = LocalActivity.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        BackHandler(enabled = true) {
            activity?.finishAffinity()
        }
        SearchBar(
            onCartClicked = onCardClicked,
            onSearchClicked = onSearchClicked
        )
        Spacer(modifier = Modifier.height(24.dp))
        AdsSection(offers = offers)
        Spacer(modifier = Modifier.height(24.dp))
        TopBrandsSection(items = categoriesList,
            onCategoryClick = onCategoryClick)
        Spacer(modifier = Modifier.height(24.dp))
        ForYouSection( items = brandsList)
    }
}












