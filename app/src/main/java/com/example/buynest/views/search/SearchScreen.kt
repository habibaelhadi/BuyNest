package com.example.buynest.views.search

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import com.example.buynest.model.state.UiResponseState
import com.example.buynest.ui.theme.LightGray
import com.example.buynest.ui.theme.LightGray2
import com.example.buynest.ui.theme.MainColor
import com.example.buynest.ui.theme.white
import com.example.buynest.utils.FilterType
import com.example.buynest.viewmodel.home.HomeViewModel
import com.example.buynest.views.component.Indicator
import com.example.buynest.BrandsAndProductsQuery
import com.example.buynest.ProductsByHandleQuery
import com.example.buynest.model.entity.UiProduct
import com.example.buynest.utils.mapFromBrandProduct
import com.example.buynest.utils.mapFromCategoryProduct
import com.example.buynest.viewmodel.categoryViewModel.CategoryViewModel
import com.example.buynest.views.orders.phenomenaFontFamily
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onBackClicked: () -> Unit,
    onProductClicked: (productId: String) -> Unit,
    homeViewModel: HomeViewModel = koinViewModel(),
    categoryViewModel: CategoryViewModel = koinViewModel()
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf(FilterType.All) }
    val brands by homeViewModel.brand.collectAsStateWithLifecycle()
    val categoryProductsState by categoryViewModel.categoryProducts.collectAsStateWithLifecycle()
    var brandsList by remember { mutableStateOf<List<BrandsAndProductsQuery.Node3>>(emptyList()) }
    var categoryList by remember { mutableStateOf<List<BrandsAndProductsQuery.Node3>>(emptyList()) }
    var productsList by remember { mutableStateOf<List<BrandsAndProductsQuery.Node>>(emptyList()) }
    var isCategorySelected by remember { mutableStateOf(false) }
    var categoryUiProducts by remember { mutableStateOf<List<UiProduct>?>(null) }
    var uiProducts by remember { mutableStateOf<List<UiProduct>?>(null) }
    var filteredProducts by remember { mutableStateOf<List<UiProduct>?>(null) }
    val filterExpanded = remember { mutableStateOf(false) }
    val selectedPrice = remember { mutableFloatStateOf(1000f) }

    LaunchedEffect(Unit) {
        homeViewModel.getBrands()
    }

    LaunchedEffect(searchQuery, uiProducts, selectedPrice.floatValue) {
        filteredProducts = uiProducts?.filter { product ->
            val matchesQuery = product.title.contains(searchQuery, ignoreCase = true)
            val priceMatches = product.price != null && product.price <= selectedPrice.floatValue
            matchesQuery && priceMatches
        }
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        CenterAlignedTopAppBar(
            title = {
                Text(
                    "Search", fontSize = 20.sp,
                    fontFamily = phenomenaFontFamily,
                    color = MainColor,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            },
            navigationIcon = {
                IconButton(onClick = { onBackClicked() }) {
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

        SearchBar(
            query = searchQuery,
            onQueryChange =
            { searchQuery = it },
            onSearch = {},
            active = false,
            onActiveChange = { /* Optional: Handle focus change */ },
            placeholder = { Text("Search products using categories, or brands...") },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = "Search Icon")
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = {
                        searchQuery = ""
                        filteredProducts = null
                    }) {
                        Icon(Icons.Default.Close, contentDescription = "Clear")
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {

        }

        Spacer(modifier = Modifier.height(12.dp))

        FilterTabs(selectedFilter) { filter ->
            selectedFilter = filter
            if (filter == FilterType.All) {
                isCategorySelected = false
            }
        }

        Spacer(modifier = Modifier.height(12.dp))


        when (val result = brands) {
            is UiResponseState.Error -> {
                Text(text = result.message)
            }
            UiResponseState.Loading -> {
                Indicator()
            }
            is UiResponseState.Success<*> -> {
                val (brandList, productList) = result.data as Pair<List<BrandsAndProductsQuery.Node3>, List<BrandsAndProductsQuery.Node>>
                brandsList = brandList.dropLast(4)
                categoryList = brandList.subList(12,16)
                productsList = productList.drop(10)
                FilterExpansionSection(
                    filterType = selectedFilter,
                    query = searchQuery,
                    onItemClick = { type,value ->
                        if (type == FilterType.All.name) {
                            isCategorySelected = false
                        }else{
                            isCategorySelected = true
                            categoryViewModel.getCategoryProducts(value)
                        }
                    },
                    categoryList,
                    brandsList
                )

                Spacer(modifier = Modifier.height(12.dp))
            }
        }

         categoryUiProducts = when (val result = categoryProductsState) {
            is UiResponseState.Success<*> -> {
                val data = result.data as ProductsByHandleQuery.Data
                data.collectionByHandle?.products?.edges?.map { mapFromCategoryProduct(it.node) }
            }
            else -> null
        }

         uiProducts = when {
            isCategorySelected && categoryUiProducts != null -> categoryUiProducts
            else -> productsList.map { mapFromBrandProduct(it) }
        }

        val prices = uiProducts?.mapNotNull { it.price }
        val minPrice = prices?.minOrNull() ?: 0f
        val maxPrice = prices?.maxOrNull() ?: 1000f

        val adjustedMin = if (minPrice == maxPrice) minPrice - 1 else minPrice
        val adjustedMax = if (minPrice == maxPrice) maxPrice + 1 else maxPrice
        val sliderSteps = if (adjustedMax - adjustedMin >= 2) 4 else 0

        LaunchedEffect(maxPrice) {
            if (selectedPrice.floatValue > maxPrice || selectedPrice.floatValue == 1000f)
                selectedPrice.floatValue = maxPrice
        }


        if (filterExpanded.value) {
            Text(
                text = "Min Price: ${selectedPrice.floatValue.toInt()}LE - Max price: ${adjustedMax.toInt()}LE",
                color = MainColor,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
            )

            Slider(
                value = selectedPrice.floatValue.coerceIn(adjustedMin, adjustedMax),
                onValueChange = { selectedPrice.floatValue = it },
                valueRange = adjustedMin..adjustedMax,
                steps = sliderSteps,
                modifier = Modifier.padding(horizontal = 8.dp),
                colors = SliderDefaults.colors(
                    thumbColor = MainColor,
                    activeTrackColor = MainColor,
                    inactiveTrackColor = Color.LightGray
                )
            )

        }


        AllProductsSection(filteredProducts ?: uiProducts.orEmpty(),onProductClicked)


    }
}

@Composable
fun FilterTabs(selected: FilterType, onSelect: (FilterType) -> Unit) {
    val filters = listOf(FilterType.All, FilterType.Categories, FilterType.Brands)
    Row (
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ){
        filters.forEach { filter ->
            val isSelected = filter == selected
            Button(
                onClick = { onSelect(filter) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSelected) MainColor else LightGray,
                    contentColor = if (isSelected) white else Color.Black
                ),
                modifier = Modifier
                    .padding(end = 8.dp)
            ) {
                Text(filter.name)
            }
        }
    }
}

@Composable
fun ProductCard(
    product: UiProduct,
    onProductClicked: (productId: String) -> Unit
    ) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onProductClicked(product.id.substringAfterLast("/")) },
        border = BorderStroke(1.dp, MainColor.copy(0.5f)),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(white)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = rememberAsyncImagePainter(product.imageUrl),
                contentDescription = null ,
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = product.title,
                style = MaterialTheme.typography.titleMedium,
                color = MainColor
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FilterExpansionSection(
    filterType: FilterType,
    query: String,
    onItemClick: (String,String) -> Unit,
    categoryList: List<BrandsAndProductsQuery.Node3>,
    brandsList: List<BrandsAndProductsQuery.Node3>
) {
    val categories = categoryList.map { it.title }
    val brands = brandsList.map { it.title }

    val items = when (filterType) {
        FilterType.Categories -> categories
        FilterType.Brands -> brands
        else -> emptyList()
    }

    if (filterType != FilterType.All) {
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items.forEach { item ->
                Text(
                    text = item,
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(LightGray2)
                        .clickable { onItemClick(filterType.name, item) }
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun AllProductsSection(
    uiProducts: List<UiProduct>,
    onProductClicked: (productId: String) -> Unit
) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items(uiProducts) { product ->
            ProductCard(product,onProductClicked)
        }
    }
}
