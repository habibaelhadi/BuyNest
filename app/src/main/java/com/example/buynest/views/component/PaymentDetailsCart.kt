package com.example.buynest.views.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.buynest.ui.theme.Gray
import com.example.buynest.ui.theme.white

@Composable
fun PaymentDetails(totalAmount1: Any,PaymentWay: String,priceBeforeDiscount: String,discount: String) {
    val paymentWay = PaymentWay
    Card (
        modifier = Modifier.fillMaxWidth().padding(end = 16.dp),
        colors = CardDefaults.cardColors(white),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(2.dp, Gray),
    ){
        Text(
            paymentWay,
            modifier = Modifier.padding(top = 16.dp, start = 16.dp, bottom = 6.dp),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        Text("Total Price : $priceBeforeDiscount LE",
            modifier = Modifier.padding(top = 16.dp, start = 16.dp, bottom = 16.dp),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        Text("Discount : $discount LE",
            modifier = Modifier.padding(top = 16.dp, start = 16.dp, bottom = 16.dp),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        Text("Total Price after Discount : $totalAmount1 LE",
            modifier = Modifier.padding(top = 16.dp, start = 16.dp, bottom = 16.dp),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}