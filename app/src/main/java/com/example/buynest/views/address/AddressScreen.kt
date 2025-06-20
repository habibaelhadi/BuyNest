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
import com.example.buynest.views.component.AddressItem

@Composable
fun AddressScreen(
    onBackClicked: () -> Unit,
    onMapClicked: () -> Unit
) {
    val addressList = remember {
        mutableStateListOf(
            Address(
                "Home",
                Icons.Default.Home,
                "12 , Raml Station, Alexandria",
                "+20 100 123 4567"
            ),
            Address(
                "Office",
                Icons.Default.Work,
                "35 Fawzy Moaz Street, Smouha, Alexandria",
                "+20 122 345 6789"
            ),
            Address(
                "Ahmed’s House",
                Icons.Default.Person,
                "22 , Cleopatra, Alexandria",
                "+20 111 234 5678"
            ),
            Address(
                "Other Location",
                Icons.Default.LocationOn,
                "5 , Stanley, Alexandria",
                "+20 109 876 5432"
            )
        )
    }

    var selectedIndex by remember { mutableStateOf(0) }

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
                        addressList[0]
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

                LazyColumn {
                    itemsIndexed(addressList) { index, address ->
                        AddressItem(
                            label = address.label,
                            icon = address.icon,
                            address = address.address,
                            phone = address.phone,
                            isSelected = selectedIndex == index,
                            onSelect = { selectedIndex = index },
                            onMapClick = {
                                Log.d("AddressScreen", "View on map clicked for ${address.label}")
                            }
                        )
                    }
                }
            }
        }
    )
}


