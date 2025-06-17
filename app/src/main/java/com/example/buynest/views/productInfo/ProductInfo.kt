package com.example.buynest.views.productInfo

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import com.example.buynest.R
import com.example.buynest.ui.theme.LightGray
import com.example.buynest.ui.theme.MainColor
import com.example.buynest.utils.toColorList
import com.example.buynest.views.component.BottomSection
import com.example.buynest.views.component.ExpandableText
import com.example.buynest.views.component.QuantitySelector
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.calculateCurrentOffsetForPage
import com.google.accompanist.pager.rememberPagerState
import kotlin.math.abs

data class Product(
    val id: Int,
    val name: String,
    val price: Int,
    val color: List<String>,
    val size: List<Int>,
    val imageRes: List<Int>,
    val quantity: Int,
    val desc: String
)

@Composable
fun ProductInfo(
    backClicked :()->Unit,
    navigateToCart :()->Unit
){
    var product by remember { mutableStateOf(Product(
        id = 1,
        name = "Nike Air Jordan",
        price = 3500,
        color = listOf("Orange", "Blue", "Green"),
        size = listOf(40, 42, 44),
        imageRes = listOf(R.drawable.product,R.drawable.product,R.drawable.product,R.drawable.product),
        quantity = 1,
        desc = "Nike is a multinational corporation that designs, develops, and sells athletic footwear ,apparel, and accessories Nike is a multinational corporation that designs, develops, and sells athletic footwear ,apparel, and accessories,Nike is a multinational corporation that designs, develops, and sells athletic footwear ,apparel, and accessories Nike is a multinational corporation that designs, develops, and sells athletic footwear ,apparel, and accessories"
    ))}
    val totalPrice = product.price * product.quantity
    val scrollState = rememberScrollState()
    Scaffold (
        topBar = { ProductInfoTopBar(backClicked, navigateToCart) },
        bottomBar = { BottomSection(totalPrice, Icons.Default.AddShoppingCart, "Add to Cart")}
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            Column(
                modifier = Modifier.verticalScroll(scrollState)
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                ProductImages(images = product.imageRes)
                Spacer(modifier = Modifier.height(16.dp))
                ProductDetails(
                    title = product.name,
                    price = product.price,
                    rating = 4.5,
                    quantity = product.quantity,
                    description = product.desc,
                    sizes = product.size.takeIf { it.isNotEmpty() },
                    colors = product.color.toColorList(),
                    onQuantityChange = { id, newQty -> product = product.copy(quantity = newQty) }
                )

            }
        }
    }
}

@Composable
fun ProductImages(images: List<Int>){
    val pagerState = rememberPagerState(images.size)
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
                    Image(
                        painter = painterResource(id = images[page]),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .offset(x = imageOffsetX)
                            .fillMaxSize()
                            .alpha(0.85f)
                    )

                    IconButton(
                        onClick = { /* handle fav toggle */ },
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(12.dp)
                            .background(Color.White.copy(alpha = 0.7f), shape = CircleShape)
                            .size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FavoriteBorder,
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
}

@Composable
fun ProductDetails(
    title: String,
    price: Int,
    rating: Double,
    quantity: Int,
    description: String,
    sizes: List<Int>?,
    colors: List<Color>?,
    onQuantityChange: (Int, Int) -> Unit,
    onColorSelected: (Color) -> Unit = {},
    onSizeSelected: (Int) -> Unit = {}
) {
    val selectedColor = remember { mutableStateOf(colors?.firstOrNull() ?: Color.Unspecified) }
    val selectedSize = remember { mutableIntStateOf(sizes?.firstOrNull() ?: 0) }

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
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "LE $price",
                fontSize = 18.sp,
                color = MainColor,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = Color.Yellow,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "$rating",
                fontSize = 18.sp,
                color = Color.Gray,
            )
            Spacer(modifier = Modifier.weight(1f))
            QuantitySelector(quantity) { newQuantity -> onQuantityChange(1, newQuantity) }
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

        sizes?.takeIf { it.isNotEmpty() }?.let {
            Text(
                text = "Size",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MainColor
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                it.forEach { size ->
                    val isSelected = selectedSize.value == size
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) MainColor else LightGray)
                            .clickable {
                                selectedSize.value = size
                                onSizeSelected(size)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = size.toString(),
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
