package com.example.buynest.repository.currency

import android.content.Context
import com.example.buynest.model.remote.rest.IRemoteDataSource

class CurrencyRepositoryImpl(private val remoteDataSource: IRemoteDataSource): ICurrencyRepository {
    override suspend fun getExchangeRate(context: Context): Double {
        return remoteDataSource.getUsdToTargetRate(context)
    }
}
