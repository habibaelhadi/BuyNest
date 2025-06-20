package com.example.buynest.views.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.buynest.ProductsByHandleQuery
import com.example.buynest.ui.theme.LightGray2
import com.example.buynest.ui.theme.MainColor
import com.example.buynest.ui.theme.lightGreen
import com.example.buynest.ui.theme.white
import com.example.buynest.utils.mapColorNameToColor

@Composable
fun CategoryItem(
    product : ProductsByHandleQuery.Node
) {
    val cleanedTitle = product.title.replace(Regex("\\(.*?\\)"), "").trim()
    val parts = cleanedTitle.split("|").map { it.trim() }
    val productName = if (parts.size >= 2) parts[1] else "there is no name"
    val selectedOptions = product.variants.edges[0].node.selectedOptions
    val color = selectedOptions
        .map { it.value }
        .filter { it.any { ch -> ch.isLetter() } }
        .joinToString(" / ")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(2.dp, LightGray2),
        colors = cardColors(containerColor = white)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = rememberAsyncImagePainter(product.featuredImage?.url),
                contentDescription = null,
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(2f)
            ) {
                Text(
                    text = productName,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    fontSize = 16.sp,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(mapColorNameToColor(color), shape = CircleShape)
                            .border(1.dp,LightGray2, shape = CircleShape)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = color,
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${product.variants.edges[0].node.price.amount} LE",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MainColor
                )
            }

        }
    }
}
