package com.example.buynest.views.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import com.example.buynest.model.entity.OfferModel
import com.example.buynest.ui.theme.MainColor
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.calculateCurrentOffsetForPage
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.delay
import kotlin.math.abs

@Composable
fun AdsSection(offers: List<OfferModel>) {
    val pagerState = rememberPagerState(offers.size)

    LaunchedEffect(pagerState) {
        while (true) {
            delay(5_000)
            val nextPage = (pagerState.currentPage + 1) % offers.size
            pagerState.animateScrollToPage(nextPage)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            HorizontalPager(
                count = offers.size,
                state = pagerState,
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
                        painter = painterResource(id = offers[page].imageRes),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .offset(x = imageOffsetX)
                            .fillMaxSize()
                            .alpha(0.85f)
                    )
                    Column(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(start = 16.dp, end = 16.dp)
                    ) {
                        Text(
                            text = offers[page].title,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MainColor,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.width(120.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = offers[page].subtitle,
                            fontSize = 14.sp,
                            color = MainColor,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.width(140.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF003366)),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text(
                                offers[page].buttonText,
                                color = Color.White,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(offers.size) { index ->
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