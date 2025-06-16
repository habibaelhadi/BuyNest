package com.example.buynest.views.cart

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.SwipeToDismiss
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.buynest.R
import com.example.buynest.ui.theme.LightGray2
import com.example.buynest.views.component.BottomSection
import com.example.buynest.views.component.CartItemRow
import com.example.buynest.views.component.CartTopBar

data class CartItem(
    val id: Int,
    val name: String,
    val price: Int,
    val color: String,
    val size: Int,
    val imageRes: Int,
    val quantity: Int
)

@OptIn(ExperimentalMaterialApi::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun CartScreen(navController: NavController) {
    var cartItems by remember {
        mutableStateOf(
            listOf(
                CartItem(1, "Nike Air Jordan", 3500, "Orange", 40, R.drawable.product, 1),
                CartItem(2, "Adidas Runner", 2800, "Blue", 42, R.drawable.product, 2)
            )
        )
    }

    var showConfirmDialog by remember { mutableStateOf(false) }
    var itemToDelete by remember { mutableStateOf<CartItem?>(null) }

    val totalPrice = cartItems.sumOf { it.price * it.quantity }

    Scaffold(
        topBar = { CartTopBar(navController) },
        bottomBar = { BottomSection(totalPrice) }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            items(cartItems, key = { it.id }) { item ->
                val dismissState = rememberDismissState(
                    confirmStateChange = { dismissValue ->
                        if (dismissValue == DismissValue.DismissedToStart || dismissValue == DismissValue.DismissedToEnd) {
                            itemToDelete = item
                            showConfirmDialog = true
                            false
                        } else true
                    }
                )

                SwipeToDismiss(
                    state = dismissState,
                    directions = setOf(DismissDirection.StartToEnd, DismissDirection.EndToStart),
                    background = {
                        val alignment = when (dismissState.dismissDirection) {
                            DismissDirection.StartToEnd -> Alignment.CenterStart
                            DismissDirection.EndToStart -> Alignment.CenterEnd
                            null -> Alignment.CenterEnd
                        }

                        val isSwiping = dismissState.dismissDirection != null

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Transparent)
                                .padding(horizontal = 20.dp),
                            contentAlignment = alignment
                        ) {
                            Card(
                                modifier = Modifier.fillMaxSize(),
                                shape = RoundedCornerShape(16.dp),
                                border = BorderStroke(1.dp, LightGray2),
                                backgroundColor = if (isSwiping) Color.Red else Color.LightGray
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 20.dp, vertical = 20.dp),
                                    contentAlignment = alignment
                                ) {
                                    Text(
                                        text = "Delete",
                                        color = Color.White,
                                        fontSize = 20.sp
                                    )
                                }
                            }
                        }
                    },
                    dismissContent = {
                        CartItemRow(
                            item = item,
                            onQuantityChange = { id, newQty ->
                                cartItems = cartItems.map {
                                    if (it.id == id) it.copy(quantity = newQty) else it
                                }
                            },
                            onDelete = { id ->
                                itemToDelete = cartItems.find { it.id == id }
                                showConfirmDialog = true
                            }
                        )
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    if (showConfirmDialog && itemToDelete != null) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("Confirm Deletion") },
            text = { Text("Are you sure you want to delete '${itemToDelete?.name}' from your cart?") },
            confirmButton = {
                TextButton(onClick = {
                    cartItems = cartItems.filterNot { it.id == itemToDelete?.id }
                    itemToDelete = null
                    showConfirmDialog = false
                }) {
                    Text("Delete", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    itemToDelete = null
                    showConfirmDialog = false
                }) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        )
    }
}
