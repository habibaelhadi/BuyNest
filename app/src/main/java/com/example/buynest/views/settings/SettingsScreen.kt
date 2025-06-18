package com.example.buynest.views.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.buynest.ui.theme.MainColor
import com.example.buynest.ui.theme.white
import com.example.buynest.views.component.SettingsCard

@Composable
fun SettingsScreen(
    gotoProfileScreen: () -> Unit,
    gotoOrdersHistoryScreen: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(white)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        Text("BuyNest", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MainColor)
        Spacer(modifier = Modifier.height(12.dp))


        SettingsCard("Flores Juanita", icon = Icons.Default.Person, bold = true,
            onClick = {
                gotoProfileScreen()
            })
        Spacer(modifier = Modifier.height(12.dp))

        SettingsCard("Address Book", Icons.Default.Home)
        SettingsCard("Payment Option", Icons.Default.Payment)
        SettingsCard("Orders History", Icons.Default.History,
            onClick = {
                gotoOrdersHistoryScreen()
            })
        Spacer(modifier = Modifier.height(12.dp))

        SettingsCard("Country/Region", Icons.Default.Public)
        SettingsCard("Currency", Icons.Default.AttachMoney)
        Spacer(modifier = Modifier.height(12.dp))


        SettingsCard("Connect with Us", Icons.Default.Phone)
        SettingsCard("About", Icons.Default.Info)

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { /* Log out logic */ },
            colors = ButtonDefaults.buttonColors(
                containerColor = MainColor,
                contentColor = white
            ),
            shape = RoundedCornerShape(6.dp),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = "Log Out")
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
