package com.example.buynest.views.component

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.buynest.ui.theme.MainColor
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartTopBar(backClicked :()->Unit) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                "Cart",
                fontSize = 20.sp,
                fontWeight = Bold,
                color = MainColor
            )
        },
        navigationIcon = {
            IconButton(onClick = {
                backClicked()
            }) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = null,
                    tint = MainColor
                )
            }
        },
        actions = {},
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White),
    )
}

