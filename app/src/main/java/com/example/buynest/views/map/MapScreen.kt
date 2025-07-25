package com.example.buynest.views.map

import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.apollographql.apollo3.api.Optional
import com.example.buynest.R
import com.example.buynest.type.MailingAddressInput
import com.example.buynest.ui.theme.MainColor
import com.example.buynest.utils.AppConstants.ADDRESS_TYPE_FRIEND
import com.example.buynest.utils.AppConstants.ADDRESS_TYPE_HOME
import com.example.buynest.utils.AppConstants.ADDRESS_TYPE_OFFICE
import com.example.buynest.utils.AppConstants.ADDRESS_TYPE_OTHER
import com.example.buynest.utils.AppConstants.KEY_CUSTOMER_TOKEN
import com.example.buynest.utils.SecureSharedPrefHelper
import com.example.buynest.viewmodel.address.AddressViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import org.koin.androidx.compose.koinViewModel
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    backClicked: () -> Unit,
    onMapSearchClicked: () -> Unit,
    addressViewModel: AddressViewModel
) {
    val context = LocalContext.current

    val hasLocationPermission = remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasLocationPermission.value = granted
    }

    LaunchedEffect(Unit) {
        if (!hasLocationPermission.value) {
            permissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }

        Configuration.getInstance().load(
            context.applicationContext,
            context.getSharedPreferences("osm_config", Context.MODE_PRIVATE)
        )
    }

    var selectedPoint by remember { mutableStateOf<GeoPoint?>(null) }
    var address by remember { mutableStateOf("") }
    var showSheet by remember { mutableStateOf(false) }
    var currentStep by remember { mutableStateOf(1) }

    var name by remember { mutableStateOf(TextFieldValue()) }
    var phone by remember { mutableStateOf(TextFieldValue()) }
    var addressType by remember { mutableStateOf("Home") }
    var landmark by remember { mutableStateOf(TextFieldValue()) }

    var nameError by remember { mutableStateOf<String?>(null) }
    var phoneError by remember { mutableStateOf<String?>(null) }
    var addressError by remember { mutableStateOf<String?>(null) }

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
            currentStep = 1
        }
    }

    Scaffold(
        modifier = Modifier.statusBarsPadding(),
        topBar = {
            IconButton(
                onClick = backClicked,
                modifier = Modifier
                    .size(40.dp)
                    .padding(start = 8.dp)
                    .clip(CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            OsmMapView(
                modifier = Modifier.fillMaxSize(),
                onLocationSelected = { point -> selectedPoint = point } ,
                locationEnabled = hasLocationPermission.value
            )
        }

        if (showSheet && selectedPoint != null) {
            ModalBottomSheet(
                onDismissRequest = { showSheet = false },
                modifier = Modifier.fillMaxWidth()
            ) {
                if (currentStep == 1) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 16.dp)
                    ) {
                        Text("Confirm your order delivery location", style = MaterialTheme.typography.titleLarge)
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .padding(12.dp)
                        ) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = MainColor)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(address, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = { currentStep = 2 },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MainColor)
                        ) {
                            Text("Confirm and add details", fontSize = 16.sp)
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
                if (currentStep == 2) {
                    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Address details", style = MaterialTheme.typography.titleLarge)
                            IconButton(onClick = { showSheet = false }) {
                                Icon(Icons.Default.Close, contentDescription = "Close")
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Select address type", style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf(ADDRESS_TYPE_HOME, ADDRESS_TYPE_OFFICE, ADDRESS_TYPE_FRIEND, ADDRESS_TYPE_OTHER).forEach { type ->
                                FilterChip(
                                    selected = addressType == type,
                                    onClick = { addressType = type },
                                    label = { Text(type) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MainColor,
                                        selectedLabelColor = Color.White,
                                        containerColor = MaterialTheme.colorScheme.surface,
                                        labelColor = MaterialTheme.colorScheme.onSurface
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(
                            value = name,
                            onValueChange = {
                                name = it
                                nameError = null
                            },
                            singleLine = true,
                            label = { Text("Receiver's name") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = MainColor,
                                cursorColor = MainColor,
                                focusedLabelColor = MainColor
                            )
                        )
                        nameError?.let {
                            Text(it, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = phone,
                            onValueChange = { input ->
                                if (input.text.all { it.isDigit() }) {
                                    phone = input
                                    phoneError = null
                                }
                            },
                            singleLine = true,
                            label = { Text("Receiver's phone") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Number
                            ),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = MainColor,
                                cursorColor = MainColor,
                                focusedLabelColor = MainColor
                            )
                        )
                        phoneError?.let {
                            Text(it, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = landmark,
                            onValueChange = { landmark = it },
                            label = { Text("Nearby Landmark (optional)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = MainColor,
                                cursorColor = MainColor,
                                focusedLabelColor = MainColor
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                var valid = true
                                if (name.text.isBlank()) {
                                    nameError = "Name cannot be empty"
                                    valid = false
                                }
                                if (phone.text.isBlank()) {
                                    phoneError = "Phone cannot be empty"
                                    valid = false
                                }
                                if (address.isBlank() || address == "Address not found") {
                                    addressError = "Invalid address selected"
                                    valid = false
                                } else {
                                    addressError = null
                                }
                                if (!valid) return@Button

                                val formattedPhone = phone.text.takeIf { it.isNotBlank() }?.let {
                                    if (!it.startsWith("+")) "+2$it" else it
                                }

                                val city = addressViewModel.extractFromAddress(address) { parts ->
                                    parts.getOrNull(parts.size - 2) ?: "Unknown City"
                                }
                                val country = addressViewModel.extractFromAddress(address) { parts ->
                                    parts.lastOrNull() ?: "Unknown Country"
                                }

                                val address2Combined = if (landmark.text.isNotBlank()) {
                                    "$addressType - ${landmark.text}"
                                } else addressType

                                val mailingAddressInput = MailingAddressInput(
                                    address1 = Optional.Present(address),
                                    address2 = Optional.Present(address2Combined),
                                    city = Optional.Present(city),
                                    country = Optional.Present(country),
                                    firstName = Optional.Present(name.text),
                                    lastName = Optional.Absent,
                                    phone = formattedPhone?.let { Optional.Present(it) } ?: Optional.Absent
                                )

                                addressViewModel.addAddress(
                                    SecureSharedPrefHelper.getString(KEY_CUSTOMER_TOKEN).toString(),
                                    mailingAddressInput
                                )

                                showSheet = false
                                name = TextFieldValue("")
                                phone = TextFieldValue("")
                                landmark = TextFieldValue("")
                            },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MainColor),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Save address", fontSize = 16.sp)
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun OsmMapView(
    modifier: Modifier = Modifier,
    onLocationSelected: (GeoPoint) -> Unit = {},
    locationEnabled: Boolean = false
) {
    val context = LocalContext.current
    var marker by remember { mutableStateOf<Marker?>(null) }
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val defaultLocation = GeoPoint(31.2001, 29.9187)
    var mapView: MapView? by remember { mutableStateOf(null) }

    LaunchedEffect(locationEnabled) {
        if (locationEnabled) {
            try {
                val location = fusedLocationClient.lastLocation.await()
                location?.let {
                    val userPoint = GeoPoint(it.latitude, it.longitude)
                    mapView?.apply {
                        controller.setCenter(userPoint)
                        marker?.let { overlays.remove(it) }

                        val newMarker = Marker(this).apply {
                            position = userPoint
                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                            icon = ContextCompat.getDrawable(context, R.drawable.placeholder)
                        }

                        overlays.add(newMarker)
                        marker = newMarker
                        invalidate()
                        onLocationSelected(userPoint)
                    }
                }
            } catch (e: SecurityException) {
                Log.e("OsmMapView", "Location permission not granted: ${e.message}")
            }
        }
    }

    AndroidView(
        factory = {
            MapView(context).apply {
                mapView = this // Save reference
                setTileSource(TileSourceFactory.MAPNIK)
                setMultiTouchControls(true)
                controller.setZoom(14.0)

                // Add tap listener
                val receiver = object : org.osmdroid.events.MapEventsReceiver {
                    override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                        p?.let {
                            marker?.let { overlays.remove(it) }
                            val newMarker = Marker(this@apply).apply {
                                position = it
                                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                icon = ContextCompat.getDrawable(context, R.drawable.placeholder)
                            }
                            overlays.add(newMarker)
                            marker = newMarker
                            controller.setCenter(it)
                            invalidate()
                            onLocationSelected(it)
                        }
                        return true
                    }

                    override fun longPressHelper(p: GeoPoint?): Boolean = false
                }

                overlays.add(org.osmdroid.views.overlay.MapEventsOverlay(receiver))
            }
        },
        modifier = modifier
    )
}


suspend fun <T> Task<T>.await(): T = suspendCancellableCoroutine { cont ->
    addOnSuccessListener { cont.resume(it) }
    addOnFailureListener { cont.resumeWithException(it) }
}
