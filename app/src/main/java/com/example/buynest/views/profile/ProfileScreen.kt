package com.example.buynest.views.profile

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.buynest.ui.theme.MainColor
import com.example.buynest.ui.theme.lightBlue
import com.example.buynest.ui.theme.lightGreen
import com.example.buynest.ui.theme.lightOrange
import com.example.buynest.ui.theme.lightPurple

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("AutoboxingStateCreation")
@Composable
fun ProfileScreen() {
    val scrollState = rememberScrollState()
    var expandedIndex by remember { mutableStateOf(-1) }
    var isEditing by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MainColor)
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


data class ProfileOptionItem(
    val icon: ImageVector,
    val title: String,
    val iconColor: Color
)

@ExperimentalMaterial3Api
@Composable
fun ProfileOption(
    option: ProfileOptionItem,
    isExpanded: Boolean,
    isEditing: Boolean,
    onClick: () -> Unit,
    onEditingChange: (Boolean) -> Unit,
    onSaveRequested: () -> Unit
) {
    Column(
        modifier = Modifier.animateContentSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(option.iconColor.copy(alpha = 0.2f), shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = option.icon,
                    contentDescription = null,
                    tint = option.iconColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = option.title,
                fontSize = 15.sp,
                color = Color.Black,
                modifier = Modifier.weight(1f)
            )

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color.Gray
            )
        }

        if (isExpanded) {
            EditableProfileOption(
                isEditing = isEditing,
                onEditingChange = onEditingChange,
                onSaveRequested = onSaveRequested
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditableProfileOption(
    isEditing: Boolean,
    onEditingChange: (Boolean) -> Unit,
    onSaveRequested: () -> Unit
) {
    var text by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 4.dp, top = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = text,
                onValueChange = { text = it },
                enabled = isEditing,
                placeholder = { Text("Enter new value") },
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                ),
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = if (isEditing) "Save" else "Change",
                color = MainColor,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier
                    .clickable {
                        if (isEditing) {
                            onSaveRequested()
                        } else {
                            onEditingChange(true)
                        }
                    }
                    .padding(horizontal = 8.dp)
            )
        }
    }
}


@Composable
fun CustomConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    var visible by remember { mutableStateOf(true) }

    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .zIndex(10f)

    ) {
        Box(
            modifier = Modifier
                .background(Color.White, shape = RoundedCornerShape(16.dp))
                .border(1.dp, Color.LightGray, shape = RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column {
                Text(
                    text = "Confirm Change",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Are you sure you want to save the changes?",
                    fontSize = 15.sp,
                    color = Color.DarkGray
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = {
                        visible = false
                        onDismiss()
                    }) {
                        Text("Cancel", color = Color.Red)
                    }
                    TextButton(onClick = {
                        visible = false
                        onConfirm()
                    }) {
                        Text("Confirm", color = Color.Green)
                    }
                }
            }
        }
    }
}


@Composable
fun ProfileImagePicker() {
    val context = LocalContext.current
    var imageBitmap by remember { mutableStateOf<Bitmap?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            imageBitmap = loadBitmapFromUri(context, it)
        }
    }

    Box(
        modifier = Modifier
            .size(150.dp)
            .clickable { launcher.launch("image/*") },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(140.dp)
                .clip(CircleShape)
                .border(4.dp, Color.Yellow, CircleShape)
                .background(MainColor),
            contentAlignment = Alignment.Center
        ) {
            if (imageBitmap != null) {
                Image(
                    bitmap = imageBitmap!!.asImageBitmap(),
                    contentDescription = "Selected Profile Picture",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Default Profile Icon",
                    tint = Color.White,
                    modifier = Modifier.size(75.dp)
                )
            }
        }

        Box(
            modifier = Modifier
                .size(40.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 8.dp, y = 8.dp)
                .background(Color.White, shape = CircleShape)
                .border(2.dp, MainColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Camera Icon",
                tint = MainColor,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

fun loadBitmapFromUri(context: Context, uri: Uri): Bitmap? {
    return try {
        if (Build.VERSION.SDK_INT < 28) {
            @Suppress("DEPRECATION")
            MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        } else {
            val source = ImageDecoder.createSource(context.contentResolver, uri)
            ImageDecoder.decodeBitmap(source)
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
