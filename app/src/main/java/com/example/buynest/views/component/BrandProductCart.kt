package com.example.buynest.views.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.buynest.ProductsByCollectionIDQuery
import com.example.buynest.R
import com.example.buynest.ui.theme.MainColor
import com.example.buynest.ui.theme.Yellow
import com.example.buynest.ui.theme.white

@Composable
fun ProductItem(onProductClicked: () -> Unit, bradProduct: ProductsByCollectionIDQuery.Node?){
    Card (
        modifier = Modifier
            .width(200.dp)
            .padding(6.dp),
        border = BorderStroke(1.dp, MainColor.copy(0.5f)),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        onClick = onProductClicked
    ){
        val productImageUrl = bradProduct?.featuredImage?.url.toString()
        val productPrice = bradProduct?.variants?.edges?.firstOrNull()?.node?.price?.amount.toString()
        val compareAtPrice = bradProduct?.variants?.edges?.firstOrNull()?.node?.compareAtPrice?.amount?.toString()
        val cleanedTitle = bradProduct?.title?.replace(Regex("\\(.*?\\)"), "")?.trim()
        val parts = cleanedTitle?.split("|")?.map { it.trim() }
        val productName = if (parts != null && parts.size >= 2) parts[1] else "there is no name"

        Column(
            modifier = Modifier.background(white)
                .height(240.dp)
        ){
            Box(
                modifier = Modifier
                    .height(140.dp)
                    .fillMaxWidth()

            ){
                Image(
                    painter = rememberAsyncImagePainter(productImageUrl),
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .padding(end = 8.dp, top = 8.dp)
                        .align(Alignment.TopEnd)
                ){
                    IconButton(
                        onClick = {},
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .background(white, shape = CircleShape)
                            .size(32.dp)
                            .padding(8.dp)

                    ) {
                        Icon(
                            imageVector = Icons.Outlined.FavoriteBorder,
                            contentDescription = null,
                            tint = MainColor
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = productName.toString(),
                style = MaterialTheme.typography.titleMedium,
                color = MainColor,
                maxLines = 2,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))

            Row (
                Modifier.padding(bottom = 16.dp, end = 8.dp)
            ){
                Text(
                    text = productPrice,
                    style = MaterialTheme.typography.titleSmall,
                    color = MainColor,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                Spacer(modifier = Modifier.weight(1f))
                IconButton(
                    onClick = {},
                    modifier = Modifier
                        .background(MainColor, shape = CircleShape)
                        .size(24.dp)
                        .align(Alignment.CenterVertically)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = null,
                        tint = white
                    )
                }
            }
        }
    }
}

