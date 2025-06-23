package com.example.buynest.views.orderdetails

import androidx.compose.foundation.background
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.buynest.R
import com.example.buynest.ui.theme.*
import com.example.buynest.views.component.OrderProductItem
import com.example.buynest.views.component.PaymentDetails
import com.example.buynest.views.orders.phenomenaFontFamily
import com.example.buynest.model.entity.CartItem

val cartItems = listOf(
            CartItem(1, "Nike Air Jordan", "",3500, "Orange", 40, "", 1),
            CartItem(2, "Adidas Runner", "",2800, "Blue", 42, "", 2),
            CartItem(2, "Adidas Runner", "",2800, "Blue", 42, "", 2),
            CartItem(1, "Nike Air Jordan", "",3500, "Orange", 40,"", 1),
            CartItem(2, "Adidas Runner", "",2800, "Blue", 42, "", 2),
            CartItem(1, "Nike Air Jordan", "",3500, "Orange", 40, "", 1),
            CartItem(2, "Adidas Runner", "",2800, "Blue", 42, "", 2),
            CartItem(1, "Nike Air Jordan", "",3500, "Orange", 40, "", 1),
            CartItem(2, "Adidas Runner", "",2800, "Blue", 42, "", 2),
            CartItem(1, "Nike Air Jordan", "",3500, "Orange", 40, "", 1),
            CartItem(2, "Adidas Runner", "",2800, "Blue", 42, "", 2),

        )

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailsScreen(backClicked: () -> Unit) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(white)
            .padding(start = 24.dp, end = 18.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Order Details", fontSize = 20.sp,
                        fontFamily = phenomenaFontFamily,
                        color = MainColor,
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
        }

        item {
            Text(
                "Order Products",
                fontSize = 20.sp,
                fontFamily = phenomenaFontFamily,
                color = MainColor,
                modifier = Modifier.padding(top = 8.dp, start = 4.dp)
            )
        }

        items(cartItems.size) {
            OrderProductItem(item = cartItems[it])
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                "Payment Details",
                fontSize = 20.sp,
                fontFamily = phenomenaFontFamily,
                color = MainColor,
                modifier = Modifier.padding(start = 4.dp)
            )
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            PaymentDetails()
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}


@Composable
fun OrdersItemsList(itemsList:List<CartItem>){
    LazyColumn(
        modifier = Modifier.padding(top = 16.dp, end = 18.dp)
    ){
        items(itemsList.size){
            OrderProductItem(item = itemsList[it])
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}



