package com.example.buynest.views.map

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.buynest.model.entity.PlaceData
import com.example.buynest.model.state.UiResponseState
import com.example.buynest.ui.theme.MainColor
import com.example.buynest.viewmodel.sreachMap.SearchViewModel
import com.example.buynest.views.component.MapSearchBar
import com.example.buynest.views.component.SearchResultItem
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun MapSearchScreen(
    onBack: () -> Unit,
    onPlaceSelected: (GeoPoint, String) -> Unit,
    searchViewModel: SearchViewModel = koinViewModel()
) {
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }
    val searchResults by searchViewModel.places.collectAsStateWithLifecycle(initialValue = UiResponseState.Loading)
    val scope = rememberCoroutineScope()

    LaunchedEffect(searchQuery.text) {
        searchViewModel.updateSearchQuery(searchQuery.text)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(modifier = Modifier.fillMaxWidth().statusBarsPadding()) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.padding(8.dp).size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
            MapSearchBar(
                searchQuery = searchQuery,
                onQueryChange = { searchQuery = it }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (searchResults) {
            is UiResponseState.Loading -> {
                CircularProgressIndicator(color = MainColor)
            }

            is UiResponseState.Success<*> -> {
                val results = (searchResults as UiResponseState.Success<List<*>>).data.filterIsInstance<PlaceData>()

                if (results.isEmpty()) {
                    Text(
                        text = "No results found",
                        color = Color.White,
                        modifier = Modifier.padding(16.dp)
                    )
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(results.size) { index ->
                            val place = results[index]
                            SearchResultItem(
                                address = place.address ?: "Unknown",
                                onClick = {
                                    scope.launch {
                                        val latLng = searchViewModel.fetchPlaceDetails(place.placeId)
                                        latLng?.let {
                                            onPlaceSelected(
                                                GeoPoint(it.latitude, it.longitude),
                                                place.address.toString()
                                            )
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }

            is UiResponseState.Error -> {
                Text(
                    text = "Error: ${(searchResults as UiResponseState.Error).message}",
                    color = Color.Red,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}
