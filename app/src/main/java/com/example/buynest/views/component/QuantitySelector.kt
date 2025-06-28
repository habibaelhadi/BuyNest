package com.example.buynest.views.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.buynest.ui.theme.MainColor

@Composable
fun QuantitySelector(
    quantity: Int,
    maxQuantity: Int,
    onChange: (Int) -> Unit,
    onLimitReached: () -> Unit
    ){
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(32.dp))
            .background(MainColor)
            .padding(horizontal = 4.dp)
            .height(35.dp)
    ) {
        IconButton(onClick = { if (quantity > 1) onChange(quantity - 1) }) {
            Icon(imageVector = Icons.Default.Remove, contentDescription = "Decrease", tint = Color.White)
        }
        Text(
            text = quantity.toString(),
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
        IconButton(
            onClick = {
                if (quantity < maxQuantity) {
                    onChange(quantity + 1)
                }else{
                    onLimitReached()
                }
            }
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Increase", tint = Color.White)
        }
    }
}
