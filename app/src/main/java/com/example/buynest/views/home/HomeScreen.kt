package com.example.buynest.views.home

import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.compose.ui.util.lerp
import com.example.buynest.R
import com.example.buynest.ui.theme.*
import kotlin.math.abs
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.pager.calculateCurrentOffsetForPage


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


@Composable
fun HomeScreen() {
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
        Spacer(modifier = Modifier.height(8.dp))
        Text("BuyNest", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MainColor)
        Spacer(modifier = Modifier.height(24.dp))
        SearchBar()
        Spacer(modifier = Modifier.height(24.dp))
        AdsSection(offers = offers)
        Spacer(modifier = Modifier.height(24.dp))
        CategoriesSection(items = categoriesList)
        Spacer(modifier = Modifier.height(24.dp))
        BrandSection( items = brandsList)
    }
}

@Composable
fun SearchBar() {
    Row (
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ){
        OutlinedTextField(
            value = "",
            onValueChange = {},
            placeholder = { Text("What do you search for?") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            modifier = Modifier
                .weight(30F)
                .background(Color.White),
            shape = RoundedCornerShape(24.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MainColor,
                unfocusedBorderColor = Gray
            )
        )
        Spacer(modifier = Modifier.width(20.dp))
        Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = MainColor)
    }
}

@Composable
fun AdsSection(offers: List<OfferModel>) {
    val pagerState = rememberPagerState(offers.size)
    val pageSpacing = 8.dp

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        HorizontalPager(
            count = offers.size,
            state = pagerState,
            contentPadding = PaddingValues(horizontal = 16.dp),
            itemSpacing = pageSpacing,
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
        ) { page ->

            val pageOffset = calculateCurrentOffsetForPage(page)
            val absOffset = abs(pageOffset)

            val scale = lerp(0.85f, 1f, 1f - absOffset.coerceIn(0f, 1f))
            var alpha = lerp(0.4f, 1f, 1f - absOffset.coerceIn(0f, 1f))
            val imageOffsetX = (pageOffset * 60).dp

            Box(
                modifier = Modifier
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        this.alpha = alpha
                        cameraDistance = 12f * density
                    }
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
            ) {
                Image(
                    painter = painterResource(id = offers[page].imageRes),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .offset(x = imageOffsetX)
                        .fillMaxSize()
                        .alpha(0.85f)
                )

                Column(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 16.dp, end = 16.dp)
                        .graphicsLayer {
                            translationX = -pageOffset * 50.dp.toPx()
                            alpha = lerp(0f, 1f, 1f - absOffset)
                        }
                ) {
                    Text(
                        text = offers[page].title,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MainColor,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.width(120.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = offers[page].subtitle,
                        fontSize = 14.sp,
                        color = MainColor,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.width(140.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF003366)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(
                            offers[page].buttonText,
                            color = Color.White,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(offers.size) { index ->
                val selected = pagerState.currentPage == index
                val dotAlpha by animateFloatAsState(
                    targetValue = if (selected) 1f else 0.5f,
                    animationSpec = tween(300)
                )

                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .size(if (selected) 10.dp else 8.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF003366).copy(alpha = dotAlpha))
                )
            }
        }
    }
}

@Composable
fun CategoriesSection(items: List<ItemModel>) {
    Column(modifier = Modifier.padding(horizontal = 8.dp)) {
        Text(
            text = "Categories",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier.padding(bottom = 8.dp),
            color = MainColor
        )

        //each column has 2 items instead of Grid
        val chunkedItems = items.chunked(2)

        LazyRow {
            items(chunkedItems) { columnItems ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(end = 16.dp)
                ) {
                    columnItems.forEach { item ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .padding(bottom = 16.dp)
                        ) {
                            Image(
                                painter = painterResource(id = item.imageRes),
                                contentDescription = item.name,
                                modifier = Modifier
                                    .size(90.dp)
                                    .clip(CircleShape)
                                    .background(Color.LightGray),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(item.name, fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BrandSection(items: List<ItemModel>) {
    Column (
        modifier = Modifier.padding(horizontal = 8.dp)
    ){
        Text(
            text = "Top Brands",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier.padding(bottom = 8.dp),
            color = MainColor
        )
        LazyRow {
            items(items) { item ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(end = 16.dp)
                ) {
                    Image(
                        painter = painterResource(id = item.imageRes),
                        contentDescription = item.name,
                        modifier = Modifier
                            .size(170.dp)
                            .clip(RoundedCornerShape(
                                topStart = 16.dp,
                                topEnd = 16.dp,
                                bottomStart = 16.dp,
                                bottomEnd = 16.dp
                            ))
                            .background(Color.LightGray),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        item.name, fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }
        }
    }
}





