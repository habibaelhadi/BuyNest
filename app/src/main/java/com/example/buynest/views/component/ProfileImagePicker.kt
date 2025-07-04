package com.example.buynest.views.component

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.buynest.ui.theme.MainColor

@Composable
fun ProfileImagePicker() {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("profile_prefs", Context.MODE_PRIVATE)
    var imageBitmap by remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(Unit) {
        val fileName = prefs.getString("profile_image", null)
        fileName?.let {
            val file = context.getFileStreamPath(it)
            if (file.exists()) {
                imageBitmap = BitmapFactory.decodeFile(file.absolutePath)
            }
        }
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val bitmap = loadBitmapFromUri(context, it)
            bitmap?.let {
                imageBitmap = it
                val savedFileName = saveBitmapToInternalStorage(context, it)
                savedFileName?.let { name ->
                    prefs.edit().putString("profile_image", name).apply()
                }
            }
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
                contentDescription = "Edit Icon",
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

fun saveBitmapToInternalStorage(context: Context, bitmap: Bitmap): String? {
    return try {
        val filename = "profile_image.png"
        context.openFileOutput(filename, Context.MODE_PRIVATE).use { stream ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        }
        filename
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
