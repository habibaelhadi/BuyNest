package com.example.buynest.views.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.buynest.ui.theme.MainColor
import com.example.buynest.ui.theme.white

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountryOptionBottomSheet(
    selectedCountry: String,
    onDismiss: () -> Unit,
    onSelect: (String) -> Unit
) {
    val countries = listOf("Egypt", "United States", "Germany", "France", "Saudi Arabia")

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Select Country/Region", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(12.dp))
            countries.forEach { country ->
                Button(
                    onClick = {
                        onSelect(country)
                        onDismiss()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (country == selectedCountry) MainColor else white,
                        contentColor = if (country == selectedCountry) white else MainColor
                    )
                ) {
                    Text(country)
                }
            }
        }
    }
}
