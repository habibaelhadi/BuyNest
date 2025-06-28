package com.example.buynest.views.address.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.buynest.ui.theme.DarkGreen
import com.example.buynest.ui.theme.MainColor

@Composable
fun SettingsBottomSheet(
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onDismiss: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MainColor)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        TextButton(
            onClick = {
                onDismiss()
                onDelete()
            },
            modifier = Modifier
                .background(Color.Red, shape = RoundedCornerShape(8.dp))
        ) {
            Text(
                "Delete",
                color = Color.White,
                fontSize = 18.sp
            )
        }

        TextButton(
            onClick = {
                onDismiss()
                onEdit()
            },
            modifier = Modifier
                .background(DarkGreen, shape = RoundedCornerShape(8.dp))
        ) {
            Text(
                "Edit",
                color = Color.White,
                fontSize = 18.sp
            )
        }
    }
}
