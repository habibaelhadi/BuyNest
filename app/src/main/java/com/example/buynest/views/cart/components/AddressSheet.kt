package com.example.buynest.views.cart.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.buynest.model.entity.AddressModel
import com.example.buynest.ui.theme.MainColor

@Composable
fun AddressSheet(
    defaultAddress: AddressModel?,
    onNavigateToAddress: () -> Unit,
    onProceed: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text("Shipping Address", style = MaterialTheme.typography.h4)
        Spacer(Modifier.height(8.dp))

        if (defaultAddress != null) {
            Text(defaultAddress.address1.toString())
        } else {
            Text("No default address set.")
        }

        Spacer(Modifier.height(16.dp))
        Button(
            onClick = onNavigateToAddress,
            modifier = Modifier.fillMaxWidth(),
            enabled = defaultAddress != null,
            shape = MaterialTheme.shapes.medium,
            elevation = null,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = MainColor,
                contentColor = Color.White,
            )
        ) {
            Text("Change Address")
        }

        Spacer(Modifier.height(16.dp))
        Button(
            onClick = onProceed,
            modifier = Modifier.fillMaxWidth(),
            enabled = defaultAddress != null,
            shape = MaterialTheme.shapes.medium,
            elevation = null,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = MainColor,
                contentColor = Color.White,
            )
        ) {
            Text("Proceed to Payment")
        }
    }
}
