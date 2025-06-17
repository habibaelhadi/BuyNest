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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.buynest.R
import com.example.buynest.ui.theme.*
import com.example.buynest.views.component.CategoryItem
import com.example.buynest.views.component.ProductItem
import com.example.buynest.views.component.SearchBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrandDetailsScreen(categoryName: String,onCardClicked:()->Unit,backClicked:()->Unit) {
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
        SearchBar(
            onCardClicked = onCardClicked
        )
        Spacer(modifier = Modifier.height(24.dp))
        ProductGrid("")
    }
}


@Composable
fun ProductGrid(screenName:String) {
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
                ProductItem()
            }
        }
    }
}





