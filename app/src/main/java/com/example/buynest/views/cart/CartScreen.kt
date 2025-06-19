package com.example.buynest.views.cart

import android.annotation.SuppressLint
import android.widget.Toast
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowRightAlt
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.buynest.BuildConfig
import com.example.buynest.R
import com.example.buynest.model.entity.CartItem
import com.example.buynest.model.remote.rest.RemoteDataSource
import com.example.buynest.model.remote.rest.StripeAPI
import com.example.buynest.model.remote.rest.StripeClient
import com.example.buynest.repository.payment.PaymentRepository
import com.example.buynest.ui.theme.LightGray2
import com.example.buynest.viewmodel.payment.PaymentViewModel
import com.example.buynest.views.component.BottomSection
import com.example.buynest.views.component.CartItemRow
import com.example.buynest.views.component.CartTopBar
import com.stripe.android.PaymentConfiguration
import com.stripe.android.customersheet.injection.CustomerSheetViewModelModule_Companion_ContextFactory.context
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import com.stripe.android.paymentsheet.rememberPaymentSheet

@SuppressLint("ViewModelConstructorInComposable")
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CartScreen(
    onBackClicked: () -> Unit
) {
    var cartItems by remember {
        mutableStateOf(
            listOf(
                CartItem(1, "Nike Air Jordan", 3500, "Orange", 40, R.drawable.product, 1),
                CartItem(2, "Adidas Runner", 2800, "Blue", 42, R.drawable.product, 2)
            )
        )
    }

    val paymentViewModel = PaymentViewModel(
        repository = PaymentRepository(
            RemoteDataSource(StripeClient.api)
        )
    )
    val context = LocalContext.current

    val paymentSheet = rememberPaymentSheet(
        paymentResultCallback = { result ->
            when (result) {
                is PaymentSheetResult.Completed -> {
                    // Payment completed successfully
                }
                is PaymentSheetResult.Canceled -> {
                    // Payment canceled
                }
                is PaymentSheetResult.Failed -> {
                    // Payment failed
                }
            }
        }
    )
    LaunchedEffect(Unit) {
        PaymentConfiguration.init(
            context,
            BuildConfig.STRIPE_PUBLISHABLE_KEY
        )
    }

    var showConfirmDialog by remember { mutableStateOf(false) }
    var itemToDelete by remember { mutableStateOf<CartItem?>(null) }

    val totalPrice = cartItems.sumOf { it.price * it.quantity }

    Scaffold(
        modifier = Modifier.padding(top = 32.dp),
        topBar = { CartTopBar(
            backClicked = onBackClicked
        ) },
        bottomBar = {
            BottomSection(totalPrice, Icons.Default.ArrowRightAlt, "Check Out") {
                paymentViewModel.initiatePaymentFlow(
                    amount = totalPrice*100 ,
                    onClientSecretReady = {
                        paymentViewModel.initiatePaymentFlow(
                            amount = totalPrice * 100,
                            onClientSecretReady = { secret ->
                                paymentSheet.presentWithPaymentIntent(
                                    paymentIntentClientSecret = secret,
                                    configuration = PaymentSheet.Configuration(
                                        merchantDisplayName = "BuyNest"
                                    )
                                )
                            }
                        )
                    }
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .padding(paddingValues),
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
                            },
                            onItemClick = {
                                /* Handle item click here*/
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
            },
            shape = RoundedCornerShape(12.dp)
        )
    }
}
