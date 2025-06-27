package com.example.buynest.views.address

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.unit.dp
import com.example.buynest.model.entity.AddressModel
import com.example.buynest.model.state.UiResponseState
import com.example.buynest.ui.theme.MainColor
import com.example.buynest.utils.AppConstants.ADDRESS_TYPE_FRIEND
import com.example.buynest.utils.AppConstants.ADDRESS_TYPE_HOME
import com.example.buynest.utils.AppConstants.ADDRESS_TYPE_OFFICE
import com.example.buynest.utils.AppConstants.KEY_CUSTOMER_TOKEN
import com.example.buynest.utils.SecureSharedPrefHelper
import com.example.buynest.viewmodel.address.AddressViewModel
import com.example.buynest.views.address.component.SettingsBottomSheet
import com.example.buynest.views.component.AddressItem
import com.example.buynest.views.component.EditAddressSheet
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AddressScreen(
    onBackClicked: () -> Unit,
    onMapClicked: () -> Unit,
    addressViewModel: AddressViewModel
) {
    val uiState by addressViewModel.addressUiState.collectAsState()
    val defaultAddress by addressViewModel.defaultAddress.collectAsState()
    val editingAddress by addressViewModel.editingAddress.collectAsState()

    val selectedAddressForSettings = remember { mutableStateOf<AddressModel?>(null) }
    val showDeleteConfirmDialog = remember { mutableStateOf(false) }

    val bottomSheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val coroutineScope = rememberCoroutineScope()

    val token = SecureSharedPrefHelper.getString(KEY_CUSTOMER_TOKEN).toString()

    LaunchedEffect(Unit) {
        addressViewModel.loadAddresses(token)
        addressViewModel.loadDefaultAddress(token)
    }

    ModalBottomSheetLayout(
        sheetState = bottomSheetState,
        sheetContent = {
            when {
                editingAddress != null -> {
                    EditAddressSheet(
                        initialAddress = editingAddress!!,
                        onDismiss = {
                            coroutineScope.launch { bottomSheetState.hide() }
                            addressViewModel.stopEditingAddress()
                        },
                        onSave = { updatedInput ->
                            addressViewModel.updateAddress(token, editingAddress!!.id!!, updatedInput)
                            coroutineScope.launch { bottomSheetState.hide() }
                            addressViewModel.stopEditingAddress()
                        }
                    )
                }

                selectedAddressForSettings.value != null -> {
                    SettingsBottomSheet(
                        onEdit = {
                            selectedAddressForSettings.value?.let {
                                coroutineScope.launch {
                                    bottomSheetState.hide()
                                    addressViewModel.startEditingAddress(it)
                                    bottomSheetState.show()
                                }
                            }
                        },
                        onDelete = {
                            coroutineScope.launch {
                                bottomSheetState.hide()
                                // Delay showing dialog until sheet is hidden
                                while (bottomSheetState.isVisible) {
                                    delay(10)
                                }
                                showDeleteConfirmDialog.value = true
                            }
                        },
                        onDismiss = {
                            coroutineScope.launch {
                                bottomSheetState.hide()
                                selectedAddressForSettings.value = null
                            }
                        }
                    )
                }

                else -> Box(modifier = Modifier.height(1.dp))
            }
        }
    ) {
        Scaffold(
            modifier = Modifier.padding(top = 10.dp),
            topBar = {
                TopAppBar(
                    backgroundColor = White,
                    elevation = 0.dp
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp)
                    ) {
                        IconButton(
                            onClick = onBackClicked,
                            modifier = Modifier.align(Alignment.CenterStart)
                        ) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                        Text(
                            text = "Addresses",
                            style = MaterialTheme.typography.h6,
                            color = MainColor,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
            },
            content = { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp)
                ) {
                    Button(
                        onClick = onMapClicked,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(backgroundColor = MainColor)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add", tint = White)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add New Address", color = White)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    when (val state = uiState) {
                        is UiResponseState.Loading -> {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(color = MainColor)
                            }
                        }

                        is UiResponseState.Error -> {
                            Text(
                                text = state.message,
                                color = MaterialTheme.colors.error,
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )
                        }

                        is UiResponseState.Success<*> -> {
                            val addressList = state.data as? List<AddressModel> ?: emptyList()

                            if (addressList.isEmpty()) {
                                Text("No addresses found", modifier = Modifier.align(Alignment.CenterHorizontally))
                            } else {
                                LazyColumn {
                                    itemsIndexed(addressList) { _, address ->
                                        val labelType = address.address2?.split("-")?.firstOrNull()?.trim()?.lowercase() ?: " "
                                        val landmark = address.address2?.split("-")?.lastOrNull()?.trim().takeUnless { it.isNullOrEmpty() } ?: "-"
                                        val label = labelType.replaceFirstChar { it.uppercase() }

                                        val isSameId = defaultAddress?.id?.normalizedId() == address.id?.normalizedId()

                                        AddressItem(
                                            label = label,
                                            icon = when (label) {
                                                ADDRESS_TYPE_HOME -> Icons.Default.Home
                                                ADDRESS_TYPE_OFFICE -> Icons.Default.Work
                                                ADDRESS_TYPE_FRIEND -> Icons.Default.Person
                                                else -> Icons.Default.LocationOn
                                            },
                                            address = address.address1 ?: "",
                                            phone = address.phone ?: "",
                                            receiverName = address.firstName ?: "",
                                            landmark = landmark,
                                            isSelected = isSameId,
                                            isDefault = isSameId,
                                            onSetDefault = {
                                                Log.d("AddressScreen", "Setting default address ID: ${address.id}")
                                                addressViewModel.setDefaultAddress(token, address.id ?: "")
                                            },
                                            onSettingsClick = {
                                                selectedAddressForSettings.value = address
                                                coroutineScope.launch { bottomSheetState.show() }
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        )

        if (showDeleteConfirmDialog.value && selectedAddressForSettings.value != null) {
            AlertDialog(
                onDismissRequest = {
                    showDeleteConfirmDialog.value = false
                },
                title = { Text("Delete Address") },
                text = { Text("Are you sure you want to delete this address?") },
                confirmButton = {
                    TextButton(onClick = {
                        selectedAddressForSettings.value?.let {
                            addressViewModel.deleteAddress(token, it.id ?: "")
                            addressViewModel.loadAddresses(token)
                        }
                        selectedAddressForSettings.value = null
                        showDeleteConfirmDialog.value = false
                    }) {
                        Text("Delete", color = MaterialTheme.colors.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showDeleteConfirmDialog.value = false
                    }) {
                        Text("Cancel", color = Color.Gray)
                    }
                }
            )
        }
    }
}

private fun String.normalizedId(): String = this.substringBefore("?")
