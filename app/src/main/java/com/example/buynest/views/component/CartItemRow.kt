package com.example.buynest.views.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.example.buynest.ui.theme.LightGray2
import com.example.buynest.ui.theme.MainColor

@Composable
fun CartItemRow(
    item: CartItem,
    onQuantityChange: (Int, Int) -> Unit,
    onDelete: (Int) -> Unit,
    onItemClick: (CartItem) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick(item) },
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, LightGray2)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = rememberAsyncImagePainter(item.imageUrl),
                contentDescription = null,
                modifier = Modifier
                    .size(100.dp)
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

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "EGP ${item.price}",
                    fontWeight = FontWeight.Bold,
                    color = MainColor
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(mapColorNameToColor(item.color), shape = CircleShape)
                    )
                    Text(
                        text = "  ${item.color} | Size: ${item.size}",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    QuantitySelector(
                        quantity = item.quantity,
                        onChange = { newQty -> onQuantityChange(item.id, newQty) },
                        maxQuantity = item.maxQuantity
                    )

                    IconButton(onClick = { onDelete(item.id) }) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = null,
                            tint = MainColor
                        )
                    }
                }
            }
        }
    }
}
