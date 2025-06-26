package com.example.buynest.views.favourites

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.buynest.ProductsDetailsByIDsQuery
import com.example.buynest.model.state.UiResponseState
import com.example.buynest.R
import com.example.buynest.repository.FirebaseAuthObject
import com.example.buynest.ui.theme.LightGray2
import com.example.buynest.ui.theme.MainColor
import com.example.buynest.ui.theme.white
import com.example.buynest.utils.NetworkHelper
import com.example.buynest.viewmodel.currency.CurrencyViewModel
import com.example.buynest.viewmodel.favorites.FavouritesViewModel
import com.example.buynest.views.component.Indicator
import com.example.buynest.views.component.NoInternetLottie
import com.example.buynest.views.component.SearchBar
import com.example.buynest.views.orders.phenomenaFontFamily
import org.koin.androidx.compose.koinViewModel

@Composable
fun FavouriteScreen(
    onCartClicked:()->Unit,
    navigateToProductInfo: (String) -> Unit,
    onSearchClicked:()->Unit,
    viewModel: FavouritesViewModel = koinViewModel(),
    currencyViewModel: CurrencyViewModel
) {
    val product by viewModel.productDetails.collectAsStateWithLifecycle()
    val user = FirebaseAuthObject.getAuth().currentUser
    val rate by currencyViewModel.rate
    val currencySymbol by currencyViewModel.currencySymbol

    LaunchedEffect(Unit) {
        currencyViewModel.loadCurrency()
    }
    val isConnected by NetworkHelper.isConnected.collectAsStateWithLifecycle()

    LaunchedEffect(product,isConnected) {
        if (user != null){
            viewModel.getAllFavorites()
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(white)
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        SearchBar(
            onCartClicked = onCartClicked,
            onSearchClicked = onSearchClicked
        )
        Spacer(modifier = Modifier.height(24.dp))
        if (user == null){
            NoDataLottie(true)
        }else{
            when (val result = product){
                is UiResponseState.Error -> {
                    NoInternetLottie()
                }
                UiResponseState.Loading -> {
                    Indicator()
                }
                is UiResponseState.Success<*> -> {
                    val data = result.data as? ProductsDetailsByIDsQuery.Data
                    if (data == null) {
                        NoInternetLottie()
                    } else {
                        val productList = data?.nodes
                        if (productList != null) {
                            if (productList.isEmpty())
                                NoDataLottie(false)
                            else {
                                Favourites(productList, viewModel, navigateToProductInfo,rate,currencySymbol.toString())
                            }
                        }
                    }
                }
            }
        }

    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Favourites(
    productList: List<ProductsDetailsByIDsQuery.Node?>?,
    viewModel: FavouritesViewModel,
    navigateToProductInfo: (String) -> Unit,
    rate: Double,
    currencySymbol: String
) {
    var itemToDelete by remember { mutableStateOf<ProductsDetailsByIDsQuery.Node?>(null) }
    var showConfirmDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(productList?.size ?: 0) { index ->
            val item = productList?.get(index)
            if (item == null) return@items

            val dismissState = rememberDismissState(
                confirmStateChange = { dismissValue ->
                    if (dismissValue == DismissValue.DismissedToStart || dismissValue == DismissValue.DismissedToEnd) {
                        itemToDelete = item
                        showConfirmDialog = true
                        false
                    } else true
                }
            )

            SwipeToDismiss(
                state = dismissState,
                directions = setOf(DismissDirection.StartToEnd, DismissDirection.EndToStart),
                background = {
                    val alignment = when (dismissState.dismissDirection) {
                        DismissDirection.StartToEnd -> Alignment.CenterStart
                        DismissDirection.EndToStart -> Alignment.CenterEnd
                        null -> Alignment.CenterEnd
                    }

                    val isSwiping = dismissState.dismissDirection != null

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Transparent)
                            .padding(horizontal = 20.dp),
                        contentAlignment = alignment
                    ) {
                        Card(
                            modifier = Modifier.fillMaxSize(),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, LightGray2),
                            backgroundColor = if (isSwiping) Color.Red else Color.LightGray
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 20.dp, vertical = 20.dp),
                                contentAlignment = alignment
                            ) {
                                Text(
                                    text = "Delete",
                                    color = Color.White,
                                    fontSize = 20.sp
                                )
                            }
                        }
                    }
                },
                dismissContent = {
                    FavouriteCard(
                        item = item,
                        onDelete = { id ->
                            itemToDelete = item
                            showConfirmDialog = true
                        },
                        viewModel,
                        navigateToProductInfo,
                        rate,
                        currencySymbol
                    )
                },
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    if (showConfirmDialog && itemToDelete != null) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("Confirm Deletion") },
            text = { Text("Are you sure you want to delete '${itemToDelete?.__typename}' from favourites?") },
            confirmButton = {
                TextButton(onClick = {
                    itemToDelete?.onProduct?.id?.let { id ->
                        viewModel.removeFromFavorite(id)
                        viewModel.getAllFavorites()
                    }
                    showConfirmDialog = false
                    itemToDelete = null
                }) {
                    Text("Delete", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showConfirmDialog = false
                    itemToDelete = null
                }) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        )
    }
}

@Composable
fun NoDataLottie(isGuest: Boolean){
    val composition by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(R.raw.no_data)
    )
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(white),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = Modifier.height(300.dp)
                .padding(bottom = 0.dp)
        )
        if (isGuest){
            Text(
                text = "Register to add",
                fontFamily = phenomenaFontFamily,
                fontSize = 20.sp,
                color = MainColor
            )
        }
    }
}

