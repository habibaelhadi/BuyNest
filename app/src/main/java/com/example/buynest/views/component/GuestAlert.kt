package com.example.buynest.views.component

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun GuestAlertDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Guest Mode") },
            text = {
                Text("You are currently browsing as a guest. Please log in or sign up to access all features.")
            },
            confirmButton = {
                TextButton(onClick = onConfirm) {
                    Text("Ok")
                }
            }
        )
    }
}
