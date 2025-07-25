package com.example.buynest.viewmodel.address

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.buynest.model.entity.AddressModel
import com.example.buynest.model.state.UiResponseState
import com.example.buynest.model.repository.address.AddressRepository
import com.example.buynest.type.MailingAddressInput
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AddressViewModel(
    private val repository: AddressRepository
) : ViewModel() {

    private val _addresses = MutableStateFlow<List<AddressModel>>(emptyList())
    val addresses: StateFlow<List<AddressModel>> = _addresses

    private val _addressUiState = MutableStateFlow<UiResponseState>(UiResponseState.Loading)
    val addressUiState: StateFlow<UiResponseState> = _addressUiState

    private val _defaultAddress = MutableStateFlow<AddressModel?>(null)
    val defaultAddress: StateFlow<AddressModel?> = _defaultAddress

    private val _editingAddress = MutableStateFlow<AddressModel?>(null)
    val editingAddress: StateFlow<AddressModel?> = _editingAddress

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadAddresses(token: String) {
        Log.d("AddressViewModel", "Loading addresses with token: $token")
        viewModelScope.launch {
            _addressUiState.value = UiResponseState.Loading
            repository.getAllAddresses(token).fold(
                onSuccess = { list ->
                    Log.d("AddressViewModel", "Loaded ${list.size} addresses")
                    _addresses.value = list
                    _addressUiState.value = UiResponseState.Success(list)
                },
                onFailure = { ex ->
                    val errorMsg = ex.message ?: "Unknown error loading addresses"
                    Log.e("AddressViewModel", "Error loading addresses: $errorMsg")
                    _addressUiState.value = UiResponseState.Error(errorMsg)
                    _addresses.value = emptyList()
                }
            )
        }
    }

    fun addAddress(token: String, addressInput: MailingAddressInput) {
        Log.d("AddressViewModel", "Adding address: $addressInput")
        viewModelScope.launch {
            repository.addAddress(token, addressInput).fold(
                onSuccess = {
                    Log.d("AddressViewModel", "Address added successfully")
                    loadAddresses(token)
                },
                onFailure = { ex ->
                    val errorMsg = ex.message ?: "Failed to add address"
                    Log.e("AddressViewModel", "Error adding address: $errorMsg")
                    _error.value = errorMsg
                }
            )
        }
    }

    fun deleteAddress(token: String, addressId: String) {
        Log.d("AddressViewModel", "Deleting address ID: $addressId")
        viewModelScope.launch {
            repository.deleteAddress(token, addressId).fold(
                onSuccess = {
                    Log.d("AddressViewModel", "Address deleted successfully")
                    loadAddresses(token)
                },
                onFailure = { ex ->
                    val errorMsg = ex.message ?: "Failed to delete address"
                    Log.e("AddressViewModel", "Error deleting address: $errorMsg")
                    _error.value = errorMsg
                }
            )
        }
    }

    fun updateAddress(token: String, addressId: String, addressInput: MailingAddressInput) {
        Log.d("AddressViewModel", "Updating address ID: $addressId")
        viewModelScope.launch {
            repository.updateAddress(token, addressId, addressInput).fold(
                onSuccess = {
                    Log.d("AddressViewModel", "Address updated successfully")
                    loadAddresses(token)
                    stopEditingAddress()
                },
                onFailure = { ex ->
                    val errorMsg = ex.message ?: "Failed to update address"
                    Log.e("AddressViewModel", "Error updating address: $errorMsg")
                    _error.value = errorMsg
                }
            )
        }
    }

    fun setDefaultAddress(token: String, addressId: String) {
        Log.d("AddressViewModel", "Setting default address ID: $addressId")
        viewModelScope.launch {
            repository.setDefaultAddress(token, addressId).fold(
                onSuccess = { defaultAddr ->
                    Log.d("AddressViewModel", "Default address set: $defaultAddr")
                    _defaultAddress.value = defaultAddr
                    loadAddresses(token)
                },
                onFailure = { ex ->
                    val errorMsg = ex.message ?: "Failed to set default address"
                    Log.e("AddressViewModel", "Error setting default address: $errorMsg")
                    _error.value = errorMsg
                }
            )
        }
    }

    fun loadDefaultAddress(token: String) {
        Log.d("AddressViewModel", "Loading default address")
        viewModelScope.launch {
            repository.getDefaultAddress(token).fold(
                onSuccess = { defAddr ->
                    Log.d("AddressViewModel", "Default address loaded")
                    _defaultAddress.value = defAddr
                },
                onFailure = { ex ->
                    val errorMsg = ex.message ?: "Failed to load default address"
                    Log.e("AddressViewModel", "Error loading default address: $errorMsg")
                    _error.value = errorMsg
                }
            )
        }
    }

    fun startEditingAddress(address: AddressModel) {
        _editingAddress.value = address
    }

    fun stopEditingAddress() {
        _editingAddress.value = null
    }

    fun extractFromAddress(
        address: String,
        extractor: (List<String>) -> String
    ): String {
        val parts = address.split(",").map { it.trim() }
        return extractor(parts)
    }
}
