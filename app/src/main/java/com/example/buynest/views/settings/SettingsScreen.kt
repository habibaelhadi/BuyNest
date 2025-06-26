package com.example.buynest.views.settings

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.buynest.R
import com.example.buynest.repository.FirebaseAuthObject
import com.example.buynest.ui.theme.MainColor
import com.example.buynest.ui.theme.white
import com.example.buynest.utils.SharedPrefHelper
import com.example.buynest.viewmodel.authentication.AuthenticationViewModel
import com.example.buynest.views.component.CurrencyOptionBottomSheet
import com.example.buynest.views.component.GuestAlertDialog
import com.example.buynest.views.component.PaymentOptionBottomSheet
import com.example.buynest.views.component.SettingsCard
import org.koin.androidx.compose.koinViewModel

@Composable
fun SettingsScreen(
    gotoProfileScreen: () -> Unit,
    gotoOrdersHistoryScreen: () -> Unit,
    gotoAddressScreen: () -> Unit,
    gotoLoginScreen: () -> Unit,
    authViewModel: AuthenticationViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val phenomenaBold = FontFamily(Font(R.font.phenomena_bold))
    val showSheet = remember { mutableStateOf(false) }
    val selectedPayment = remember { mutableStateOf(SharedPrefHelper.getPaymentMethod(context)) }
    val showCurrencySheet = remember { mutableStateOf(false) }
    val selectedCurrency = remember { mutableStateOf(SharedPrefHelper.getCurrency(context)) }
    val launchEmailIntent = remember { mutableStateOf(false) }
    val showAboutDialog = remember { mutableStateOf(false) }
    val user = FirebaseAuthObject.getAuth().currentUser
    val buttonText = if (user == null) "Login" else "Log out"
    val showGuestDialog = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        authViewModel.message.collect { message ->
            if (message == "Success") {
                gotoLoginScreen()
                SharedPrefHelper.setLogIn(context = context, false)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(white)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            "BuyNest",
            fontSize = 20.sp,
            fontFamily = phenomenaBold,
            fontWeight = FontWeight.Bold,
            color = MainColor
        )
        Spacer(modifier = Modifier.height(12.dp))


        SettingsCard(user?.displayName.toString(), icon = Icons.Default.Person, bold = true,
            onClick = {
                if (user == null){
                    showGuestDialog.value = true
                }else{
                    gotoProfileScreen()
                }
            })
        Spacer(modifier = Modifier.height(12.dp))

        SettingsCard("Address Book", Icons.Default.Home,
            onClick = {
                if (user == null){
                    showGuestDialog.value = true
                }else{
                    gotoAddressScreen()
                }
            })

        SettingsCard("Orders History", Icons.Default.History,
            onClick = {
                if (user == null){
                    showGuestDialog.value = true
                }else{
                    gotoOrdersHistoryScreen()
                }
            })
        Spacer(modifier = Modifier.height(12.dp))

        SettingsCard("Payment Option", Icons.Default.Payment,
            onClick = {
                if (user == null){
                    showGuestDialog.value = true
                }else{
                    showSheet.value = true
                }
            }
        )

        SettingsCard("Currency", Icons.Default.AttachMoney,
            onClick = { showCurrencySheet.value = true }
        )
        Spacer(modifier = Modifier.height(12.dp))


        SettingsCard("Connect with Us", Icons.Default.Phone) {
            launchEmailIntent.value = true
        }

        SettingsCard("About", Icons.Default.Info,
            onClick = { showAboutDialog.value = true }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (user == null){
                    gotoLoginScreen()
                }else{
                    authViewModel.logout()
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = MainColor,
                contentColor = white
            ),
            shape = RoundedCornerShape(6.dp),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = buttonText)
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (showSheet.value) {
            PaymentOptionBottomSheet(
                selectedOption = selectedPayment.value,
                onDismiss = { showSheet.value = false },
                onSelectOption = {
                    selectedPayment.value = it
                    SharedPrefHelper.savePaymentMethod(context, it)
                }
            )
        }
        if (showCurrencySheet.value) {
            CurrencyOptionBottomSheet(
                selectedCurrency = selectedCurrency.value,
                onDismiss = { showCurrencySheet.value = false },
                onSelect = {
                    selectedCurrency.value = it
                    SharedPrefHelper.saveCurrency(context, it)
                }
            )
        }
        if (launchEmailIntent.value) {
            LaunchedEffect(Unit) {
                val baseIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "message/rfc822"
                    putExtra(Intent.EXTRA_EMAIL, arrayOf("team.buynest@gmail.com"))
                    putExtra(Intent.EXTRA_SUBJECT, "Contact from BuyNest")
                    putExtra(Intent.EXTRA_TEXT, "Hello, I’d like to get in touch with your team.")
                }

                val pm = context.packageManager
                val emailApps = pm.queryIntentActivities(baseIntent, 0)

                if (emailApps.isNotEmpty()) {
                    val chooser = Intent.createChooser(baseIntent, "Send Email")
                    context.startActivity(chooser)
                } else {
                    Log.e("EmailIntent", "No email app found.")
                }

                launchEmailIntent.value = false
            }
        }

        if (showAboutDialog.value) {
            AlertDialog(
                onDismissRequest = { showAboutDialog.value = false },
                title = { Text("About BuyNest") },
                text = {
                    Text("BuyNest is a shopping app that provides high-quality products with smooth delivery and excellent service.\n\nVersion 1.0.0")
                },
                confirmButton = {
                    TextButton(onClick = { showAboutDialog.value = false }) {
                        Text("OK", color = MainColor)
                    }
                }
            )
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