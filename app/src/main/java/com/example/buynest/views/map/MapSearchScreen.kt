package com.example.buynest.views.map

import android.location.Geocoder
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.buynest.ui.theme.MainColor
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapSearchScreen(
    onBack: () -> Unit,
    onPlaceSelected: (GeoPoint, String) -> Unit
) {
    val context = LocalContext.current
    var query by remember { mutableStateOf("") }
    var results by remember { mutableStateOf(listOf<Pair<String, GeoPoint>>()) }

    LaunchedEffect(query) {
        if (query.length >= 3) {
            results = withContext(Dispatchers.IO) {
                val geocoder = Geocoder(context, Locale.getDefault())
                try {
                    geocoder.getFromLocationName(query, 5)?.map {
                        val name = buildString {
                            if (!it.featureName.isNullOrBlank()) append(it.featureName)
                            if (!it.locality.isNullOrBlank()) append(", ${it.locality}")
                            if (!it.countryName.isNullOrBlank()) append(", ${it.countryName}")
                        }.ifBlank { "Unnamed Place" }

                        name to GeoPoint(it.latitude, it.longitude)
                    } ?: emptyList()
                } catch (e: Exception) {
                    emptyList()
                }
            }
        } else {
            results = emptyList()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    OutlinedTextField(
                        value = query,
                        onValueChange = { query = it },
                        placeholder = { Text("Search for a location...") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "Search Icon",
                                tint = MainColor
                            )
                        },
                        shape = CircleShape,
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = MainColor,
                            unfocusedBorderColor = Color.LightGray,
                            cursorColor = MainColor,

                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            itemsIndexed(results) { _, (name, geoPoint) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                           // onPlaceSelected(geoPoint, name)
                            Log.d("MapSearchScreen", "Selected place: $name, $geoPoint")
                        }
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = MainColor,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(name, style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}
