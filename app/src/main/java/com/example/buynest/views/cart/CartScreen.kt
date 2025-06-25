package com.example.buynest.views.cart

import android.annotation.SuppressLint
import android.util.Log
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.buynest.BuildConfig
import com.example.buynest.model.entity.CartItem
import com.example.buynest.model.data.remote.rest.RemoteDataSourceImpl
import com.example.buynest.model.data.remote.rest.StripeClient
import com.example.buynest.repository.FirebaseAuthObject
import com.example.buynest.repository.payment.PaymentRepositoryImpl
import com.example.buynest.ui.theme.LightGray2
import com.example.buynest.utils.AppConstants.KEY_CART_ID
import com.example.buynest.utils.AppConstants.KEY_CUSTOMER_TOKEN
import com.example.buynest.utils.SecureSharedPrefHelper
import com.example.buynest.viewmodel.address.AddressViewModel
import com.example.buynest.viewmodel.cart.CartViewModel
import com.example.buynest.viewmodel.discount.DiscountViewModel
import com.example.buynest.viewmodel.payment.PaymentViewModel
import com.example.buynest.views.cart.components.AddressSheet
import com.example.buynest.views.cart.components.CouponSheet
import com.example.buynest.views.component.BottomSection
import com.example.buynest.views.component.CartItemRow
import com.example.buynest.views.component.CartTopBar
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import com.stripe.android.paymentsheet.rememberPaymentSheet
import kotlinx.coroutines.launch
import androidx.compose.material3.rememberModalBottomSheetState
import com.example.buynest.model.state.SheetType
import com.example.buynest.utils.SharedPrefHelper


@SuppressLint("ViewModelConstructorInComposable")
@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    onBackClicked: () -> Unit,
    goTOAddress: () -> Unit,
    cartViewModel: CartViewModel,
    addressViewModel: AddressViewModel,
    discountViewModel: DiscountViewModel
) {
    val context = LocalContext.current
    val cartId = SecureSharedPrefHelper.getString(KEY_CART_ID)
    var cartItems by remember { mutableStateOf(emptyList<CartItem>()) }
    val cartState by cartViewModel.cartResponse.collectAsState()

    var showConfirmDialog by remember { mutableStateOf(false) }
    var itemToDelete by remember { mutableStateOf<CartItem?>(null) }

    val defaultAddress by addressViewModel.defaultAddress.collectAsStateWithLifecycle()
    val draftOrderid by cartViewModel.orderResponse.collectAsStateWithLifecycle()
    val token = SecureSharedPrefHelper.getString(KEY_CUSTOMER_TOKEN).toString()

    var activeSheet by remember { mutableStateOf<SheetType>(SheetType.None) }
    val sheetState = rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()

    val paymentViewModel = PaymentViewModel(
        repository = PaymentRepositoryImpl(RemoteDataSourceImpl(StripeClient.api))
    )
    val paymentSheet = rememberPaymentSheet(
        paymentResultCallback = { result ->
            when (result) {
                is PaymentSheetResult.Completed -> {
                    val orderId = draftOrderid?.data?.draftOrderCreate?.draftOrder?.id
                    if (orderId != null) {
                        cartViewModel.completeOrder(orderId)
                        cartItems.forEach { item ->
                            cartViewModel.removeItemFromCart(cartId!!, item.lineId)
                        }
                        cartItems = emptyList()
                    }else{
                        Log.i("TAG", "CartScreen: DraftOrderId is null ")
                    }
                }
                is PaymentSheetResult.Canceled -> {}
                is PaymentSheetResult.Failed -> {}
            }
        }
    )

    LaunchedEffect(Unit) {
        PaymentConfiguration.init(context, BuildConfig.STRIPE_PUBLISHABLE_KEY)
        SecureSharedPrefHelper.getString(KEY_CART_ID)?.let {
            cartViewModel.getCart(it)
        }
        addressViewModel.loadDefaultAddress(token)
    }

    LaunchedEffect(cartState) {
        cartItems = cartState?.data?.cart?.lines?.edges?.mapNotNull { edge ->
            val node = edge.node
            val variant = node.merchandise.onProductVariant ?: return@mapNotNull null
            val product = variant.product
            val price = variant.priceV2.amount.toString().toDoubleOrNull()?.toInt() ?: 0
            val color = variant.selectedOptions.firstOrNull { it.name == "Color" }?.value ?: "Default"
            val size = variant.selectedOptions.firstOrNull { it.name == "Size" }?.value?.toIntOrNull() ?: 0
            val imageUrl = variant.image?.url?.toString() ?: ""

            CartItem(
                id = "${node.id}-$size-$color".hashCode(),
                lineId = node.id,
                name = product.title,
                price = price,
                color = color,
                size = size,
                imageUrl = imageUrl,
                quantity = node.quantity,
                variantId = variant.id
            )
        } ?: emptyList()


    }

    var totalPrice = cartItems.sumOf { it.price * it.quantity }

    fun launchCheckoutFlow() {
        activeSheet = SheetType.Coupon
    }

    LaunchedEffect(activeSheet) {
        coroutineScope.launch {
            when (activeSheet) {
                is SheetType.None -> {
                    if (sheetState.isVisible) {
                        sheetState.hide()
                    }
                }
                else -> {
                    if (!sheetState.isVisible) {
                        sheetState.show()
                    }
                }
            }
        }
    }

    if (activeSheet != SheetType.None) {
        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = {
                coroutineScope.launch {
                    sheetState.hide()
                    activeSheet = SheetType.None
                }
            },
            shape = RoundedCornerShape(20.dp),
            tonalElevation = 4.dp
        ) {
            when (activeSheet) {
                is SheetType.Coupon -> CouponSheet(
                    onValidCoupon = { coupon ->
                        var discount:Double  = discountViewModel.applyCoupon(coupon)
                        totalPrice = (totalPrice * (1 - discount)).toInt()
                        activeSheet = SheetType.Address
                    },
                    checkCoupon = { coupon ->
                        discountViewModel.isCouponValid(coupon)
                    },
                    onSkip = {
                        activeSheet = SheetType.Address
                    }
                )

                is SheetType.Address -> AddressSheet(
                    defaultAddress = defaultAddress,
                    onNavigateToAddress = { goTOAddress() },
                    onProceed = {
                        val email = FirebaseAuthObject.getAuth().currentUser?.email ?: return@AddressSheet

                        cartViewModel.getOrderModelFromCart(email, defaultAddress, cartItems)

                        val method = SharedPrefHelper.getPaymentMethod(context)

                        if (method == "Cash on Delivery") {
                            Toast.makeText(context, "Order placed successfully!", Toast.LENGTH_SHORT).show()
                            coroutineScope.launch {
                                sheetState.hide()
                                activeSheet = SheetType.None
                            }
                        } else if (totalPrice >= 20000) {
                            paymentViewModel.initiatePaymentFlow(
                                amount = totalPrice * 100,
                                onClientSecretReady = { secret ->
                                    paymentSheet.presentWithPaymentIntent(
                                        paymentIntentClientSecret = secret,
                                        configuration = PaymentSheet.Configuration("BuyNest")
                                    )
                                }
                            )
                        }
                    }
                )
                else -> Spacer(modifier = Modifier.height(1.dp))
            }
        }
    }

    Scaffold(
        modifier = Modifier.padding(top = 8.dp),
        topBar = { CartTopBar(backClicked = onBackClicked) },
        bottomBar = {
            BottomSection(totalPrice, Icons.Default.ArrowRightAlt, "Check Out") {
                launchCheckoutFlow()
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .padding(paddingValues)
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
                            null -> Alignment.Center
                        }

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
                                backgroundColor = if (dismissState.dismissDirection != null) Color.Red else Color.LightGray
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(20.dp),
                                    contentAlignment = alignment
                                ) {
                                    Text("Delete", color = Color.White, fontSize = 20.sp)
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
                                // TODO: handle item click
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
                    itemToDelete?.let { item ->
                        cartId?.let {
                            cartViewModel.removeItemFromCart(it, item.lineId)
                        }
                        cartItems = cartItems.filterNot { it.id == item.id }
                    }
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
