package com.example.buynest.views.cart.components

import android.R
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.buynest.ui.theme.MainColor
import kotlinx.coroutines.launch

@Composable
fun CouponSheet(
    onValidCoupon: suspend (String) -> Unit,
    checkCoupon: suspend (String) -> Boolean,
    onSkip: () -> Unit
) {
    var coupon by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Enter Coupon",
                style = MaterialTheme.typography.h6,
                color = MainColor
            )
            TextButton(onClick = onSkip) {
                Text(
                    text = "Skip",
                    color = Color.Gray,
                    style = MaterialTheme.typography.body2
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = coupon,
            onValueChange = {
                coupon = it
                error = null
            },
            label = { Text("Coupon Code", color = MainColor) },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.Transparent,
                focusedIndicatorColor = MainColor,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = MainColor,
                textColor = MainColor
            ),
            isError = error != null,
            singleLine = true
        )

        if (error != null) {
            Spacer(Modifier.height(6.dp))
            Text(
                text = error.orEmpty(),
                color = Color.Red,
                style = MaterialTheme.typography.body2
            )
        }

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = {
                coroutineScope.launch {
                    val isValid = checkCoupon(coupon.trim())
                    if (isValid) {
                        error = null
                        onValidCoupon(coupon)
                    } else {
                        error = "Invalid coupon"
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = coupon.isNotBlank(),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = MainColor,
                contentColor = Color.White
            )
        ) {
            Text("Apply")
        }
    }
}
