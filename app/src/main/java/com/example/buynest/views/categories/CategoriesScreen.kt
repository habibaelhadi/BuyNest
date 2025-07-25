package com.example.buynest.views.categories

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.buynest.ProductsByHandleQuery
import com.example.buynest.R
import com.example.buynest.model.state.UiResponseState
import com.example.buynest.ui.theme.*
import com.example.buynest.utils.NetworkHelper
import com.example.buynest.viewmodel.categoryViewModel.CategoryViewModel
import com.example.buynest.viewmodel.currency.CurrencyViewModel
import com.example.buynest.viewmodel.shared.SharedViewModel
import com.example.buynest.views.component.CategoryItem
import com.example.buynest.views.component.Indicator
import com.example.buynest.views.component.NoInternetLottie
import com.example.buynest.views.component.SearchBar
import com.example.buynest.views.component.SideNavigation
import org.koin.androidx.compose.koinViewModel

@Composable
fun CategoriesScreen(
    onCartClicked: () -> Unit,
    onProductClicked: (productId: String) -> Unit,
    onSearchClicked: () -> Unit,
    sharedViewModel: SharedViewModel,
    categoryViewModel: CategoryViewModel,
    currencyViewModel: CurrencyViewModel
    ) {
    val selectedCategory by categoryViewModel.selectedCategory.collectAsStateWithLifecycle()
    val selectedSubcategory by categoryViewModel.selectedSubcategory.collectAsStateWithLifecycle()


    var showFilter by remember { mutableStateOf(false) }
    val rate by currencyViewModel.rate
    val currencySymbol by currencyViewModel.currencySymbol

    val phenomenaBold = FontFamily(Font(R.font.phenomena_bold))

    val categoryProduct by categoryViewModel.categoryProducts.collectAsStateWithLifecycle()

    val categories by sharedViewModel.category.collectAsStateWithLifecycle()
    val isConnected by NetworkHelper.isConnected.collectAsStateWithLifecycle()

    LaunchedEffect(categories,isConnected) {
        if (categories.isNotEmpty() && selectedCategory == null) {
            val firstCategory = categories.first().title
            categoryViewModel.setSelectedCategory(firstCategory)
            categoryViewModel.setSelectedSubcategory("Accessories")
            categoryViewModel.getCategoryProducts(firstCategory)
        }
    }

    LaunchedEffect(Unit) {
        currencyViewModel.loadCurrency()
    }

    LaunchedEffect(selectedCategory,isConnected) {
        if (selectedCategory != null) {
            categoryViewModel.getCategoryProducts(selectedCategory!!)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 10.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(onClick = { showFilter = !showFilter }) {
                Icon(
                    imageVector = Icons.Default.FilterList,
                    contentDescription = "Filter Icon",
                    tint = MainColor
                )
            }
        }

        SearchBar(onCartClicked = onCartClicked,onSearchClicked = onSearchClicked)

        val edges = (categoryProduct as? UiResponseState.Success<*>)?.data
            ?.let { it as? ProductsByHandleQuery.Data }
            ?.collectionByHandle?.products?.edges

        val allPrices = edges?.mapNotNull { edge ->
            val amount = edge.node.variants.edges.firstOrNull()?.node?.price?.amount
            when (amount) {
                is String -> amount.toFloatOrNull()
                is Number -> amount.toFloat()
                else -> null
            }
        } ?: emptyList()

        val maxPriceAvailable = allPrices.maxOrNull() ?: 1000f
        val minPriceAvailable = allPrices.minOrNull() ?: 0f

        var maxPrice by remember(maxPriceAvailable) {
            mutableStateOf(maxPriceAvailable)
        }



        if (maxPrice > maxPriceAvailable) maxPrice = maxPriceAvailable

        if (showFilter && allPrices.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Max Price: \$${maxPrice.toInt()}",
                fontSize = 16.sp,
                fontFamily = phenomenaBold,
                color = MainColor
            )
            Slider(
                value = maxPrice,
                onValueChange = { maxPrice = it },
                valueRange = minPriceAvailable..maxPriceAvailable,
                steps = 5,
                modifier = Modifier.padding(horizontal = 8.dp),
                colors = SliderDefaults.colors(
                    thumbColor = MainColor,
                    activeTrackColor = MainColor,
                    inactiveTrackColor = Color.LightGray
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxSize()) {
            SideNavigation(
                selectedItem = selectedCategory,
                onItemSelected = { clicked ->
                    val newCategory = if (selectedCategory == clicked) null else clicked
                    categoryViewModel.setSelectedCategory(newCategory)
                    categoryViewModel.setSelectedSubcategory(null)
                },
                onSubcategorySelected = { sub ->
                    categoryViewModel.setSelectedSubcategory(sub)
                },
                selectedSubcategory = selectedSubcategory,
                sharedViewModel = sharedViewModel
                )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "   ${selectedCategory ?: ""}${selectedSubcategory?.let { " : $it" } ?: ""}",
                    fontSize = 20.sp,
                    fontFamily = phenomenaBold,
                    color = MainColor
                )
                Spacer(modifier = Modifier.height(8.dp))

                when (val result = categoryProduct) {
                    is UiResponseState.Error ->{
                        NoInternetLottie()
                    }
                    UiResponseState.Loading -> {
                        Indicator()
                    }
                    is UiResponseState.Success<*> -> {
                        val data = result.data as ProductsByHandleQuery.Data
                        if (data == null) {
                            NoInternetLottie()
                        } else {
                            val productEdges = data.collectionByHandle?.products?.edges

                            val filteredEdges = productEdges?.filter { edge ->
                                val amount =
                                    edge.node.variants.edges.firstOrNull()?.node?.price?.amount
                                val price = when (amount) {
                                    is String -> amount.toFloatOrNull()
                                    is Number -> amount.toFloat()
                                    else -> 0f
                                }

                                (selectedSubcategory == null ||
                                        edge.node.productType.contains(
                                            selectedSubcategory!!,
                                            ignoreCase = true
                                        )) &&
                                        price!! <= maxPrice
                            }
                            CategoryProducts(
                                onProductClicked,
                                filteredEdges,
                                rate,
                                currencySymbol.toString()
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun CategoryProducts(
    onProductClicked: (productId: String) -> Unit,
    categoryProductList: List<ProductsByHandleQuery.Edge>?,
    rate: Double,
    currencySymbol: String
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        items(categoryProductList?.size ?: 0) { item ->
            CategoryItem(
                product = categoryProductList!![item].node,
                onProductClicked = onProductClicked,
                rate = rate,
                currencySymbol = currencySymbol
            )
        }
    }
}



