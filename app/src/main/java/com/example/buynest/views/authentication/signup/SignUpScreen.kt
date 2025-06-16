package com.example.buynest.views.authentication.signup

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.buynest.R
import com.example.buynest.ui.theme.MainColor
import com.example.buynest.ui.theme.white
import com.example.buynest.views.authentication.CustomTextField

@Composable
fun SignUpScreen(mainNavController: NavHostController) {
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val name = remember { mutableStateOf("") }
    val phone = remember { mutableStateOf("") }
    val passwordVisible = remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MainColor)
            .imePadding()
            .focusable()
    ){
        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
                .fillMaxSize()
                .padding(horizontal = 16.dp,vertical = 16.dp)
                .padding(top = 100.dp),
            horizontalAlignment = Alignment.Start
        ){
            Box (
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ){
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
                text = "Full Name",
                color = white,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            CustomTextField(
                value = name.value,
                onValueChange = { name.value = it },
                placeholder = "Enter your full name"
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Mobile Number",
                color = white,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            CustomTextField(
                value = phone.value,
                onValueChange = { phone.value = it },
                placeholder = "Enter your phone number"
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Email",
                color = white,
                fontSize = 16.sp
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
                fontSize = 16.sp
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

            Spacer(modifier = Modifier.height(50.dp))

            Button(
                onClick = {  },
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
                    text = "Sign Up",
                    color = MainColor,
                    fontSize = 24.sp,
                    fontFamily = FontFamily(Font(R.font.phenomena_bold)),
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row (modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                androidx.compose.material.Text(
                    text = "Already have an account? ",
                    color = white,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.width(4.dp))
                androidx.compose.material.Text(
                    text = "Login",
                    modifier = Modifier.clickable { mainNavController.popBackStack() },
                    color = white,
                    fontSize = 14.sp,
                    fontFamily = FontFamily(Font(R.font.phenomena_bold)),
                    textDecoration = TextDecoration.Underline
                )
            }
        }
    }

    BackHandler(enabled = true) {}
}