package com.example.buynest.views.component.snackbar

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.buynest.ui.theme.snackbarColor
import com.example.buynest.ui.theme.white
import kotlinx.coroutines.delay

@Composable
fun CustomSnackbar(
    message: String,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 32.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = snackbarColor),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Text(
                text = message,
                modifier = Modifier.padding(16.dp),
                color = white
            )
        }
    }

    LaunchedEffect(message) {
        delay(2500)
        onDismiss()
    }
}
