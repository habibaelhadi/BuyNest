package com.example.buynest.views.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.buynest.R
import com.example.buynest.ui.theme.MainColor
import com.example.buynest.views.home.ItemModel

@Composable
fun TopBrandsSection(items: List<ItemModel>, onCategoryClick: (String) -> Unit ) {
    val phenomena_bold = FontFamily(
        Font(R.font.phenomena_bold)
    )
    Column(modifier = Modifier.padding(horizontal = 8.dp)) {
        Text(
            text = "Brands",
            fontFamily = phenomena_bold,
            fontSize = 24.sp,
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
                                .clickable { onCategoryClick(item.name) }
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
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Black)
                        }
                    }
                }
            }
        }
    }
}
