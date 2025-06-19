package com.example.buynest.views.authentication.login

import android.app.Activity
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.buynest.R
import com.example.buynest.repository.authenticationrepo.AuthenticationRepoImpl
import com.example.buynest.ui.theme.MainColor
import com.example.buynest.ui.theme.white
import com.example.buynest.utils.sharedPreferences.SharedPreferencesImpl
import com.example.buynest.utils.strategies.GoogleAuthenticationStrategy
import com.example.buynest.utils.strategies.LoginAuthenticationStrategy
import com.example.buynest.viewmodel.authentication.AuthenticationViewModel
import com.example.buynest.views.authentication.CustomTextField
import com.example.buynest.views.customsnackbar.CustomSnackbar

@Composable
fun LoginScreen(
    navigateToHome: () -> Unit,
    navigateToSignUp: () -> Unit,
    navigateToForgotPassword: () -> Unit) {
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val passwordVisible = remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    val activity = LocalActivity.current
    val context = LocalContext.current
    val viewModel: AuthenticationViewModel = viewModel(
        factory = AuthenticationViewModel.AuthenticationViewModelFactory(AuthenticationRepoImpl())
    )
    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            viewModel.handleGoogleSignInResult(123, data,context)
        } else {
            Log.e("GoogleSignIn", "Sign-in failed or canceled.")
        }
    }

    val snackbarMessage = remember { mutableStateOf<String?>(null) }

    LaunchedEffect(snackbarMessage.value) {
        if (snackbarMessage.value == "Success") {
            navigateToHome()
            SharedPreferencesImpl.setLogIn(context = context, true)
            SharedPreferencesImpl.setAuthenticationMode(context,"emailPassword")
        }
    }

    LaunchedEffect(Unit) {
        viewModel.message.collect {
            snackbarMessage.value = it
        }
    }

    LaunchedEffect(Unit) {
        viewModel.setGoogleLauncher(googleSignInLauncher)
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MainColor)
            .imePadding()
            .focusable()
    ) {
        if (snackbarMessage.value != null) {
            CustomSnackbar(message = snackbarMessage.value!!) {
                snackbarMessage.value = null
            }
        }

        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
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
                Text(
                    text = "BuyNest",
                    style = MaterialTheme.typography.headlineMedium,
                    fontFamily = FontFamily(Font(R.font.phenomena_bold)),
                    fontSize = 50.sp,
                    color = white
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Welcome Back To BuyNest",
                color = white,
                fontSize = 25.sp,
                fontFamily = FontFamily(Font(R.font.phenomena_bold)),
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Please sign in with your mail",
                color = white,
                fontSize = 16.sp,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Email",
                color = white,
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            CustomTextField(
                value = email.value,
                onValueChange = { email.value = it },
                placeholder = "Enter your email"
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Password",
                color = white,
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            CustomTextField(
                value = password.value,
                onValueChange = { password.value = it },
                placeholder = "Enter your password",
                isPassword = true,
                isPasswordVisible = passwordVisible.value,
                onVisibilityToggle = { passwordVisible.value = !passwordVisible.value }
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Forgot password?",
                modifier = Modifier
                    .align(Alignment.End)
                    .clickable { navigateToForgotPassword() },
                color = white,
                fontSize = 12.sp,
                fontFamily = FontFamily(Font(R.font.phenomena_bold))
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val strategy = LoginAuthenticationStrategy(email.value, password.value)
                    viewModel.authenticate(strategy)
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
                    text = "Login",
                    color = MainColor,
                    fontSize = 24.sp,
                    fontFamily = FontFamily(Font(R.font.phenomena_bold)),
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row (modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Don't have an account? ",
                    color = white,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Create Account",
                    modifier = Modifier.clickable { navigateToSignUp() },
                    color = white,
                    fontSize = 14.sp,
                    fontFamily = FontFamily(Font(R.font.phenomena_bold)),
                    textDecoration = TextDecoration.Underline

                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Row {
                    IconButton(
                        onClick = {
                            val strategy = GoogleAuthenticationStrategy(context = context, launcher = googleSignInLauncher)
                            val validation = viewModel.setGoogleStrategy(strategy)

                            if (validation == null) {
                                SharedPreferencesImpl.setAuthenticationMode(context, "google")
                                viewModel.getGoogleSignInIntent(context)?.let { intent ->
                                    googleSignInLauncher.launch(intent)
                                }
                            } else {
                                snackbarMessage.value = validation
                            }
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(white)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.google),
                            contentDescription = "Sign in with Google",
                            tint = Color.Unspecified
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    IconButton(
                        onClick = {
                            navigateToHome()
                            SharedPreferencesImpl.setAuthenticationMode(context,"guest")
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(white)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.guest),
                            contentDescription = "Continue as Guest",
                            tint = Color.Unspecified
                        )
                    }
                }
            }
        }
    }

    BackHandler(enabled = true) {
        activity?.finishAffinity()
    }
}