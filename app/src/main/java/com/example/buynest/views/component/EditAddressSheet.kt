package com.example.buynest.views.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.apollographql.apollo3.api.Optional
import com.example.buynest.model.entity.AddressModel
import com.example.buynest.type.MailingAddressInput
import com.example.buynest.ui.theme.MainColor
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import com.example.buynest.utils.AppConstants.ADDRESS_TYPE_FRIEND
import com.example.buynest.utils.AppConstants.ADDRESS_TYPE_HOME
import com.example.buynest.utils.AppConstants.ADDRESS_TYPE_OFFICE
import com.example.buynest.utils.AppConstants.ADDRESS_TYPE_OTHER


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun EditAddressSheet(
    initialAddress: AddressModel,
    onDismiss: () -> Unit,
    onSave: (MailingAddressInput) -> Unit
) {
    var name by remember { mutableStateOf(initialAddress.firstName.orEmpty()) }
    var phone by remember { mutableStateOf(initialAddress.phone.orEmpty()) }
    var address1 by remember { mutableStateOf(initialAddress.address1.orEmpty()) }
    val initialLabel = initialAddress.address2?.split("-")?.firstOrNull()?.trim().orEmpty()
    val initialLandmark = initialAddress.address2?.split("-")?.lastOrNull()?.trim().orEmpty()

    var labelType by remember { mutableStateOf(initialLabel) }
    var landmark by remember { mutableStateOf(initialLandmark) }

    var nameError by remember { mutableStateOf<String?>(null) }
    var phoneError by remember { mutableStateOf<String?>(null) }
    var address1Error by remember { mutableStateOf<String?>(null) }
    var initialLabelError by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Edit Address", style = MaterialTheme.typography.h6)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = name,
            onValueChange = {
                name = it
                nameError = null
            },
            label = { Text("Receiver's name") },
            isError = nameError != null,
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = MainColor,
                cursorColor = MainColor,
                focusedLabelColor = MainColor
            ),
            singleLine = true
        )
        if (nameError != null) {
            Text(nameError!!, color = MaterialTheme.colors.error, style = MaterialTheme.typography.caption)
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = phone,
            onValueChange = {
                if (it.isEmpty() || it.all { char -> char.isDigit() }) {
                    phone = it
                    phoneError = null
                }
            },
            label = { Text("Phone") },
            isError = phoneError != null,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number
            ),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = MainColor,
                cursorColor = MainColor,
                focusedLabelColor = MainColor
            ),
            singleLine = true
        )


        if (phoneError != null) {
            Text(phoneError!!, color = MaterialTheme.colors.error, style = MaterialTheme.typography.caption)
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = address1,
            onValueChange = {
                address1 = it
                address1Error = null
            },
            label = { Text("Street Address") },
            isError = address1Error != null,
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = MainColor,
                cursorColor = MainColor,
                focusedLabelColor = MainColor
            ),
            singleLine = true
        )
        if (address1Error != null) {
            Text(address1Error!!, color = MaterialTheme.colors.error, style = MaterialTheme.typography.caption)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text("Label Type", style = MaterialTheme.typography.subtitle1)
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            listOf(
                ADDRESS_TYPE_HOME,
                ADDRESS_TYPE_OFFICE,
                ADDRESS_TYPE_FRIEND,
                ADDRESS_TYPE_OTHER
            ).forEach { type ->
                val isSelected = labelType == type
                FilterChip(
                    selected = isSelected,
                    onClick = { labelType = type },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MainColor,
                        containerColor = Color.White
                    ),
                    label = {
                        Text(
                            text = type.replaceFirstChar { it.uppercase() },
                            color = if (isSelected) Color.White else Color.Black
                        )
                    }
                )
        }
    }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = landmark,
            onValueChange = { landmark = it },
            label = { Text("Landmark (e.g., Near Mall)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = MainColor,
                cursorColor = MainColor,
                focusedLabelColor = MainColor
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(MainColor),
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Cancel", color = Color.White)
            }

            Spacer(modifier = Modifier.width(16.dp))

            Button(
                onClick = {
                    var isValid = true

                    if (name.isBlank()) {
                        nameError = "Name cannot be empty"
                        isValid = false
                    }
                    if (phone.isBlank()) {
                        phoneError = "Phone cannot be empty"
                        isValid = false
                    }
                    if (address1.isBlank()) {
                        address1Error = "Address cannot be empty"
                        isValid = false
                    }
                    if (labelType.isBlank()) {
                        initialLabelError = "Label type cannot be empty"
                        isValid = false
                    }

                    if (isValid) {
                        onSave(
                            MailingAddressInput(
                                firstName = Optional.Present(name),
                                phone = Optional.Present(phone),
                                address1 = Optional.Present(address1),
                                address2 = Optional.Present("${labelType.trim()} - ${landmark.trim()}")
                            )
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(MainColor),
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Save", color = Color.White)
            }
        }
    }
}
