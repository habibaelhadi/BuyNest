package com.example.buynest.views.map

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.buynest.BuildConfig
import com.example.buynest.ui.theme.MainColor
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.*
import com.google.android.libraries.places.api.net.*
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapSearchScreen(
    onBack: () -> Unit,
    onPlaceSelected: (GeoPoint, String) -> Unit
) {
    val context = LocalContext.current

    var isPlacesReady by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (!Places.isInitialized()) {
            Places.initializeWithNewPlacesApiEnabled(
                context.applicationContext,
                BuildConfig.PLACES_API_KEY
            )
        }
        isPlacesReady = true
    }

    if (!isPlacesReady) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = MainColor)
        }
        return
    }

    val placesClient = remember { Places.createClient(context) }

    var query by remember { mutableStateOf("") }
    var predictions by remember { mutableStateOf<List<AutocompletePrediction>>(emptyList()) }

    LaunchedEffect(query) {
        if (query.length >= 2) {
            val request = FindAutocompletePredictionsRequest.builder()
                .setQuery(query)
                .build()

            withContext(Dispatchers.IO) {
                try {
                    val response = placesClient.findAutocompletePredictions(request).await()
                    predictions = response.autocompletePredictions
                } catch (e: Exception) {
                    predictions = emptyList()
                    Log.e("PlacesSearch", "Error fetching predictions", e)
                }
            }
        } else {
            predictions = emptyList()
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
                                contentDescription = null,
                                tint = MainColor
                            )
                        },
                        shape = CircleShape,
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 8.dp),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = MainColor,
                            unfocusedBorderColor = Color.LightGray,
                            cursorColor = MainColor
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
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            items(predictions) { prediction ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            fetchPlaceDetails(prediction.placeId, placesClient) { latLng, name ->
                                onPlaceSelected(GeoPoint(latLng.latitude, latLng.longitude), name)
                            }
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
                    Text(
                        prediction.getFullText(null).toString(),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}


private fun fetchPlaceDetails(
    placeId: String,
    placesClient: PlacesClient,
    onComplete: (LatLng, String) -> Unit
) {
    val placeFields = listOf(Place.Field.LAT_LNG, Place.Field.NAME, Place.Field.ADDRESS)
    val request = FetchPlaceRequest.builder(placeId, placeFields).build()

    placesClient.fetchPlace(request)
        .addOnSuccessListener { response ->
            val place = response.place
            val latLng = place.latLng
            val name = place.name ?: place.address ?: "Unknown place"
            if (latLng != null) {
                onComplete(latLng, name)
            }
        }
        .addOnFailureListener { exception ->
            Log.e("PlaceDetails", "Place not found: $exception")
        }
}
