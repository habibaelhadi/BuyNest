package com.example.buynest.views.component

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.buynest.repository.FirebaseAuthObject
import com.example.buynest.ui.theme.Gray
import com.example.buynest.ui.theme.MainColor
import com.example.buynest.views.home.phenomenaBold

@Composable
fun SearchBar(onCartClicked: () -> Unit, onSearchClicked: () -> Unit) {
    val showGuestDialog = remember { mutableStateOf(false) }
    val user = FirebaseAuthObject.getAuth().currentUser
    Column {
        Spacer(modifier = Modifier.height(20.dp))
        Text("BuyNest", fontSize = 20.sp,
            fontFamily = phenomenaBold, color = MainColor
        )
        Spacer(modifier = Modifier.height(18.dp))
        Row (
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ){
            OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    placeholder = { Text("What do you search for?") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    modifier = Modifier
                        .weight(30F)
                        .background(Color.White)
                        .clickable {
                            Log.i("TAG", "SearchBar: *********************")
                            onSearchClicked()
                        },
                    readOnly = true,
                    enabled = false,
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MainColor,
                        unfocusedBorderColor = Gray
                    )
                )

            Spacer(modifier = Modifier.width(20.dp))
            IconButton(
                onClick =
                {
                    if (user == null){
                        showGuestDialog.value = true
                    }else{
                        onCartClicked()
                    }
                },
                modifier = Modifier.weight(3F)
            ) {
                Icon(
                    Icons.Default.ShoppingCart,
                    contentDescription = null, tint = MainColor,
                    modifier = Modifier.size(35.dp),
                )
            }
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
