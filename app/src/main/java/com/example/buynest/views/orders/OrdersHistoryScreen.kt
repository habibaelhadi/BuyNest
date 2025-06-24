package com.example.buynest.views.orders

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.buynest.R
import androidx.compose.runtime.getValue
import com.example.buynest.admin.GetOrdersByEmailQuery
import com.example.buynest.model.state.UiResponseState
import com.example.buynest.repository.FirebaseAuthObject
import com.example.buynest.repository.order.OrderRepo
import com.example.buynest.ui.theme.*
import com.example.buynest.viewmodel.orders.OrdersFactory
import com.example.buynest.viewmodel.orders.OrdersViewModel
import com.example.buynest.views.component.Indicator
import com.example.buynest.views.component.OrderItem

data class Order(
    val id: Int,
    val deliveryDate: String,
    val imageRes: Int
)
val pastOrders =
    listOf(
        Order(1001, "June 17, 2025", R.drawable.product),
        Order(2521, "June 15, 2025", R.drawable.product),
        Order(2545, "June 15, 2025", R.drawable.product),
        Order(2, "June 15, 2025", R.drawable.product),
        Order(2, "June 15, 2025", R.drawable.product),
        Order(2, "June 15, 2025", R.drawable.product),
        Order(2, "June 15, 2025", R.drawable.product),
        Order(2, "June 15, 2025", R.drawable.product),
        Order(2, "June 15, 2025", R.drawable.product),
        Order(2, "June 15, 2025", R.drawable.product),
        Order(101654, "June 17, 2025", R.drawable.product),
        Order(25454, "June 15, 2025", R.drawable.product),
        Order(2, "June 15, 2025", R.drawable.product),
        Order(2, "June 15, 2025", R.drawable.product),
    )

val phenomenaFontFamily = FontFamily(
    Font(R.font.phenomena_bold)
)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersHistoryScreen(backClicked:()->Unit,gotoOrderDetails:()->Unit){
    val orderViewModel: OrdersViewModel = viewModel(
        factory = OrdersFactory(OrderRepo())
    )

    val orderList by orderViewModel.orders.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        val email = FirebaseAuthObject.getAuth().currentUser?.email
        orderViewModel.getAllOrders(email.toString())
    }
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
        when(val result = orderList){
            is UiResponseState.Error -> {
                Text(result.message)
            }
            UiResponseState.Loading -> {
                Indicator()
            }
            is UiResponseState.Success<*> -> {
                val response = result.data as GetOrdersByEmailQuery.Data
                val orders = response.orders.edges.mapNotNull { it.node }
                AllOrdersList(orders, gotoOrderDetails)

            }
        }
    }
}


@Composable
fun AllOrdersList(orders: List<GetOrdersByEmailQuery.Node>, gotoOrderDetails: () -> Unit){
        LazyColumn(
            modifier = Modifier.padding(top = 16.dp, end = 18.dp)
        ){
            items(orders.size){
                OrderItem(order = orders[it],gotoOrderDetails)
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
}


