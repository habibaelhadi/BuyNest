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
import com.example.buynest.BrandsAndProductsQuery
import com.example.buynest.R
import com.example.buynest.model.entity.OfferModel
import com.example.buynest.model.state.UiResponseState
import com.example.buynest.viewmodel.discount.DiscountViewModel
import com.example.buynest.viewmodel.home.HomeViewModel
import com.example.buynest.viewmodel.shared.SharedViewModel
import com.example.buynest.views.component.AdsSection
import com.example.buynest.views.component.ForYouSection
import com.example.buynest.views.component.Indicator
import com.example.buynest.views.component.SearchBar
import com.example.buynest.views.component.TopBrandsSection
import org.koin.androidx.compose.koinViewModel

val phenomenaBold = FontFamily(
    Font(R.font.phenomena_bold)
)

@Composable
fun HomeScreen(
    onCategoryClick: (String,String) -> Unit,
    onCardClicked:()->Unit,
    onSearchClicked:()->Unit,
    sharedViewModel: SharedViewModel,
    onProductClicked: (productId: String) -> Unit,
    homeViewModel: HomeViewModel = koinViewModel() ,
    discountViewModel: DiscountViewModel
) {
    val activity = LocalActivity.current
    val brands by homeViewModel.brand.collectAsStateWithLifecycle()
    val offers by discountViewModel.offers.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        homeViewModel.getBrands()
        discountViewModel.loadDiscounts()
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
            onCartClicked = onCardClicked,
            onSearchClicked = onSearchClicked
        )
        Spacer(modifier = Modifier.height(24.dp))
        AdsSection(offers = offers)
        Spacer(modifier = Modifier.height(24.dp))
        when (val result = brands) {
            is UiResponseState.Error -> {
                Text(text = result.message)
            }
            UiResponseState.Loading -> {
                Indicator()
            }
            is UiResponseState.Success<*> -> {
                val (brandList, productList) = result.data as Pair<List<BrandsAndProductsQuery.Node3>, List<BrandsAndProductsQuery.Node>>
                TopBrandsSection(items = brandList.dropLast(4), onCategoryClick = onCategoryClick)
                sharedViewModel.setCategories(brandList.subList(12,16))
                Spacer(modifier = Modifier.height(24.dp))
                ForYouSection(items = productList.drop(10),onProductClicked)
            }
        }
    }
}












