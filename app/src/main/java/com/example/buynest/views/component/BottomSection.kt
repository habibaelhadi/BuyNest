package com.example.buynest.views.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.buynest.repository.FirebaseAuthObject
import com.example.buynest.ui.theme.MainColor

@Composable
fun BottomSection(
    totalPrice: Int,
    icon: ImageVector,
    title: String,
    currencySymbol: String?,
    onClick: () -> Unit
) {
    val showGuestDialog = remember { mutableStateOf(false) }
    val user = FirebaseAuthObject.getAuth().currentUser
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            Text(
                "Total price",
                color = Color.Gray,
                fontSize = 20.sp
            )
            Text(
                "$currencySymbol $totalPrice",
                color = MainColor,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Button(
            onClick = {
                if (user == null) {
                    showGuestDialog.value = true
                } else {
                    onClick()
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = MainColor),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .height(50.dp)
                .fillMaxWidth(),
        ) {
            Text(
                title,
                color = Color.White,
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(icon, contentDescription = null, tint = Color.White)
        }
    }

    GuestAlertDialog(
        showDialog = showGuestDialog.value,
        onDismiss = { showGuestDialog.value = false },
        onConfirm = {
            showGuestDialog.value = false
        }
    )
}
