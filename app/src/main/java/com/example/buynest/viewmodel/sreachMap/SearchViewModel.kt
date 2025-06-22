package com.example.buynest.viewmodel.sreachMap

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.buynest.BuildConfig.PLACES_API_KEY
import com.example.buynest.model.entity.PlaceData
import com.example.buynest.model.uistate.ResponseState
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.*
import com.google.android.libraries.places.api.net.*
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SearchViewModel(context: Context) : ViewModel() {
    init {
        if (!Places.isInitialized()) {
            Places.initialize(context.applicationContext, PLACES_API_KEY)
        }
    }

    private val placesClient: PlacesClient = Places.createClient(context)
    private val sessionToken = AutocompleteSessionToken.newInstance()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _places = MutableSharedFlow<ResponseState>(replay = 1)
    val places: SharedFlow<ResponseState> = _places.asSharedFlow()

    private val _message = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val message: SharedFlow<String> = _message.asSharedFlow()

    init {
        observeSearchQuery()
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    @OptIn(FlowPreview::class)
    private fun observeSearchQuery() {
        _searchQuery
            .debounce(500)
            .onEach { query ->
                if (query.isBlank()) {
                    _places.emit(ResponseState.Success(emptyList<PlaceData>()))
                } else {
                    fetchPlacePredictions(query)
                }
            }
            .launchIn(viewModelScope)
    }

    private fun fetchPlacePredictions(query: String) {
        viewModelScope.launch { _places.emit(ResponseState.Loading) }

        val request = FindAutocompletePredictionsRequest.builder()
            .setQuery(query)
            .setSessionToken(sessionToken)
            .setCountries("EG")
            .build()

        placesClient.findAutocompletePredictions(request)
            .addOnSuccessListener { response ->
                val predictions = response.autocompletePredictions.map { prediction ->
                    PlaceData(
                        placeId = prediction.placeId,
                        displayName = prediction.getPrimaryText(null).toString(),
                        address = prediction.getFullText(null).toString()
                    )
                }
                viewModelScope.launch { _places.emit(ResponseState.Success(predictions)) }
            }
            .addOnFailureListener { exception ->
                handleFailure("Error fetching predictions", exception)
            }
    }

    suspend fun fetchPlaceDetails(placeId: String): com.google.android.gms.maps.model.LatLng? {
        return try {
            val request = FetchPlaceRequest.builder(
                placeId,
                listOf(Place.Field.LAT_LNG, Place.Field.NAME, Place.Field.ADDRESS)
            ).build()

            val response = placesClient.fetchPlace(request).await()
            response.place.latLng
        } catch (exception: Exception) {
            handleFailure("Error fetching place details", exception)
            null
        }
    }

    private fun handleFailure(message: String, exception: Exception) {
        Log.e("SearchViewModel", "$message: ${exception.localizedMessage}")
        viewModelScope.launch {
            _places.emit(ResponseState.Error(message = exception.localizedMessage ?: "Unknown error"))
            _message.emit("$message: ${exception.localizedMessage ?: "Unknown error"}")
        }
    }
}
