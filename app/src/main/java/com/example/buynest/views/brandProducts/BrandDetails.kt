package com.example.buynest.views.brandProducts

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.buynest.R
import com.example.buynest.ui.theme.*
import com.example.buynest.views.categories.CategoryItem
import com.example.buynest.views.home.SearchBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrandDetailsScreen(categoryName: String,onCartClicked:()->Unit,backClicked:()->Unit,onProductClicked:()->Unit) {
    val phenomenaFontFamily = FontFamily(
        Font(R.font.phenomena_bold)
    )
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

        Spacer(modifier = Modifier.height(4.dp))
        Text("BuyNest", fontSize = 20.sp,
            fontFamily = phenomenaFontFamily,
             color = MainColor)
        Spacer(modifier = Modifier.height(24.dp))
        SearchBar(
            onCartClicked = onCartClicked
        )
        Spacer(modifier = Modifier.height(24.dp))
        ProductGrid("",onProductClicked)
    }
}


@Composable
fun ProductGrid(screenName: String, onProductClicked: () -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ){
        if (screenName == "Categories"){
            items(4){
                CategoryItem()
            }
        }else {
            items(20) {
                ProductItem(onProductClicked)
            }
        }
    }
}

@Composable
fun ProductItem(onProductClicked: () -> Unit) {
    Card (
        modifier = Modifier
            .width(200.dp)
            .padding(6.dp),
        border = BorderStroke(1.dp, MainColor.copy(0.5f)),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        onClick = { onProductClicked() }
    ){
        Column(
            modifier = Modifier.background(white)
        ){
            Box(
                modifier = Modifier
                    .height(140.dp)
                    .fillMaxWidth()

            ){
                Image(
                    painter = painterResource(id = R.drawable.product),
                    contentDescription = null,
                    modifier = Modifier.align(Alignment.Center),
                    contentScale = ContentScale.FillHeight
                )
                Box(
                    modifier = Modifier
                        .padding(end = 8.dp, top = 8.dp)
                        .align(Alignment.TopEnd)
                ){
                    IconButton(
                        onClick = {},
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .background(white, shape = CircleShape)
                            .size(32.dp)
                            .padding(8.dp)

                    ) {
                        Icon(
                            imageVector = Icons.Outlined.FavoriteBorder,
                            contentDescription = null,
                            tint = MainColor
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Nike Air Jordon",
                style = MaterialTheme.typography.titleMedium,
                color = MainColor,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "120.00 LE",
                style = MaterialTheme.typography.titleSmall,
                color = MainColor,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row (
                Modifier.padding(bottom = 16.dp, end = 8.dp)
            ){
                Text(
                    text = "Review (4.8) ",
                    style = MaterialTheme.typography.titleSmall,
                    color = MainColor,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = null,
                    tint = Yellow,
                    modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.weight(1f))
                IconButton(
                    onClick = {},
                    modifier = Modifier
                        .background(MainColor, shape = CircleShape)
                        .size(24.dp)
                        .align(Alignment.CenterVertically)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = null,
                        tint = white
                    )
                }
            }
        }
    }
}



