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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.buynest.ProductsByCollectionIDQuery
import com.example.buynest.ProductsDetailsByIDsQuery
import com.example.buynest.repository.FirebaseAuthObject
import com.example.buynest.ui.theme.MainColor
import com.example.buynest.ui.theme.white
import com.example.buynest.viewmodel.favorites.FavouritesViewModel

@Composable
fun ProductItem(
    onProductClicked: (productId: String) -> Unit,
    bradProduct: ProductsByCollectionIDQuery.Node?,
    favViewModel: FavouritesViewModel,
    rate: Double,
    currencySymbol: String
) {
    val productId = bradProduct?.id.toString()
    val numericId = productId.substringAfterLast("/")

    Card(modifier = Modifier
        .width(200.dp)
        .padding(6.dp),
        border = BorderStroke(1.dp, MainColor.copy(0.5f)),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        onClick = {
            onProductClicked(numericId)
        }) {
        val productImageUrl = bradProduct?.featuredImage?.url.toString()
        val finalPrice = bradProduct?.variants?.edges?.firstOrNull()?.node?.price?.amount
            ?.toString()?.toDoubleOrNull()?.times(rate)?.toInt() ?: 0

        val cleanedTitle = bradProduct?.title?.replace(Regex("\\(.*?\\)"), "")?.trim()
        val parts = cleanedTitle?.split("|")?.map { it.trim() }
        val productName = if (parts != null && parts.size >= 2) parts[1] else "there is no name"

        val productId = bradProduct?.id.toString()
        val favoriteProducts by favViewModel.favorite.collectAsState()
        val isFav = favoriteProducts.contains(productId)
        var itemToDelete by remember { mutableStateOf<ProductsDetailsByIDsQuery.Node?>(null) }
        var showConfirmDialog by remember { mutableStateOf(false) }
        val user = FirebaseAuthObject.getAuth().currentUser
        val showGuestDialog = remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            if (user != null) {
                favViewModel.getAllFavorites()
            }
        }

        Column(
            modifier = Modifier
                .background(white)
                .height(240.dp)
        ) {
            Box(
                modifier = Modifier
                    .height(140.dp)
                    .fillMaxWidth()

            ) {
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
                ) {
                    IconButton(
                        onClick = {
                            if(user == null){
                                showGuestDialog.value = true
                            }else{
                                if (isFav) {
                                    itemToDelete = ProductsDetailsByIDsQuery.Node(
                                        __typename = "Product",
                                        onProduct = ProductsDetailsByIDsQuery.OnProduct(
                                            id = productId,
                                            title = productName,
                                            vendor = "",
                                            productType = "",
                                            description = "",
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
                            .align(Alignment.TopEnd)
                            .background(white, shape = CircleShape)
                            .size(32.dp)
                            .padding(8.dp)
                    ) {
                        Icon(
                            imageVector = if (isFav) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = null,
                            tint = MainColor
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = productName,
                style = MaterialTheme.typography.titleMedium,
                color = MainColor,
                maxLines = 2,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))

            Row(
                Modifier.padding(bottom = 16.dp, end = 8.dp)
            ) {
                Text(
                    text =  "${finalPrice} $currencySymbol",
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
                        imageVector = Icons.Filled.Add, contentDescription = null, tint = white
                    )
                }
            }
        }

        if (showConfirmDialog && itemToDelete != null) {
            AlertDialog(onDismissRequest = { showConfirmDialog = false },
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
                })
        }
        GuestAlertDialog(
            showDialog = showGuestDialog.value,
            onDismiss = { showGuestDialog.value = false },
            onConfirm = {
                showGuestDialog.value = false
            }
        )
    }
}

