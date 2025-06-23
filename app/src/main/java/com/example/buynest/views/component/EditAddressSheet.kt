package com.example.buynest.views.component

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.apollographql.apollo3.api.Optional
import com.example.buynest.model.entity.AddressModel
import com.example.buynest.type.MailingAddressInput
import com.example.buynest.ui.theme.MainColor

@Composable
fun EditAddressSheet(
    initialAddress: AddressModel,
    onDismiss: () -> Unit,
    onSave: (MailingAddressInput) -> Unit
) {
    var name by remember { mutableStateOf(initialAddress.firstName.orEmpty()) }
    var phone by remember { mutableStateOf(initialAddress.phone.orEmpty()) }
    var address1 by remember { mutableStateOf(initialAddress.address1.orEmpty()) }
    var address2 by remember { mutableStateOf(initialAddress.address2.orEmpty()) }

    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)) {

        Text("Edit Address", style = MaterialTheme.typography.h6)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Receiver Name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Phone") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = address1,
            onValueChange = { address1 = it },
            label = { Text("Street Address") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = address2,
            onValueChange = { address2 = it },
            label = { Text("Label - Landmark (e.g., Near Mall)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = MaterialTheme.colors.error)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = {
                onSave(
                    MailingAddressInput(
                        firstName = Optional.Present(name),
                        phone = Optional.Present(phone),
                        address1 = Optional.Present(address1),
                        address2 = Optional.Present(address2)
                    )
                )
            }) {
                Text("Save" , color = MainColor)
            }
        }
    }
}
