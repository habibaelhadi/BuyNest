package com.example.buynest.views.search

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.buynest.R
import com.example.buynest.ui.theme.LightGray
import com.example.buynest.ui.theme.LightGray2
import com.example.buynest.ui.theme.MainColor
import com.example.buynest.ui.theme.white
import com.example.buynest.utils.FilterType

data class Product(
    val imageUrl: Int,
    val name: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen() {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf(FilterType.All) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        IconButton(onClick = {

        }) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = null,
                tint = MainColor
            )
        }

        SearchBar(
            query = searchQuery,
            onQueryChange = { searchQuery = it },
            onSearch = { /* Optional: Handle search action */ },
            active = false,
            onActiveChange = { /* Optional: Handle focus change */ },
            placeholder = { Text("Search products, categories, or brands...") },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = "Search Icon")
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Close, contentDescription = "Clear")
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {

        }


        Spacer(modifier = Modifier.height(12.dp))

        FilterTabs(selectedFilter) { selectedFilter = it }

        Spacer(modifier = Modifier.height(12.dp))

        when (selectedFilter) {
            FilterType.All -> AllTab() // still as before
            FilterType.Categories -> CategoryTab(query = searchQuery, onCategoryClick = { /* navigate */ })
            FilterType.Brands -> BrandTab(query = searchQuery, onBrandClick = { /* navigate */ })
        }
    }
}

@Composable
fun AllTab() {
    val categories = listOf(
        Product(R.drawable.product, "Women's Fashion"),
        Product(R.drawable.product, "Men's Fashion"),
        Product(R.drawable.product, "Electronics"),
    )

    val brands = listOf(
        Product(R.drawable.product, "Nike"),
        Product(R.drawable.product, "Adidas"),
        Product(R.drawable.product, "Apple"),
        Product(R.drawable.product, "Nike"),
        Product(R.drawable.product, "Adidas"),
        Product(R.drawable.product, "Apple"),
        Product(R.drawable.product, "Nike"),
        Product(R.drawable.product, "Adidas"),
        Product(R.drawable.product, "Apple"),
        Product(R.drawable.product, "Nike"),
        Product(R.drawable.product, "Adidas"),
        Product(R.drawable.product, "Apple"),
    )

    Column(modifier = Modifier.fillMaxSize()) {

        Text(
            text = "Categories",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { category ->
                SearchResultCard(
                    imageUrl = category.imageUrl,
                    name = category.name,
                    onClick = { /* Navigate or filter */ }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Brands",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(brands) { brand ->
                SearchResultCard(
                    imageUrl = brand.imageUrl,
                    name = brand.name,
                    onClick = { /* Navigate or filter */ }
                )
            }
        }
    }
}


@Composable
fun SearchResultCard(
    imageUrl: Int,
    name: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp)) // <- Apply clipping first
            .clickable { onClick() }
            .border(
                border = BorderStroke(width = 2.dp, color = LightGray2),
                shape = RoundedCornerShape(24.dp) // match the clip
            )
            .background(white),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = imageUrl),
            contentDescription = name,
            modifier = Modifier
                .size(60.dp)
                .padding(8.dp)
                .clip(CircleShape)
                .background(Color.LightGray),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(4.dp))

        Text(
            text = name,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 2
        )
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
fun CategoryTab(query: String, onCategoryClick: (String) -> Unit) {
    val categories = listOf(
        Product(R.drawable.product, "Electronics"),
        Product(R.drawable.product, "Clothing"),
        Product(R.drawable.product, "Home"),
        Product(R.drawable.product, "Books"),
        Product(R.drawable.product, "Electronics"),
        Product(R.drawable.product, "Clothing"),
        Product(R.drawable.product, "Home"),
        Product(R.drawable.product, "Books"),
        Product(R.drawable.product, "Electronics"),
        Product(R.drawable.product, "Clothing"),
        Product(R.drawable.product, "Home"),
        Product(R.drawable.product, "Books")
    ).filter { it.name.contains(query, ignoreCase = true) }

    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(categories) { category ->
            ProductCard(product = category) { onCategoryClick(category.name) }
        }
    }
}

@Composable
fun BrandTab(query: String, onBrandClick: (String) -> Unit) {
    val brands = listOf(
        Product(R.drawable.product, "Nike"),
        Product(R.drawable.product, "Adidas"),
        Product(R.drawable.product, "Apple"),
        Product(R.drawable.product, "Samsung"),
        Product(R.drawable.product, "Nike"),
        Product(R.drawable.product, "Adidas"),
        Product(R.drawable.product, "Apple"),
        Product(R.drawable.product, "Samsung"),
        Product(R.drawable.product, "Nike"),
        Product(R.drawable.product, "Adidas"),
        Product(R.drawable.product, "Apple"),
        Product(R.drawable.product, "Samsung")
    ).filter { it.name.contains(query, ignoreCase = true) }

    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(brands) { brand ->
            ProductCard(product = brand) { onBrandClick(brand.name) }
        }
    }
}

@Composable
fun ProductCard(
    product: Product,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
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
                painter = painterResource(id = product.imageUrl),
                contentDescription = product.name,
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = product.name,
                style = MaterialTheme.typography.titleMedium,
                color = MainColor
            )
        }
    }
}

