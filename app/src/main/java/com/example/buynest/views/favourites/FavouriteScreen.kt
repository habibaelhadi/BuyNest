package com.example.buynest.views.favourites

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Card
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.TextButton
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.buynest.R
import com.example.buynest.ui.theme.LightGray2
import com.example.buynest.ui.theme.white
import com.example.buynest.views.component.SearchBar

data class FavItem(
    val id: Int,
    val name: String,
    val price: Int,
    val color: String,
    val imageRes: Int
)

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun FavouriteScreen(onCartClicked:()->Unit, onSearchClicked:()->Unit) {
    var favItems by remember {
        mutableStateOf(
            listOf(
                FavItem(1, "Nike Air Jordan", 3500, "Orange", R.drawable.product),
                FavItem(2, "Adidas Runner", 2800, "Blue", R.drawable.product)
            )
        )
    }
    val phenomenaBold = FontFamily(
        Font(R.font.phenomena_bold)
    )
    var showConfirmDialog by remember { mutableStateOf(false) }
    var itemToDelete by remember { mutableStateOf<FavItem?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(white)
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        SearchBar(
            onCartClicked = onCartClicked,
            onSearchClicked = onSearchClicked
        )
        Spacer(modifier = Modifier.height(24.dp))
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
        ){
            items(favItems, key = { it.id }) { item ->
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
                        FavouriteCard(
                            item = item,
                            onDelete = { id ->
                                itemToDelete = favItems.find { it.id == id }
                                showConfirmDialog = true
                            }
                        )
                    },
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
    if (showConfirmDialog && itemToDelete != null) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { androidx.compose.material.Text("Confirm Deletion") },
            text = { androidx.compose.material.Text("Are you sure you want to delete '${itemToDelete?.name}' from favourites?") },
            confirmButton = {
                TextButton(onClick = {
                    favItems = favItems.filterNot { it.id == itemToDelete?.id }
                    itemToDelete = null
                    showConfirmDialog = false
                }) {
                    androidx.compose.material.Text("Delete", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    itemToDelete = null
                    showConfirmDialog = false
                }) {
                    androidx.compose.material.Text("Cancel", color = Color.Gray)
                }
            }
        )
    }
}
