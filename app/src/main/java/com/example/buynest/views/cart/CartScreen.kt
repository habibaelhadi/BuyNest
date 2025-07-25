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
import com.apollographql.apollo3.api.ApolloResponse
import com.example.buynest.BuildConfig
import com.example.buynest.GetCartQuery
import com.example.buynest.model.repository.payment.datasource.PaymentDataSourceImpl
import com.example.buynest.model.data.remote.rest.StripeClient
import com.example.buynest.model.entity.CartItem
import com.example.buynest.model.mapper.mapSizeFromTextToInteger
import com.example.buynest.model.state.SheetType
import com.example.buynest.model.state.UiResponseState
import com.example.buynest.model.repository.FirebaseAuthObject
import com.example.buynest.model.repository.payment.PaymentRepositoryImpl
import com.example.buynest.ui.theme.LightGray2
import com.example.buynest.ui.theme.MainColor
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
import com.example.buynest.views.component.Indicator
import com.example.buynest.views.component.snackbar.CustomSnackbar
import com.example.buynest.views.favourites.NoDataLottie
import com.example.buynest.views.home.phenomenaBold


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
    val cartState by cartViewModel.cartUiState.collectAsState()
    var itemToDelete by remember { mutableStateOf<CartItem?>(null) }
    var showConfirmDialog by remember { mutableStateOf(false) }

    val defaultAddress by addressViewModel.defaultAddress.collectAsStateWithLifecycle()
    val draftOrderId by cartViewModel.orderResponse.collectAsStateWithLifecycle()
    val token = SecureSharedPrefHelper.getString(AppConstants.KEY_CUSTOMER_TOKEN).toString()

    var activeSheet by remember { mutableStateOf<SheetType>(SheetType.None) }
    val sheetState = rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()

    val rate by currencyViewModel.rate
    val currencySymbol by currencyViewModel.currencySymbol
    var snackbarMessage by remember { mutableStateOf<String?>(null) }

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

                PaymentSheetResult.Canceled -> {}
                is PaymentSheetResult.Failed -> {}
            }
        }
    )
    var discount by remember { mutableStateOf(0.0) }
    val originalTotal by remember(cartItems) {
        derivedStateOf { cartItems.sumOf { it.price * it.quantity } }
    }
    val totalPrice by remember(originalTotal, discount) {
        derivedStateOf { (originalTotal * (1 - discount)).toInt() }
    }

    LaunchedEffect(Unit) {
        PaymentConfiguration.init(context, BuildConfig.STRIPE_PUBLISHABLE_KEY)
        cartId?.let { cartViewModel.getCart(it) }
        addressViewModel.loadDefaultAddress(token)
        currencyViewModel.loadCurrency()
    }
    LaunchedEffect(cartState) {
        if (cartState is UiResponseState.Success<*>) {
            val response = (cartState as UiResponseState.Success<*>).data
            val data = (response as? ApolloResponse<GetCartQuery.Data>)?.data
            cartItems = data?.cart?.lines?.edges?.mapNotNull { edge ->
                val node = edge.node
                val variant = node.merchandise.onProductVariant ?: return@mapNotNull null
                val product = variant.product
                val price = variant.priceV2.amount?.toString()?.toDoubleOrNull()?.times(rate)?.toInt() ?: 0
                val color = variant.selectedOptions.firstOrNull { it.name == "Color" }?.value ?: "Default"
                val sizeText = variant.selectedOptions.firstOrNull { it.name.equals("Size", ignoreCase = true) }?.value.orEmpty()
                val size = mapSizeFromTextToInteger(sizeText) ?: 0
                val imageUrl = variant.image?.url?.toString() ?: ""
                val maxQuantity = variant.quantityAvailable?.toInt() ?: 0
                CartItem(
                    id = "${node.id}-$size-$color".hashCode(),
                    lineId = node.id,
                    name = product.title,
                    price = price,
                    currencySymbol = currencySymbol.toString(),
                    color = color,
                    size = size,
                    imageUrl = imageUrl,
                    quantity = node.quantity,
                    variantId = variant.id,
                    maxQuantity = maxQuantity
                )
            } ?: emptyList()
        }
    }

    fun launchCheckoutFlow() {
        activeSheet = SheetType.Coupon
    }

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
                        activeSheet = SheetType.Address
                    },
                    checkCoupon = { discountViewModel.isCouponValid(it) },
                    onSkip = { activeSheet = SheetType.Address }
                )

                SheetType.Address -> AddressSheet(
                    defaultAddress = defaultAddress,
                    onNavigateToAddress = goTOAddress,
                    onProceed = {
                        val email =
                            FirebaseAuthObject.getAuth().currentUser?.email ?: return@AddressSheet
                        val method = SharedPrefHelper.getPaymentMethod(context)
                        val isCOD = method == "Cash on Delivery" && totalPrice < 10000

                        cartViewModel.getOrderModelFromCart(
                            email,
                            defaultAddress,
                            cartItems,
                            !isCOD,
                            discount
                        )

                        if (isCOD) {
                            Toast.makeText(
                                context,
                                "Order placed successfully!",
                                Toast.LENGTH_SHORT
                            ).show()
                            coroutineScope.launch {
                                sheetState.hide()
                                activeSheet = SheetType.None
                            }
                            draftOrderId?.data?.draftOrderCreate?.draftOrder?.id?.let { orderId ->
                                Log.i("TAG", "CartScreen: $orderId ")
                                cartViewModel.completeOrder(orderId)
                                cartItems.forEach {
                                    cartViewModel.removeItemFromCart(
                                        cartId!!,
                                        it.lineId
                                    )
                                }
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
                            activeSheet = SheetType.None
                        }
                    }
                )

                else -> Spacer(modifier = Modifier.height(1.dp))
            }
        }
    }

    when (cartState) {
        is UiResponseState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Indicator()
            }
            return
        }
        is UiResponseState.Error -> {
            val errorMsg = (cartState as UiResponseState.Error).message
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Failed to load cart: $errorMsg", color = MaterialTheme.colors.error)
            }
            return
        }
        else -> {}
    }

    Scaffold(
        modifier = Modifier.padding(top = 8.dp),
        topBar = { Column {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                "BuyNest", fontSize = 20.sp,
                fontFamily = phenomenaBold, color = MainColor
            )
            CartTopBar(backClicked = onBackClicked) } },
        bottomBar = {
            BottomSection(totalPrice, Icons.Default.ArrowRightAlt, "Check Out", currencySymbol) {
                launchCheckoutFlow()
            }
        }
    ) { paddingValues ->
        if (cartItems.isEmpty()){
            NoDataLottie(false)
        }else {

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
                                itemToDelete = item
                                showConfirmDialog = true
                                false
                            } else true
                        }
                    )
                    SwipeToDismiss(
                        state = dismissState,
                        directions = setOf(
                            DismissDirection.StartToEnd,
                            DismissDirection.EndToStart
                        ),
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
                                },
                                onDelete = { id ->
                                    itemToDelete = cartItems.find { it.id == id }
                                    showConfirmDialog = true
                                },
                                onItemClick = {},
                                onLimitReached = {
                                    snackbarMessage = "Oops! That’s the maximum quantity available"
                                }
                            )
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
        snackbarMessage?.let { message ->
            CustomSnackbar (message = message) {
                snackbarMessage = null
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
