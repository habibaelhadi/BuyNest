package com.example.buynest.views.component

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.buynest.BrandsAndProductsQuery
import com.example.buynest.R
import com.example.buynest.ui.theme.MainColor

@Composable
fun ForYouSection(
    items: List<BrandsAndProductsQuery.Node>,
    onProductClicked: (productId: String) -> Unit,
    rate: Double,
    currencySymbol: String
) {
    Column(
        modifier = Modifier.padding(horizontal = 8.dp)
    ) {
        Text(
            text = "For You",
            fontFamily = FontFamily(Font(R.font.phenomena_bold)),
            fontSize = 24.sp,
            modifier = Modifier.padding(bottom = 8.dp),
            color = MainColor
        )
        Spacer(modifier = Modifier.height(8.dp))

        LazyRow {
            items(items) { item ->
                val imageUrl = item.featuredImage?.url
                val firstVariant = item.variants.edges.firstOrNull()?.node
                val rawPrice = firstVariant?.price?.amount
                val priceDouble = when (rawPrice) {
                    is Number -> rawPrice.toDouble()
                    is String -> rawPrice.toDoubleOrNull() ?: 0.0
                    else -> 0.0
                }

                val discountedPrice = priceDouble * 0.85
                val convertedPrice = discountedPrice * rate
                val convertedOriginalPrice = priceDouble * rate

                val parts = item.title.split("|").map { it.trim() }
                val productName = if (parts.size >= 2) parts[1] else item.title
                val id = item.id.substringAfterLast("/")

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .padding(end = 16.dp)
                        .width(180.dp)
                        .clickable { onProductClicked(id) }
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(imageUrl),
                        contentDescription = item.title,
                        modifier = Modifier
                            .size(180.dp)
                            .clip(RoundedCornerShape(16))
                            .border(1.dp, MainColor.copy(alpha = 0.1f), RoundedCornerShape(16)),
                        contentScale = androidx.compose.ui.layout.ContentScale.FillBounds
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        productName,
                        color = Color.Black,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        maxLines = 1,
                        modifier = Modifier.padding(start = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "%.2f $currencySymbol".format(convertedOriginalPrice),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MainColor
                        )
                }
            }
        }
    }
}
