package com.example.buynest.views.productInfo

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.example.buynest.ProductDetailsByIDQuery
import com.example.buynest.ProductsDetailsByIDsQuery
import com.example.buynest.model.mapper.mapSizeFromTextToInteger
import com.example.buynest.model.mapper.toColorList
import com.example.buynest.model.state.UiResponseState
import com.example.buynest.repository.FirebaseAuthObject
import com.example.buynest.ui.theme.LightGray
import com.example.buynest.ui.theme.MainColor
import com.example.buynest.viewmodel.currency.CurrencyViewModel
import com.example.buynest.viewmodel.favorites.FavouritesViewModel
import com.example.buynest.viewmodel.productInfo.ProductDetailsViewModel
import com.example.buynest.views.component.BottomSection
import com.example.buynest.views.component.ExpandableText
import com.example.buynest.views.component.GuestAlertDialog
import com.example.buynest.views.component.Indicator
import com.example.buynest.views.component.QuantitySelector
import com.example.buynest.views.customsnackbar.CustomSnackbar
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.calculateCurrentOffsetForPage
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import me.saket.telephoto.zoomable.coil.ZoomableAsyncImage
import org.koin.androidx.compose.koinViewModel
import kotlin.math.abs

@Composable
fun ProductInfoScreen(
    productId: String,
    backClicked :()->Unit,
    navigateToCart :()->Unit,
    viewModel: ProductDetailsViewModel = koinViewModel(),
    favViewModel: FavouritesViewModel = koinViewModel(),
    currencyViewModel: CurrencyViewModel
){

    val response by viewModel.productDetails.collectAsStateWithLifecycle()

    var snackbarMessage by remember { mutableStateOf<String?>(null) }
    var totalPrice by remember { mutableIntStateOf(0) }
    var selectedSize by remember { mutableStateOf<String?>(null) }
    var selectedColor by remember { mutableStateOf<String?>(null) }
    var quantity by remember { mutableIntStateOf(1) }
    var maxQuantity by remember { mutableIntStateOf(1) }
    val rate by currencyViewModel.rate
    var actualPrice = (totalPrice*rate).toInt()
    val currencySymbol by currencyViewModel.currencySymbol

    val showGuestDialog = remember { mutableStateOf(false) }
    val user = FirebaseAuthObject.getAuth().currentUser

    LaunchedEffect(Unit) {
        if (user != null){
            favViewModel.getAllFavorites()
        }
        val actualId = "gid://shopify/Product/$productId"
        viewModel.getProductDetails(actualId)
        currencyViewModel.loadCurrency()
    }


    Scaffold (
        topBar = { ProductInfoTopBar(backClicked, navigateToCart) },
        bottomBar = {
            if (response is UiResponseState.Success<*>) {
                val product = (response as UiResponseState.Success<ProductDetailsByIDQuery.Data>).data.product

                val selectedVariantId = product?.variants?.edges
                    ?.mapNotNull { it.node }
                    ?.find { variant ->
                        val options = variant.selectedOptions.associate {
                            it.name.lowercase() to it.value.lowercase()
                        }

                        Log.d("VariantDebug", "Options: $options")

                        Log.i("VariantDebug", "selected size: $selectedSize ")

                        val selected = selectedSize?.lowercase()?.trim()
                        val option = options["size"]?.lowercase()?.trim()

                        val selectedSizeMapped = mapSizeFromTextToInteger(selected ?: "")
                        val optionSizeMapped = mapSizeFromTextToInteger(option ?: "")

                        val sizeMatch = selectedSizeMapped == optionSizeMapped

                        val colorMatch = selectedColor?.lowercase()?.let { options["color"] == it } == true

                        Log.d("VariantDebug", "sizeMatch: $sizeMatch, colorMatch: $colorMatch")

                        sizeMatch && colorMatch
                    }?.id

                maxQuantity = product?.variants?.edges?.get(0)?.node?.quantityAvailable ?: 1

                val currentQuantity = quantity
                BottomSection(totalPrice, Icons.Default.AddShoppingCart, "Add to Cart",currencySymbol) {
                    if (selectedSize.isNullOrBlank() || selectedColor.isNullOrBlank()) {
                        snackbarMessage = "Pick your perfect size and color to continue \uD83D\uDE0A"
                        return@BottomSection
                    }

                    selectedVariantId?.let {
                        viewModel.viewModelScope.launch {
                            viewModel.addToCart(it, currentQuantity)
                            snackbarMessage = "Great! Item added to your cart \uD83C\uDF89"
                        }
                    } ?: run {
                        snackbarMessage = "Sorry, This color isn’t available in the selected size"
                    }
                }
            }
        }
    ) { innerPadding ->
        when (val result = response) {
            is UiResponseState.Error -> {
                Text(text = result.message)
            }
            UiResponseState.Loading ->  {
                Indicator()
            }
            is UiResponseState.Success<*> -> {
                val successData = result as UiResponseState.Success<ProductDetailsByIDQuery.Data>
                val product = successData.data.product

                val rawSizes = product?.options?.getOrNull(0)?.values ?: emptyList()
                val rawColors = product?.options?.getOrNull(1)?.values ?: emptyList()

                val sizes = if (rawSizes.isEmpty()) listOf("1") else rawSizes
                val colors = if (rawColors.isEmpty()) listOf("1") else rawColors

                // Auto-select if only one option
                if (sizes.size == 1) selectedSize = sizes[0]
                if (colors.size == 1) selectedColor = colors[0]

                ProductInfo(
                    innerPadding = innerPadding,
                    product = product,
                    onTotalChange = { updatedTotal -> totalPrice = updatedTotal },
                    favViewModel = favViewModel,
                    onSizePicked = { selectedSize = it },
                    onColorPicked = { selectedColor = it },
                    quantity = quantity,
                    onQuantityChanged = { quantity = it },
                    availableQuantity = maxQuantity,
                    rate = rate,
                    currencySymbol = currencySymbol,
                    onLimitReached = {
                        snackbarMessage = "Oops! That’s the maximum quantity available"
                    }
                )
            }
        }
    }
    snackbarMessage?.let { message ->
        CustomSnackbar(message = message) {
            snackbarMessage = null
        }
    }
}

@SuppressLint("UnrememberedMutableState")
@Composable
fun ProductInfo(
    innerPadding: PaddingValues,
    product: ProductDetailsByIDQuery.Product?,
    onTotalChange: (Int) -> Unit,
    favViewModel: FavouritesViewModel,
    onSizePicked: (String) -> Unit,
    onColorPicked: (String) -> Unit,
    quantity: Int,
    onQuantityChanged: (Int) -> Unit,
    availableQuantity: Int,
    rate: Double,
    currencySymbol: String?,
    onLimitReached: () -> Unit
) {
    Log.i("TAG", "ProductInfoScreen: ${product?.id} ")
    val scrollState = rememberScrollState()
    val media = product?.media?.edges
    val images = media?.map { it.node.previewImage?.url.toString() } ?: emptyList()
    val price = product?.variants?.edges?.get(0)?.node?.price?.amount.toString()
    var actualPrice = (price.toDouble()*rate).toInt()
    val size = product?.options?.get(0)?.values
    val color = product?.options?.get(1)?.values
    val colorList = color?.toColorList()
    val id = product?.id.toString()
    val productName = product?.title.toString()

    LaunchedEffect(key1 = quantity, key2 = actualPrice) {
        val total = actualPrice.toDouble() * quantity
        onTotalChange(total.toInt())
    }

    Box(modifier = Modifier.padding(innerPadding)) {
        Column(
            modifier = Modifier.verticalScroll(scrollState)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            ProductImages(images = images,favViewModel,id,productName)
            Spacer(modifier = Modifier.height(16.dp))
            if (product != null) {
                ProductDetails(
                    title = product.title,
                    price = actualPrice.toDouble(),
                    quantity = quantity,
                    maxQuantity = availableQuantity,
                    description = product.description,
                    sizes = size,
                    colors = colorList,
                    onQuantityChange = { newQty -> onQuantityChanged(newQty) },
                    onColorSelected = {
                        onColorPicked(color?.getOrNull(colorList?.indexOf(it) ?: -1) ?: "")
                    },
                    onSizeSelected = { onSizePicked(it.toString()) },
                    currencySymbol = currencySymbol,
                    onLimitReached = onLimitReached
                )
            }

        }
    }
}


@Composable
fun ProductDetails(
    title: String,
    price: Double,
    quantity: Int,
    description: String,
    sizes: List<String>?,
    colors: List<Color>?,
    onQuantityChange: (Int) -> Unit,
    onColorSelected: (Color) -> Unit = {},
    onSizeSelected: (Int) -> Unit = {},
    maxQuantity: Int,
    currencySymbol: String?,
    onLimitReached: () -> Unit
) {
    val selectedColor = remember { mutableStateOf(colors?.firstOrNull() ?: Color.Unspecified) }
    val selectedSize = remember { mutableStateOf(sizes?.get(0) ?: "") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MainColor,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$currencySymbol $price",
                fontSize = 18.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.weight(1f))
            QuantitySelector(
                quantity = quantity,
                maxQuantity = maxQuantity,
                onChange = { newQuantity -> onQuantityChange(newQuantity) },
                onLimitReached = onLimitReached
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Description",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MainColor
        )
        Spacer(modifier = Modifier.height(8.dp))
        ExpandableText(text = description)

        Spacer(modifier = Modifier.height(16.dp))

        sizes?.takeIf { it.isNotEmpty() && it.firstOrNull() != "OS" }?.let {
            Text(
                text = "Size",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MainColor
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                sizes?.forEach { size ->
                    val selectedIsMapped = selectedSize.value.matches(Regex("(?i)XS|S|M|L|XL|XXL"))
                    val sizeIsMapped = size.matches(Regex("(?i)XS|S|M|L|XL|XXL"))

                    val isSelected = if (selectedIsMapped && sizeIsMapped) {
                        mapSizeFromTextToInteger(selectedSize.value) == mapSizeFromTextToInteger(size)
                    } else {
                        selectedSize.value == size
                    }

                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) MainColor else LightGray)
                            .clickable {
                                selectedSize.value = size
                                val selectedInt = size.toIntOrNull() ?: mapSizeFromTextToInteger(size)
                                onSizeSelected(selectedInt)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = size,
                            color = if (isSelected) Color.White else Color.Black,
                            fontSize = 14.sp
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        colors?.takeIf { it.isNotEmpty() }?.let {
            Text(
                text = "Color",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MainColor
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                it.forEach { color ->
                    val isSelected = selectedColor.value == color
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(color)
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) MainColor else Color.Gray,
                                shape = CircleShape
                            )
                            .clickable {
                                selectedColor.value = color
                                onColorSelected(color)
                            }
                    )
                }
            }
        }
    }
}


@Composable
fun ProductImages(
    images: List<String>,
    favViewModel: FavouritesViewModel,
    productId: String,
    productName: String
){
    val favoriteProducts by favViewModel.favorite.collectAsState()
    val isFav = favoriteProducts.contains(productId)
    var itemToDelete by remember { mutableStateOf<ProductsDetailsByIDsQuery.Node?>(null) }
    var showConfirmDialog by remember { mutableStateOf(false) }
    val pagerState = rememberPagerState(images.size)
    val showGuestDialog = remember { mutableStateOf(false) }
    val user = FirebaseAuthObject.getAuth().currentUser
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ){
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            HorizontalPager(
                count = images.size,
                state = pagerState,
                contentPadding = PaddingValues(horizontal = 16.dp),
                itemSpacing = 8.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            ) { page ->

                val pageOffset = calculateCurrentOffsetForPage(page)
                val absOffset = abs(pageOffset)

                val scale = lerp(0.85f, 1f, 1f - absOffset.coerceIn(0f, 1f))
                val alpha = lerp(0.4f, 1f, 1f - absOffset.coerceIn(0f, 1f))
                val imageOffsetX = (pageOffset * 60).dp

                Box(
                    modifier = Modifier
                        .graphicsLayer {
                            scaleY = scale
                            scaleX = scale
                            this.alpha = alpha
                        }
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                ) {
                    ZoomableAsyncImage(
                        model = images[page],
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .offset(x = imageOffsetX)
                            .fillMaxSize()
                            .alpha(0.85f)
                    )

                    IconButton(
                        onClick =
                            {
                                if (user == null) {
                                    showGuestDialog.value = true
                                }else{
                                    if (isFav) {
                                        itemToDelete = ProductsDetailsByIDsQuery.Node(
                                            __typename = productName,
                                            onProduct = ProductsDetailsByIDsQuery.OnProduct(
                                                id = productId,
                                                title = productName,
                                                vendor = "", productType = "", description = "",
                                                featuredImage = null,
                                                variants = ProductsDetailsByIDsQuery.Variants(emptyList()),
                                                media = ProductsDetailsByIDsQuery.Media(emptyList()),
                                                options = emptyList()
                                            )
                                        )
                                        showConfirmDialog = true
                                    } else {
                                        favViewModel.addToFavorite(productId)
                                    }
                                }
                            },
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(12.dp)
                            .background(Color.White.copy(alpha = 0.7f), shape = CircleShape)
                            .size(36.dp)
                    ) {
                        Icon(
                            imageVector = if (isFav) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = MainColor
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(images.size) { index ->
                    val selected = pagerState.currentPage == index
                    val dotAlpha by animateFloatAsState(
                        targetValue = if (selected) 1f else 0.5f,
                        animationSpec = tween(300)
                    )
                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .size(if (selected) 10.dp else 8.dp)
                            .clip(CircleShape)
                            .background(MainColor.copy(alpha = dotAlpha))
                    )
                }
            }
        }
    }

    if (showConfirmDialog && itemToDelete != null) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("Confirm Deletion") },
            text = { Text("Are you sure you want to delete '${itemToDelete?.__typename}' from favourites?") },
            confirmButton = {
                TextButton(onClick = {
                    itemToDelete?.onProduct?.id?.let { id ->
                        favViewModel.removeFromFavorite(id)
                        favViewModel.getAllFavorites()
                    }
                    showConfirmDialog = false
                    itemToDelete = null
                }) {
                    Text("Delete", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showConfirmDialog = false
                    itemToDelete = null
                }) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        )
    }

    GuestAlertDialog(
        showDialog = showGuestDialog.value,
        onDismiss = { showGuestDialog.value = false },
        onConfirm = {
            showGuestDialog.value = false
        }
    )
}