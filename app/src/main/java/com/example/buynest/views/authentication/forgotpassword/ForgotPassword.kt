package com.example.buynest.views.authentication.forgotpassword

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.buynest.R
import com.example.buynest.repository.authentication.AuthenticationRepoImpl
import com.example.buynest.repository.authentication.firebase.FirebaseRepositoryImpl
import com.example.buynest.repository.authentication.firebase.datasource.FirebaseDataSourceImpl
import com.example.buynest.repository.authentication.shopify.ShopifyAuthRepositoryImpl
import com.example.buynest.repository.authentication.shopify.datasource.ShopifyAuthRemoteDataSourceImpl
import com.example.buynest.ui.theme.MainColor
import com.example.buynest.ui.theme.white
import com.example.buynest.utils.NetworkHelper
import com.example.buynest.viewmodel.authentication.AuthenticationViewModel
import com.example.buynest.views.authentication.CustomTextField
import com.example.buynest.views.customsnackbar.CustomSnackbar
import org.koin.androidx.compose.koinViewModel

@Composable
fun ForgotPasswordScreen(
    onBackToLogin: () -> Unit,
    viewModel: AuthenticationViewModel = koinViewModel()
) {
    var email by remember { mutableStateOf("") }
    val snackbarMessage = remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    LaunchedEffect(snackbarMessage.value) {
        if (snackbarMessage.value == "Success") {
            onBackToLogin()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.message.collect {
            snackbarMessage.value = it
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MainColor)
            .imePadding()
    ) {
        if (snackbarMessage.value != null) {
            CustomSnackbar(message = snackbarMessage.value!!) {
                snackbarMessage.value = null
            }
        }

        IconButton(
            onClick = { onBackToLogin() },
            modifier = Modifier
                .padding(top = 64.dp, start = 12.dp)
                .align(Alignment.TopStart)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = white
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .padding(top = 100.dp),
            horizontalAlignment = Alignment.Start
            ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.material.Text(
                    text = "BuyNest",
                    style = MaterialTheme.typography.headlineMedium,
                    fontFamily = FontFamily(Font(R.font.phenomena_bold)),
                    fontSize = 50.sp,
                    color = white
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Enter your email to reset your password",
                fontSize = 16.sp,
                color = white
            )

            Spacer(modifier = Modifier.height(16.dp))

            CustomTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = "Enter your email"
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick =
                {
                    if (!NetworkHelper.isConnected.value){
                        snackbarMessage.value = "No internet connection"
                    }else{
                        viewModel.resetPassword(email)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(12.dp)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = white
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Send Reset Email",
                    color = MainColor,
                    fontSize = 24.sp,
                    fontFamily = FontFamily(Font(R.font.phenomena_bold)),
                )
            }

        }
    }
}
