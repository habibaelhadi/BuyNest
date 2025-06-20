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
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import com.example.buynest.repository.homeRepository.HomeRepository
import com.example.buynest.model.uistate.ResponseState
import com.example.buynest.repository.favoriteRepo.FavoriteRepoImpl
import com.example.buynest.ui.theme.*
import com.example.buynest.viewmodel.favorites.FavouritesViewModel
import com.example.buynest.viewmodel.brandproducts.BrandDetailsViewModel
import com.example.buynest.viewmodel.brandproducts.BrandProductsFactory
import com.example.buynest.views.component.Indicator
import com.example.buynest.views.component.ProductItem
import com.example.buynest.views.component.SearchBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrandDetailsScreen(brandID:String,categoryName: String,onCartClicked:()->Unit,backClicked:()->Unit,onProductClicked:()->Unit) {
    val phenomenaFontFamily = FontFamily(
        Font(R.font.phenomena_bold)
    )

    val brandProductsViewModel: BrandDetailsViewModel = viewModel(
        factory = BrandProductsFactory(HomeRepository())
    )
    val brandProducts by brandProductsViewModel.brandProducts.collectAsStateWithLifecycle()
    val favViewModel: FavouritesViewModel = viewModel(
        factory = FavouritesViewModel.FavouritesFactory(FavoriteRepoImpl())
    )

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
                IconButton(onClick = {
                    backClicked()
                }) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null,
                        tint = MainColor
                    )
                }
            }
        )
        SearchBar(
            onCartClicked = onCartClicked
        )
        Spacer(modifier = Modifier.height(24.dp))
        when (val result = brandProducts) {
            is ResponseState.Error -> {
                Text(text = result.message)
            }
            ResponseState.Loading -> {
                Indicator()
            }
            is ResponseState.Success<*> -> {
                val data = result.data as? ProductsByCollectionIDQuery.Data
                val productList = data?.collection?.products?.edges?.map { it.node }
                ProductGrid( onProductClicked, productList,favViewModel)
            }
        }
    }
}


@Composable
fun ProductGrid(
    onProductClicked: () -> Unit,
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



