package com.example.buynest.model.entity

import com.google.android.gms.maps.model.LatLng

data class PlaceData(
    val placeId: String,
    val displayName: String?,
    val latLng: LatLng? = null,
    val address: String? = null
)