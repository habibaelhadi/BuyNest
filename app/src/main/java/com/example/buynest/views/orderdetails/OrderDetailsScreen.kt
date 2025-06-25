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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import com.example.buynest.ui.theme.*
import com.example.buynest.views.component.OrderProductItem
import com.example.buynest.views.component.PaymentDetails
import com.example.buynest.views.orders.phenomenaFontFamily
import com.example.buynest.model.entity.CartItem
import com.example.buynest.viewmodel.currency.CurrencyViewModel
import com.example.buynest.viewmodel.orders.OrdersViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailsScreen(
    backClicked: () -> Unit,
    orderViewModel: OrdersViewModel,
    currencyViewModel: CurrencyViewModel
) {
    val selectedOrder by orderViewModel.selectedOrder.collectAsStateWithLifecycle()
    val rate by currencyViewModel.rate
    val currencySymbol by currencyViewModel.currencySymbol

    LaunchedEffect(Unit) {
        currencyViewModel.loadCurrency()
    }

    selectedOrder?.let { order ->
        val id = order.id

        val imageUrls = orderViewModel.extractImageUrlsFromNote(order.note)

        val cartItems = order.lineItems.edges.mapIndexed { index, edge ->
            val item = edge.node
            val variant = item.variant
            val price = variant?.price.toString().toDoubleOrNull()?.times(rate)?.toInt() ?: 0
            val options = variant?.selectedOptions ?: emptyList()
            val color = options.find { it.name == "Color" }?.value ?: ""
            val size = options.find { it.name == "Size" }?.value ?: ""

            val image = imageUrls.getOrNull(index) ?: variant?.image?.url.toString()

            CartItem(
                id = 0,
                name = item.title,
                imageUrl = image,
                price = price,
                color = color,
                size = size.toIntOrNull() ?: 0,
                quantity = item.quantity,
                lineId = order.id,
                variantId = variant?.id ?: "",
                currencySymbol = currencySymbol.toString()
            )
        }

        val totalAmount = order.totalPriceSet.shopMoney.amount.toString().toDoubleOrNull()?.times(rate)?.toInt() ?: 0

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
                        IconButton(onClick = backClicked) {
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
                PaymentDetails(totalAmount)
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}




