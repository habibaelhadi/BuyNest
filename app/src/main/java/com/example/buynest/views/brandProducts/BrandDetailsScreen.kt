package com.example.buynest.views.brandProducts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.buynest.ProductsByCollectionIDQuery
import com.example.buynest.R
import com.example.buynest.repository.home.HomeRepository
import com.example.buynest.model.state.UiResponseState
import com.example.buynest.repository.favorite.FavoriteRepoImpl
import com.example.buynest.ui.theme.*
import com.example.buynest.viewmodel.favorites.FavouritesViewModel
import com.example.buynest.viewmodel.brandproducts.BrandDetailsViewModel
import com.example.buynest.viewmodel.brandproducts.BrandProductsFactory
import com.example.buynest.views.component.Indicator
import com.example.buynest.views.component.ProductItem
import com.example.buynest.views.component.SearchBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrandDetailsScreen(
    brandID: String,
    categoryName: String,
    onCartClicked: () -> Unit,
    backClicked: () -> Unit,
    onProductClicked: (productId: String) -> Unit,
    onSearchClicked:()->Unit
) {
    val phenomenaFontFamily = FontFamily(Font(R.font.phenomena_bold))

    val brandProductsViewModel: BrandDetailsViewModel = viewModel(
        factory = BrandProductsFactory(HomeRepository())
    )
    val brandProducts by brandProductsViewModel.brandProducts.collectAsStateWithLifecycle()
    val favViewModel: FavouritesViewModel = viewModel(
        factory = FavouritesViewModel.FavouritesFactory(FavoriteRepoImpl())
    )

    val filterExpanded = remember { mutableStateOf(false) }

    LaunchedEffect(brandID) {
        val id = "gid://shopify/Collection/$brandID"
        brandProductsViewModel.getBrandProducts(id)
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        CenterAlignedTopAppBar(
            title = {
                Text(
                    categoryName, fontSize = 20.sp,
                    fontFamily = phenomenaFontFamily,
                    color = MainColor,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            },
            navigationIcon = {
                IconButton(onClick = backClicked) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null,
                        tint = MainColor
                    )
                }
            },
            actions = {
                IconButton(onClick = { filterExpanded.value = !filterExpanded.value }) {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = "Filter",
                        tint = MainColor
                    )
                }
            }
        )

        SearchBar(onCartClicked = onCartClicked, onSearchClicked = onSearchClicked)

        Spacer(modifier = Modifier.height(24.dp))

        when (val result = brandProducts) {
            is UiResponseState.Error -> {
                Text(text = result.message)
            }

            UiResponseState.Loading -> {
                Indicator()
            }
            is UiResponseState.Success<*> -> {
                val data = result.data as? ProductsByCollectionIDQuery.Data
                val productList = data?.collection?.products?.edges?.map { it.node }
                val prices = productList?.mapNotNull {
                    it.variants.edges.firstOrNull()?.node?.price?.amount?.toString()?.toFloatOrNull()
                }

                val minPrice = prices?.minOrNull() ?: 0f
                val maxPrice = prices?.maxOrNull() ?: 1000f

                val selectedPrice = remember { mutableStateOf(maxPrice) }

                if (filterExpanded.value) {
                    Text(
                        text = "Max Price: ${selectedPrice.value.toInt()}",
                        color = MainColor,
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
                    )

                    Slider(
                        value = selectedPrice.value,
                        onValueChange = { selectedPrice.value = it },
                        valueRange = minPrice..maxPrice,
                        steps = 4,
                        modifier = Modifier.padding(horizontal = 8.dp),
                        colors = SliderDefaults.colors(
                            thumbColor = MainColor,
                            activeTrackColor = MainColor,
                            inactiveTrackColor = Color.LightGray
                        )
                    )
                }

                val filteredProducts = productList?.filter {
                    val price = it.variants.edges.firstOrNull()?.node?.price?.amount?.toString()?.toFloatOrNull()
                    price != null && price <= selectedPrice.value
                }

                Spacer(modifier = Modifier.height(8.dp))

                ProductGrid(
                    onProductClicked = onProductClicked,
                    bradProduct = filteredProducts,
                    favViewModel = favViewModel
                )
            }
        }
    }
}




@Composable
fun ProductGrid(
    onProductClicked: (productId: String) -> Unit,
    bradProduct: List<ProductsByCollectionIDQuery.Node>? = null,
    favViewModel: FavouritesViewModel
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ){
        items(bradProduct?.size ?: 0) {
            ProductItem(onProductClicked, bradProduct?.get(it),favViewModel)
        }
    }
}



