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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.buynest.BrandsAndProductsQuery
import com.example.buynest.R
import com.example.buynest.repository.homeRepository.HomeRepository
import com.example.buynest.model.uistate.ResponseState
import com.example.buynest.viewmodel.home.HomeFactory
import com.example.buynest.viewmodel.home.HomeViewModel
import com.example.buynest.viewmodel.shared.SharedViewModel
import com.example.buynest.views.component.AdsSection
import com.example.buynest.views.component.ForYouSection
import com.example.buynest.views.component.Indicator
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


val phenomenaBold = FontFamily(
    Font(R.font.phenomena_bold)
)
@Composable
fun HomeScreen(onCategoryClick: (String,String) -> Unit ,
               onCardClicked:()->Unit,
               sharedViewModel: SharedViewModel,
               onProductClicked: (productId: String) -> Unit
) {
    val activity = LocalActivity.current
    val homeViewModel: HomeViewModel = viewModel(
        factory = HomeFactory(HomeRepository())
    )
    val brands by homeViewModel.brand.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        homeViewModel.getBrands()
    }

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
            onCartClicked = onCardClicked
        )
        Spacer(modifier = Modifier.height(24.dp))
        AdsSection(offers = offers)
        Spacer(modifier = Modifier.height(24.dp))
        when (val result = brands) {
            is ResponseState.Error -> {
                Text(text = result.message)
            }
            ResponseState.Loading -> {
                Indicator()
            }
            is ResponseState.Success<*> -> {
                val (brandList, productList) = result.data as Pair<List<BrandsAndProductsQuery.Node3>, List<BrandsAndProductsQuery.Node>>
                TopBrandsSection(items = brandList.dropLast(4), onCategoryClick = onCategoryClick)
                sharedViewModel.setCategories(brandList.subList(12,16))
                Spacer(modifier = Modifier.height(24.dp))
                ForYouSection(items = productList.drop(10),onProductClicked)
            }
        }
    }
}












