package com.example.buynest.views.orders

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.buynest.R
import com.example.buynest.ui.theme.*

data class Order(
    val id: Int,
    val customerName: String,
    val totalPrice: Int,
    val location: String,
    val deliveryDate: String,
    val imageRes: Int
)
val pastOrders =
    listOf(
        Order(1, "John Doe", 1200, "456 Elm Street, Springfield", "June 17, 2025", R.drawable.product),
        Order(2, "Jane Smith", 800, "739 Main Street, Springfield", "June 15, 2025", R.drawable.product),
        Order(2, "Jane Smith", 800, "739 Main Street, Springfield", "June 15, 2025", R.drawable.product),
        Order(2, "Jane Smith", 800, "739 Main Street, Springfield", "June 15, 2025", R.drawable.product),
        Order(2, "Jane Smith", 800, "739 Main Street, Springfield", "June 15, 2025", R.drawable.product),
        Order(2, "Jane Smith", 800, "739 Main Street, Springfield", "June 15, 2025", R.drawable.product),
        Order(2, "Jane Smith", 800, "739 Main Street, Springfield", "June 15, 2025", R.drawable.product),
        Order(2, "Jane Smith", 800, "739 Main Street, Springfield", "June 15, 2025", R.drawable.product),
        Order(2, "Jane Smith", 800, "739 Main Street, Springfield", "June 15, 2025", R.drawable.product),
        Order(2, "Jane Smith", 800, "739 Main Street, Springfield", "June 15, 2025", R.drawable.product),
    )
val activeOrders = listOf(
    Order(1, "John Doe", 1200, "456 Elm Street, Springfield", "June 17, 2025", R.drawable.product),
    Order(2, "Jane Smith", 800, "739 Main Street, Springfield", "June 15, 2025", R.drawable.product),
    Order(2, "Jane Smith", 800, "739 Main Street, Springfield", "June 15, 2025", R.drawable.product),
    Order(2, "Jane Smith", 800, "739 Main Street, Springfield", "June 15, 2025", R.drawable.product),
)


val phenomenaFontFamily = FontFamily(
    Font(R.font.phenomena_bold)
)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersHistoryScreen(gotoAllOrders:(String)->Unit,backClicked:()->Unit){
    Column (
        modifier = Modifier
            .fillMaxSize()
            .background(white)
            .padding(start = 24.dp)
    ){
        Spacer(modifier = Modifier.height(8.dp))
        CenterAlignedTopAppBar(
            title = {
                Text(
                    "Order History", fontSize = 20.sp,
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
        Spacer(modifier = Modifier.height(32.dp))
        SectionHeader("Active") {
            gotoAllOrders("Active Orders")
        }
        Spacer(modifier = Modifier.height(4.dp))
        AllOrdersList(activeOrders.take(2))
        Spacer(modifier = Modifier.height(16.dp))
        SectionHeader("Past") {
            gotoAllOrders("Past Orders")
        }
        Spacer(modifier = Modifier.height(4.dp))
        AllOrdersList(pastOrders.take(3))
    }
}

@Composable
fun SectionHeader(title: String, onViewAllClicked: () -> Unit) {
    Row {
        Text("$title Orders",
            fontSize = 24.sp,
            fontFamily = phenomenaFontFamily)
        Spacer(modifier = Modifier.weight(1f))
        Text(
            "View All",
            fontSize = 16.sp,
            color = MainColor,
            fontWeight = FontWeight.Normal,
            modifier = Modifier.padding(end = 16.dp, top = 8.dp)
                .clickable {
                    onViewAllClicked()
                }
        )
    }
}
@Composable
fun AllOrdersList(orders:List<Order>){
        LazyColumn(
            modifier = Modifier.padding(top = 16.dp, end = 18.dp)
        ){
            items(orders.size){
                OrderItem(order = orders[it])
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
}

@Composable
fun OrderItem(order: Order) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(white, RoundedCornerShape(16.dp))
            .border(2.dp, Gray, RoundedCornerShape(16.dp))
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .weight(2f)
        ) {
            Image(
                painter = painterResource(id = order.imageRes),
                contentDescription = "Order Image",
                modifier = Modifier
                    .padding(start = 12.dp, top = 12.dp, bottom = 12.dp)
                    .size(100.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop,

            )
        }

        Column(
            modifier = Modifier
                .weight(5f)
                .padding(16.dp)
        ) {
            Text(
                text = order.customerName,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.LocationOn,
                    contentDescription = "Location",
                    modifier = Modifier.size(16.dp),
                    tint = Color.Black
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = order.location, fontSize = 13.sp, color = Color.Black)
            }

            Spacer(modifier = Modifier.height(4.dp))

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

            Text(
                text = "Total: $${order.totalPrice}",
                fontWeight = FontWeight.SemiBold,
                color = MainColor,
                fontSize = 14.sp
            )
        }
    }
}
