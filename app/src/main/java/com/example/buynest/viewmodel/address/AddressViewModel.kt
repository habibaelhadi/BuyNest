package com.example.buynest.viewmodel.address

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.buynest.model.entity.AddressModel
import com.example.buynest.repository.address.AddressRepository
import com.example.buynest.type.MailingAddressInput
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AddressViewModel(
    private val repository: AddressRepository
) : ViewModel() {

    private val _addresses = MutableStateFlow<List<AddressModel>>(emptyList())
    val addresses: StateFlow<List<AddressModel>> = _addresses

    private val _defaultAddress = MutableStateFlow<AddressModel?>(null)
    val defaultAddress: StateFlow<AddressModel?> = _defaultAddress

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadAddresses(token: String) {
        viewModelScope.launch {
            repository.getAllAddresses(token).fold(
                onSuccess = { list ->
                    _addresses.value = list
                    _error.value = null
                },
                onFailure = { ex ->
                    _error.value = ex.message ?: "Unknown error loading addresses"
                }
            )
        }
    }

    fun addAddress(token: String, addressInput: MailingAddressInput) {
        viewModelScope.launch {
            repository.addAddress(token, addressInput).fold(
                onSuccess = {
                    loadAddresses(token)
                    _error.value = null
                },
                onFailure = { ex ->
                    _error.value = ex.message ?: "Failed to add address"
                }
            )
        }
    }

    fun deleteAddress(token: String, addressId: String) {
        viewModelScope.launch {
            repository.deleteAddress(token, addressId).fold(
                onSuccess = {
                    loadAddresses(token)
                    _error.value = null
                },
                onFailure = { ex ->
                    _error.value = ex.message ?: "Failed to delete address"
                }
            )
        }
    }

    fun setDefaultAddress(token: String, addressId: String) {
        viewModelScope.launch {
            repository.setDefaultAddress(token, addressId).fold(
                onSuccess = { defaultAddr ->
                    _defaultAddress.value = defaultAddr
                    _error.value = null
                },
                onFailure = { ex ->
                    _error.value = ex.message ?: "Failed to set default address"
                }
            )
        }
    }

    fun loadDefaultAddress(token: String) {
        viewModelScope.launch {
            repository.getDefaultAddress(token).fold(
                onSuccess = { defAddr ->
                    _defaultAddress.value = defAddr
                    _error.value = null
                },
                onFailure = { ex ->
                    _error.value = ex.message ?: "Failed to load default address"
                }
            )
        }
    }
}
