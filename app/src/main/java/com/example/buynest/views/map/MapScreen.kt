package com.example.buynest.views.map

import android.content.Context
import android.location.Geocoder
import android.util.Log
import androidx.compose.foundation.background
import com.example.buynest.R
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.buynest.ui.theme.MainColor
import com.example.buynest.views.component.MapTopBar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(backClicked: () -> Unit) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        Configuration.getInstance().load(
            context.applicationContext,
            context.getSharedPreferences("osm_config", Context.MODE_PRIVATE)
        )
    }

    var selectedPoint by remember { mutableStateOf<GeoPoint?>(null) }
    var address by remember { mutableStateOf<String>("") }
    var showSheet by remember { mutableStateOf(false) }

    var name by remember { mutableStateOf(TextFieldValue()) }
    var phone by remember { mutableStateOf(TextFieldValue()) }
    var addressType by remember { mutableStateOf("Home") }

    LaunchedEffect(selectedPoint) {
        selectedPoint?.let {
            address = withContext(Dispatchers.IO) {
                try {
                    val geocoder = Geocoder(context, Locale.getDefault())
                    val list = geocoder.getFromLocation(it.latitude, it.longitude, 1)
                    list?.firstOrNull()?.getAddressLine(0) ?: "Address not found"
                } catch (e: Exception) {
                    "Address not found"
                }
            }
            showSheet = true
        }
    }

    Scaffold(
        topBar = {
            MapTopBar(
                onBack = backClicked,
                onSearchClick = {}
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            OsmMapView(
                modifier = Modifier.fillMaxSize(),
                onLocationSelected = { point ->
                    selectedPoint = point
                }
            )
        }

        if (showSheet && selectedPoint != null) {
            ModalBottomSheet(
                onDismissRequest = { showSheet = false },
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("Selected Address:", style = MaterialTheme.typography.titleMedium)
                    Text(
                        address,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .padding(vertical = 8.dp, horizontal = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Recipient Name Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Phone Number") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Address Type", style = MaterialTheme.typography.titleMedium)
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        listOf("Home", "Office", "Friend", "Other").forEach { type ->
                            FilterChip(
                                selected = addressType == type,
                                onClick = { addressType = type },
                                label = { Text(type) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MainColor,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                                    containerColor = MaterialTheme.colorScheme.surface,
                                    labelColor = MaterialTheme.colorScheme.onSurface
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            Log.d("OrderInfo", "Name: ${name.text}, Phone: ${phone.text}, Address: $address, Type: $addressType")
                            showSheet = false
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MainColor)
                    ) {
                        Text("Save Address", fontSize = 18.sp)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun OsmMapView(
    modifier: Modifier = Modifier,
    onLocationSelected: (GeoPoint) -> Unit = {}
) {
    val context = LocalContext.current
    var marker by remember { mutableStateOf<Marker?>(null) }

    AndroidView(
        factory = {
            MapView(context).apply {
                setTileSource(TileSourceFactory.MAPNIK)
                setMultiTouchControls(true)
                controller.setZoom(12.0)
                controller.setCenter(GeoPoint(31.2001, 29.9187))
                val customIcon = ContextCompat.getDrawable(context, R.drawable.placeholder)


                // initial marker
                val initialPoint = GeoPoint(31.2001, 29.9187)
                val initialMarker = Marker(this).apply {
                    position = initialPoint
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    icon = customIcon
                }
                overlays.add(initialMarker)
                marker = initialMarker

                val receiver = object : org.osmdroid.events.MapEventsReceiver {
                    override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                        p?.let {
                            overlays.remove(marker)

                            val newMarker = Marker(this@apply).apply {
                                position = p
                                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                icon = customIcon
                            }
                            overlays.add(newMarker)
                            controller.setCenter(p)
                            marker = newMarker
                            invalidate()
                            onLocationSelected(p)
                        }
                        return true
                    }

                    override fun longPressHelper(p: GeoPoint?): Boolean {
                        return false
                    }
                }

                val eventsOverlay = org.osmdroid.views.overlay.MapEventsOverlay(receiver)
                overlays.add(eventsOverlay)
            }
        },
        modifier = modifier
    )
}

