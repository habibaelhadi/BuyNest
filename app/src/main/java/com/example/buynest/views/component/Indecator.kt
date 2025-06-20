package com.example.buynest.views.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun Indicator(){
    Box(
      modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ){
        CircularProgressIndicator()
    }
}