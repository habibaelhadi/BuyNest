package com.example.buynest.views.component

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
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
import com.example.buynest.admin.GetOrdersByEmailQuery
import com.example.buynest.ui.theme.Gray
import com.example.buynest.ui.theme.white
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun OrderItem(order: GetOrdersByEmailQuery.Node, gotoOrderDetails: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(white, RoundedCornerShape(16.dp))
            .border(2.dp, Gray, RoundedCornerShape(16.dp))
            .clickable { gotoOrderDetails() }
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
                contentScale = ContentScale.Crop
            )
        }

        Column(
            modifier = Modifier
                .weight(7f)
                .padding(16.dp, top = 20.dp)
        ) {
            Text(
                text = "Order ID: ${order.name}",
                fontSize = 13.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            val dateTime = ZonedDateTime.parse(order.createdAt as CharSequence?)
            val formattedDate = dateTime.toLocalDate()
                .format(DateTimeFormatter.ofPattern("MMMM dd, yyyy"))
            val formattedTime = dateTime.toLocalTime()
                .format(DateTimeFormatter.ofPattern("hh:mm a"))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.DateRange,
                    contentDescription = "Date Icon",
                    modifier = Modifier.size(16.dp),
                    tint = Color.Black
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Date: $formattedDate",
                    fontSize = 13.sp,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.AccessTime,
                    contentDescription = "Time Icon",
                    modifier = Modifier.size(16.dp),
                    tint = Color.Black
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Time: $formattedTime",
                    fontSize = 13.sp,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
