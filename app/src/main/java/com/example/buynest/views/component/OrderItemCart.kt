package com.example.buynest.views.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.buynest.R
import com.example.buynest.ui.theme.Gray
import com.example.buynest.ui.theme.white
import com.example.buynest.views.orders.Order

@Composable
fun OrderItem(order: Order, gotoOrderDetails:()->Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(white, RoundedCornerShape(16.dp))
            .border(2.dp, Gray, RoundedCornerShape(16.dp))
            .clickable {
                gotoOrderDetails()
            }
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .weight(2f)
        ) {
            Image(
                painter = painterResource(R.drawable.orders),
                contentDescription = "Order Image",
                modifier = Modifier
                    .padding(start = 24.dp, top = 12.dp, bottom = 12.dp)
                    .size(50.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop,

                )
        }
        Column(
            modifier = Modifier
                .weight(7f)
                .padding(16.dp, top = 20.dp)
        ) {
            Text(text = "OrderID: ${order.id}", fontSize = 13.sp,
                color = Color.Black, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.DateRange,
                    contentDescription = "Delivery Date",
                    modifier = Modifier.size(16.dp),
                    tint = Color.Black
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = order.deliveryDate, fontSize = 13.sp, color = Color.Black)
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}