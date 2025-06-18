package com.example.buynest.views.profile

import android.annotation.SuppressLint
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.buynest.model.entity.ProfileOptionItem
import com.example.buynest.ui.theme.MainColor
import com.example.buynest.ui.theme.lightBlue
import com.example.buynest.ui.theme.lightGreen
import com.example.buynest.ui.theme.lightOrange
import com.example.buynest.ui.theme.lightPurple
import com.example.buynest.views.component.CustomConfirmationDialog
import com.example.buynest.views.component.ProfileImagePicker
import com.example.buynest.views.component.ProfileOption

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("AutoboxingStateCreation")
@Composable
fun ProfileScreen(
    onBackClicked: () -> Unit
) {
    val scrollState = rememberScrollState()
    var expandedIndex by remember { mutableStateOf(-1) }
    var isEditing by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MainColor)
            .padding(top = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(bottom = 220.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(330.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(16.dp)
                        .clickable{
                            onBackClicked()
                        }
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center)
                        .padding(top = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(48.dp))

                    ProfileImagePicker()

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "Youssef Fayad",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "yousseffayad@gmail.com",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 20.sp
                    )
                }
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                )
            }
        }

        Surface(
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            color = Color.White,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 24.dp)
                    .heightIn(min = 330.dp)
            ) {
                Text(
                    text = "Account Overview",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                val options = listOf(
                    ProfileOptionItem(Icons.Default.Person, "Change User Name", lightBlue),
                    ProfileOptionItem(Icons.Default.Email, "Change Email", lightGreen),
                    ProfileOptionItem(Icons.Default.Lock, "Change Password", lightOrange),
                    ProfileOptionItem(Icons.Default.Phone, "Change Phone", lightPurple)
                )

                options.forEachIndexed { index, option ->
                    ProfileOption(
                        option = option,
                        isExpanded = expandedIndex == index,
                        isEditing = isEditing,
                        onClick = {
                            if (expandedIndex == index && isEditing) {
                                isEditing = false
                            }
                            expandedIndex = if (expandedIndex == index) -1 else index
                        },
                        onEditingChange = { editing ->
                            if (!editing) {
                            } else {
                                isEditing = true
                            }
                        },
                        onSaveRequested = {
                            showDialog = true
                        }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }

        if (showDialog) {
            CustomConfirmationDialog(
                onConfirm = {
                    isEditing = false
                    showDialog = false
                },
                onDismiss = {
                    showDialog = false
                }
            )
        }
    }
}

