package com.example.buynest.views.orders

import android.os.Build
import androidx.annotation.RequiresApi
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
import com.example.buynest.R
import androidx.compose.runtime.getValue
import com.example.buynest.admin.GetOrdersByEmailQuery
import com.example.buynest.model.state.UiResponseState
import com.example.buynest.model.repository.FirebaseAuthObject
import com.example.buynest.ui.theme.*
import com.example.buynest.viewmodel.orders.OrdersViewModel
import com.example.buynest.views.component.Indicator
import com.example.buynest.views.component.OrderItem
import com.example.buynest.views.favourites.NoDataLottie

val phenomenaFontFamily = FontFamily(
    Font(R.font.phenomena_bold)
)

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersHistoryScreen(
    backClicked:()->Unit
    ,gotoOrderDetails:()->Unit,
    orderViewModel: OrdersViewModel
){

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
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            "BuyNest", fontSize = 20.sp,
            fontFamily = phenomenaFontFamily, color = MainColor
        )
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
                val orders = response.orders.edges.map { it.node }
                if (orders.isEmpty()){
                    NoDataLottie(false)
                }else {
                    AllOrdersList(orders = orders, onOrderClick = {
                        orderViewModel.setSelectedOrder(it)
                        gotoOrderDetails()
                    })
                }
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AllOrdersList(orders: List<GetOrdersByEmailQuery.Node>, onOrderClick: (GetOrdersByEmailQuery.Node) -> Unit){
        LazyColumn(
            modifier = Modifier.padding(top = 16.dp, end = 18.dp)
        ){
            items(orders.size) {
                OrderItem(order = orders[it], gotoOrderDetails = { onOrderClick(orders[it]) })
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
}


