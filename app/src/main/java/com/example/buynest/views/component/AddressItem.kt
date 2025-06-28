package com.example.buynest.views.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.buynest.ui.theme.MainColor
import com.example.buynest.ui.theme.lightBlue

@Composable
fun AddressItem(
    label: String,
    icon: ImageVector,
    address: String,
    phone: String,
    receiverName: String,
    landmark: String?,
    isSelected: Boolean = false,
    isDefault: Boolean = false,
    onSetDefault: () -> Unit,
    onSettingsClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        backgroundColor = lightBlue,
        border = BorderStroke(1.dp, if (isSelected) MainColor else Color.LightGray),
        elevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = MainColor)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.subtitle1,
                    modifier = Modifier.weight(1f)
                )
                if (isDefault) {
                    Text(
                        text = "Default",
                        color = MainColor,
                        style = MaterialTheme.typography.caption,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
                RadioButton(
                    selected = isSelected,
                    onClick = {
                        if (!isSelected) onSetDefault()
                    },
                    colors = RadioButtonDefaults.colors(selectedColor = MainColor)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text("Receiver: $receiverName", style = MaterialTheme.typography.body2)
            Text("Phone: $phone", style = MaterialTheme.typography.body2)
            Text("Address: $address", style = MaterialTheme.typography.body2)

            landmark?.takeIf { it.isNotBlank() }?.let {
                Spacer(modifier = Modifier.height(6.dp))
                Text("Landmark: $it", style = MaterialTheme.typography.body2)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = onSettingsClick) {
                    Icon(Icons.Default.Settings, contentDescription = "Settings")
                }
            }
        }
    }
}
