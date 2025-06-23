package com.example.buynest.views.productInfo

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.buynest.repository.FirebaseAuthObject
import com.example.buynest.ui.theme.MainColor
import com.example.buynest.views.component.GuestAlertDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductInfoTopBar(
    backClicked :()->Unit,
    navigateToCart :()->Unit
){
    val showGuestDialog = remember { mutableStateOf(false) }
    val user = FirebaseAuthObject.getAuth().currentUser
    CenterAlignedTopAppBar(
        title = {
            Text(
                "Product Details",
                fontSize = 20.sp,
                fontWeight = Bold,
                color = MainColor
            )
        },
        navigationIcon = {
            IconButton(onClick = {
                backClicked()
            }) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = null,
                    tint = MainColor
                )
            }
        },
        actions = {
            IconButton(onClick = {
                if (user == null){
                    showGuestDialog.value = true
                }else{
                    navigateToCart()
                }
            }) {
                Icon(
                    Icons.Default.ShoppingCart,
                    contentDescription = null,
                    tint = MainColor,
                    modifier = Modifier.size(35.dp)
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
    )
    GuestAlertDialog(
        showDialog = showGuestDialog.value,
        onDismiss = { showGuestDialog.value = false },
        onConfirm = {
            showGuestDialog.value = false
        }
    )
}