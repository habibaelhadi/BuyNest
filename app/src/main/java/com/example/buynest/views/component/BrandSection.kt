package com.example.buynest.views.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import coil.compose.rememberAsyncImagePainter
import com.example.buynest.BrandsAndProductsQuery
import com.example.buynest.R
import com.example.buynest.ui.theme.MainColor

@Composable
fun TopBrandsSection(items: List<BrandsAndProductsQuery.Node3>, onCategoryClick: (String,String) -> Unit ) {
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
        Spacer(modifier = Modifier.height(8.dp))

        LazyRow {
            items(chunkedItems) { columnItems ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(end = 20.dp)
                ) {
                    columnItems.forEach { item ->
                        val imageUrl = item.image?.url.toString()
                        val gid = item.id
                        val numericId = gid.substringAfterLast("/")

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .padding(bottom = 24.dp)
                                .clickable { onCategoryClick(item.title,numericId) }
                        ) {
                            Image(
                                painter = rememberAsyncImagePainter(imageUrl),
                                contentDescription = item.title,
                                modifier = Modifier
                                    .size(110.dp)
                                    .clip(CircleShape)
                                    .border(1.dp, MainColor.copy(alpha = 0.1f)
                                            ,CircleShape),
                                contentScale = ContentScale.FillBounds
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(item.title, fontSize = 16.sp,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Black)
                        }
                    }
                }
            }
        }
    }
}
