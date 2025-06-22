package com.example.buynest.views.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Card
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
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
    onSelect: () -> Unit,
    onMapClick: () -> Unit
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
                Checkbox(
                    checked = isSelected,
                    onCheckedChange = { onSelect() },
                    colors = CheckboxDefaults.colors(checkedColor = MainColor)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))

            Text(text = "Receiver: $receiverName", style = MaterialTheme.typography.body2)
            Spacer(modifier = Modifier.height(6.dp))

            Text(text = "Phone: $phone", style = MaterialTheme.typography.body2)
            Spacer(modifier = Modifier.height(6.dp))

            Text(text = "Address: $address", style = MaterialTheme.typography.body2)
            Spacer(modifier = Modifier.height(6.dp))

            landmark?.takeIf { it.isNotBlank() }?.let {
                Text(text = "Landmark: $it", style = MaterialTheme.typography.body2)
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "View on map",
                color = MainColor,
                fontWeight = MaterialTheme.typography.body2.fontWeight,
                style = MaterialTheme.typography.body2,
                modifier = Modifier.clickable { onMapClick() }
            )

            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = {}) {
                    Icon(Icons.Default.MoreVert, contentDescription = "More")
                }
            }
        }
    }
}