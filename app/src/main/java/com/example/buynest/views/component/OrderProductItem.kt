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
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import coil.compose.rememberAsyncImagePainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.buynest.model.entity.CartItem
import com.example.buynest.model.mapper.mapColorNameToColor
import com.example.buynest.ui.theme.Gray
import com.example.buynest.ui.theme.LightGray2
import com.example.buynest.ui.theme.MainColor
import com.example.buynest.ui.theme.white

@Composable
fun OrderProductItem(
    item: CartItem,
) {
    val color = item.color
    val colorDot = mapColorNameToColor(color)
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(white),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(2.dp, Gray),
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = rememberAsyncImagePainter(item.imageUrl),
                contentDescription = null,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MainColor
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(colorDot, shape = CircleShape)
                            .border(1.dp, LightGray2, shape = CircleShape)
                    )
                    Text(
                        text = "  ${item.color} | Size: ${item.size}",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "${item.price} ${item.currencySymbol}",
                    fontWeight = FontWeight.Bold,
                    color = MainColor
                )
            }
            Text(
                text = "x${item.quantity}",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = MainColor,
                modifier = Modifier.padding(end = 16.dp)
            )
        }
    }
}