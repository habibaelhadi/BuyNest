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
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.unit.dp
import com.example.buynest.model.entity.Address
import com.example.buynest.ui.theme.MainColor
import com.example.buynest.utils.AppConstants.KEY_CUSTOMER_TOKEN
import com.example.buynest.utils.SecureSharedPrefHelper
import com.example.buynest.viewmodel.address.AddressViewModel
import com.example.buynest.views.component.AddressItem

@Composable
fun AddressScreen(
    onBackClicked: () -> Unit,
    onMapClicked: () -> Unit,
    addressViewModel: AddressViewModel
) {
    val addressList by addressViewModel.addresses.collectAsState()
    var selectedIndex by remember { mutableStateOf(-1) }
    val error by addressViewModel.error.collectAsState()

    LaunchedEffect(Unit) {
        addressViewModel.loadAddresses(SecureSharedPrefHelper.getString(KEY_CUSTOMER_TOKEN).toString())
    }

    Scaffold(
        modifier = Modifier.padding(top = 60.dp),
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
                        onClick = { onBackClicked() },
                        modifier = Modifier.align(Alignment.CenterStart)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
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
                    onClick = {
                        onMapClicked()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = MainColor
                    )
                ) {
                    TextButton(
                        onClick = { onMapClicked() },
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add", tint = White)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add New Address", color = White)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (error != null) {
                    Text(
                        text = error ?: "",
                        color = MaterialTheme.colors.error,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                if (addressList.isEmpty()) {
                    Text("No addresses found", modifier = Modifier.align(Alignment.CenterHorizontally))
                } else {
                    LazyColumn {
                        itemsIndexed(addressList) { index, address ->
                            AddressItem(
                                label = address.address1 ?: "No label",
                                icon = when (address.address2?.lowercase()) {
                                    "home" -> Icons.Default.Home
                                    "office" -> Icons.Default.Work
                                    "friend" -> Icons.Default.Person
                                    else -> Icons.Default.LocationOn
                                },
                                address = address.address1 ?: "",
                                phone = address.phone ?: "",
                                isSelected = selectedIndex == index,
                                onSelect = {
                                    selectedIndex = index
                                    // You can add logic here to set this address as default via ViewModel if you want
                                },
                                onMapClick = {
                                    Log.d("AddressScreen", "View on map clicked for ${address.address1}")
                                }
                            )
                        }
                    }
                }
            }
        }
    )
}


