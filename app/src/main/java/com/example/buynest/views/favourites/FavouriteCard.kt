package com.example.buynest.views.favourites

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.buynest.ProductsDetailsByIDsQuery
import com.example.buynest.repository.FirebaseAuthObject
import com.example.buynest.ui.theme.LightGray2
import com.example.buynest.ui.theme.MainColor
import com.example.buynest.ui.theme.white
import com.example.buynest.utils.mapColorNameToColor
import com.example.buynest.viewmodel.favorites.FavouritesViewModel
import com.example.buynest.views.component.GuestAlertDialog

@Composable
fun FavouriteCard(
    item: ProductsDetailsByIDsQuery.Node,
    onDelete: (String) -> Unit,
    viewModel: FavouritesViewModel,
    navigateToProductInfo: (String) -> Unit
) {
    val imageUrl = item.onProduct?.featuredImage?.url.toString()
    val cleanedTitle = item?.onProduct?.title?.replace(Regex("\\(.*?\\)"), "")?.trim()
    val parts = cleanedTitle?.split("|")?.map { it.trim() }
    val title = if (parts != null && parts.size >= 2) parts[1] else "there is no name"
    val color = item.onProduct?.variants?.edges
        ?.firstOrNull()
        ?.node
        ?.selectedOptions
        ?.firstOrNull { it.name.equals("Color", ignoreCase = true) }
        ?.value
    val price = item.onProduct?.variants?.edges
        ?.firstOrNull()
        ?.node
        ?.price
        ?.amount
        ?.toString()
    val colorDot = mapColorNameToColor(color)
    val favoriteProducts by viewModel.favorite.collectAsState()
    val isFav = favoriteProducts.contains(item.onProduct?.id)
    val id = item.onProduct?.id
    val numericId = id?.substringAfterLast("/")
    val user = FirebaseAuthObject.getAuth().currentUser
    val showGuestDialog = remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(2.dp, LightGray2),
        colors = cardColors(containerColor = white),
        onClick = {
            if (user == null){
                showGuestDialog.value = true
            }else{
                navigateToProductInfo(numericId?:"")
            }
        }
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = rememberAsyncImagePainter(imageUrl),
                contentDescription = null,
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.FillHeight
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color(0xFF1E1E1E)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(colorDot, shape = CircleShape)
                            .border(1.dp,LightGray2, shape = CircleShape)
                    )
                    Text(
                        text = "  $color",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "LE $price",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MainColor
                )
            }

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.height(90.dp)
            ) {
                IconButton(
                    onClick = { onDelete(item.onProduct?.id?:"") },
                    modifier = Modifier
                        .size(28.dp)
                ) {
                    Icon(
                        imageVector = if (isFav) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = null,
                        tint = MainColor,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {},
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MainColor,
                        contentColor = white
                    ),
                    shape = RoundedCornerShape(100),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(
                        horizontal = 20.dp, vertical = 4.dp
                    )
                ) {
                    Text(text = "Add to Cart", fontSize = 12.sp)
                }
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
