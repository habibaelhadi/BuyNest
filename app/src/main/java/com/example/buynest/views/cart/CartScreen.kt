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
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.buynest.BuildConfig
import com.example.buynest.repository.payment.datasource.PaymentDataSourceImpl
import com.example.buynest.model.data.remote.rest.StripeClient
import com.example.buynest.model.entity.CartItem
import com.example.buynest.model.state.SheetType
import com.example.buynest.repository.FirebaseAuthObject
import com.example.buynest.repository.payment.PaymentRepositoryImpl
import com.example.buynest.ui.theme.LightGray2
import com.example.buynest.utils.AppConstants
import com.example.buynest.utils.SecureSharedPrefHelper
import com.example.buynest.utils.SharedPrefHelper
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
import com.example.buynest.viewmodel.currency.CurrencyViewModel


@SuppressLint("ViewModelConstructorInComposable")
@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    onBackClicked: () -> Unit,
    goTOAddress: () -> Unit,
    cartViewModel: CartViewModel,
    addressViewModel: AddressViewModel,
    discountViewModel: DiscountViewModel,
    currencyViewModel: CurrencyViewModel
) {
    val context = LocalContext.current
    val cartId = SecureSharedPrefHelper.getString(AppConstants.KEY_CART_ID)
    var cartItems by remember { mutableStateOf(emptyList<CartItem>()) }
    val cartState by cartViewModel.cartResponse.collectAsState()

    val defaultAddress by addressViewModel.defaultAddress.collectAsStateWithLifecycle()
    val draftOrderId by cartViewModel.orderResponse.collectAsStateWithLifecycle()
    val token = SecureSharedPrefHelper.getString(AppConstants.KEY_CUSTOMER_TOKEN).toString()

    var activeSheet by remember { mutableStateOf<SheetType>(SheetType.None) }
    val sheetState = rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()

    val paymentViewModel = PaymentViewModel(
        repository = PaymentRepositoryImpl(PaymentDataSourceImpl(StripeClient.api))
    )
    val paymentSheet = rememberPaymentSheet(
        paymentResultCallback = { result ->
            when (result) {
                is PaymentSheetResult.Completed -> {
                    val orderId = draftOrderId?.data?.draftOrderCreate?.draftOrder?.id
                    if (orderId != null) {
                        cartViewModel.completeOrder(orderId)
                        cartItems.forEach { item ->
                            cartViewModel.removeItemFromCart(cartId!!, item.lineId)
                        }
                        cartItems = emptyList()
                    } else {
                        Log.i("TAG", "CartScreen: DraftOrderId is null ")
                    }
                }

                PaymentSheetResult.Canceled -> TODO()
                is PaymentSheetResult.Failed -> TODO()
            }
        }
    )
    var originalTotal by remember { mutableIntStateOf(0) }
    var totalPrice by remember { mutableIntStateOf(0) }
    var discount by remember { mutableStateOf(0.0) }

    LaunchedEffect(Unit) {
        PaymentConfiguration.init(context, BuildConfig.STRIPE_PUBLISHABLE_KEY)
        cartId?.let { cartViewModel.getCart(it) }
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
            val maxQuantity = variant.quantityAvailable?.toInt() ?:0
            CartItem(
                id = "${node.id}-$size-$color".hashCode(),
                lineId = node.id,
                name = product.title,
                price = price,
                color = color,
                size = size,
                imageUrl = imageUrl,
                quantity = node.quantity,
                variantId = variant.id,
                maxQuantity = maxQuantity
            )
        } ?: emptyList()

        originalTotal = cartItems.sumOf { it.price * it.quantity }
        totalPrice = (originalTotal * (1 - discount)).toInt()
    }

    fun launchCheckoutFlow() { activeSheet = SheetType.Coupon }

    LaunchedEffect(activeSheet) {
        coroutineScope.launch {
            if (activeSheet != SheetType.None) sheetState.show() else sheetState.hide()
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
                SheetType.Coupon -> CouponSheet(
                    onValidCoupon = { coupon ->
                        discount = discountViewModel.applyCoupon(coupon)
                        originalTotal = cartItems.sumOf { it.price * it.quantity }
                        totalPrice = (originalTotal * (1 - discount)).toInt()
                        activeSheet = SheetType.Address
                    },
                    checkCoupon = { discountViewModel.isCouponValid(it) },
                    onSkip = { activeSheet = SheetType.Address }
                )

                SheetType.Address -> AddressSheet(
                    defaultAddress = defaultAddress,
                    onNavigateToAddress = goTOAddress,
                    onProceed = {
                        val email = FirebaseAuthObject.getAuth().currentUser?.email ?: return@AddressSheet
                        val method = SharedPrefHelper.getPaymentMethod(context)
                        val isCOD = method == "Cash on Delivery" && totalPrice < 10000

                        cartViewModel.getOrderModelFromCart(email, defaultAddress, cartItems, !isCOD)

                        if (isCOD) {
                            Toast.makeText(context, "Order placed successfully!", Toast.LENGTH_SHORT).show()
                            coroutineScope.launch {
                                sheetState.hide()
                                activeSheet = SheetType.None
                            }
                            draftOrderId?.data?.draftOrderCreate?.draftOrder?.id?.let { orderId ->
                                cartViewModel.completeOrder(orderId)
                                cartItems.forEach { cartViewModel.removeItemFromCart(cartId!!, it.lineId) }
                                cartItems = emptyList()
                            }
                        } else {
                            paymentViewModel.initiatePaymentFlow(
                                amount = totalPrice * 100,
                                onClientSecretReady = { clientSecret ->
                                    paymentSheet.presentWithPaymentIntent(
                                        paymentIntentClientSecret = clientSecret,
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
                    confirmStateChange = {
                        if (it == DismissValue.DismissedToEnd || it == DismissValue.DismissedToStart) {
                            cartViewModel.removeItemFromCart(cartId!!, item.lineId)
                            cartItems = cartItems.filterNot { it.id == item.id }
                            originalTotal = cartItems.sumOf { i -> i.price * i.quantity }
                            totalPrice = (originalTotal * (1 - discount)).toInt()
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
                            onQuantityChange = { id, qty ->
                                cartItems = cartItems.map {
                                    if (it.id == id) it.copy(quantity = qty) else it
                                }
                                originalTotal = cartItems.sumOf { it.price * it.quantity }
                                totalPrice = (originalTotal * (1 - discount)).toInt()
                            },
                            onDelete = { id ->
                                cartViewModel.removeItemFromCart(cartId!!, item.lineId)
                                cartItems = cartItems.filterNot { it.id == id }
                                originalTotal = cartItems.sumOf { it.price * it.quantity }
                                totalPrice = (originalTotal * (1 - discount)).toInt()
                            },
                            onItemClick = {}
                        )
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
        }