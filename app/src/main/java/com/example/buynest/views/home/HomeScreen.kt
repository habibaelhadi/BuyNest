package com.example.buynest.views.home

import android.util.Log
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.buynest.BrandsAndProductsQuery
import com.example.buynest.R
import com.example.buynest.model.state.UiResponseState
import com.example.buynest.utils.NetworkHelper
import com.example.buynest.utils.SharedPrefHelper
import com.example.buynest.viewmodel.currency.CurrencyViewModel
import com.example.buynest.viewmodel.discount.DiscountViewModel
import com.example.buynest.viewmodel.home.HomeViewModel
import com.example.buynest.viewmodel.shared.SharedViewModel
import com.example.buynest.views.component.AdsSection
import com.example.buynest.views.component.ForYouSection
import com.example.buynest.views.component.Indicator
import com.example.buynest.views.component.NoInternetLottie
import com.example.buynest.views.component.SearchBar
import com.example.buynest.views.component.TopBrandsSection
import com.google.android.play.integrity.internal.c
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
    discountViewModel: DiscountViewModel,
    currencyViewModel: CurrencyViewModel
) {
    val activity = LocalActivity.current
    val brands by homeViewModel.brand.collectAsStateWithLifecycle(initialValue = UiResponseState.Loading)
    val offers by discountViewModel.offers.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val rate by currencyViewModel.rate
    val currencySymbol by currencyViewModel.currencySymbol
    val isConnected by NetworkHelper.isConnected.collectAsStateWithLifecycle()

    LaunchedEffect(isConnected) {
        homeViewModel.getBrands(context = context)
        discountViewModel.loadDiscounts()
        currencyViewModel.loadCurrency()
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


        when (val result = brands) {
            is UiResponseState.Error -> {
                Log.i("TAG", "Error lottie: ${result.message}")
                NoInternetLottie()
                return@Column
            }

            UiResponseState.Loading -> {
                Indicator()
            }

            is UiResponseState.Success<*> -> {
                val data = result.data
                if (data is Pair<*, *>) {
                    val brandList = data.first as? List<BrandsAndProductsQuery.Node3>
                    val productList = data.second as? List<BrandsAndProductsQuery.Node>

                    if (brandList.isNullOrEmpty() || productList.isNullOrEmpty()) {
                        Log.e("TAG", "Empty or null brand/product list")
                        NoInternetLottie()
                        return@Column
                    }

                    AdsSection(offers = offers)
                    Spacer(modifier = Modifier.height(24.dp))

                    if (brandList.size >= 16) {
                        TopBrandsSection(
                            items = brandList.dropLast(4),
                            onCategoryClick = onCategoryClick
                        )
                        sharedViewModel.setCategories(brandList.subList(12, 16))
                    } else {
                        Text("Not enough brand data")
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    ForYouSection(
                        items = productList.drop(10),
                        onProductClicked = onProductClicked,
                        rate = rate,
                        currencySymbol = currencySymbol.toString()
                    )
                } else {
                    Log.e("TAG", "Invalid data or null: ${data?.javaClass?.name}")
                    NoInternetLottie()
                }

            }
        }
    }
}












