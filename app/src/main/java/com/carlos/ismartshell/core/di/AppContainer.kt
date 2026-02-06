package com.carlos.ismartshell.core.di

import android.content.Context
import com.carlos.ismartshell.core.local.TokenManager
import com.carlos.ismartshell.core.network.ApiService
import com.carlos.ismartshell.core.network.AuthInterceptor
import com.carlos.ismartshell.features.auth.data.repositories.AuthRepositoryImpl
import com.carlos.ismartshell.features.auth.domain.repositories.AuthRepository
import com.carlos.ismartshell.features.buyer.data.repositories.StoreRepositoryImpl
import com.carlos.ismartshell.features.buyer.domain.repositories.StoreRepository
import com.carlos.ismartshell.features.seller.data.repositories.SellerRepositoryImpl
import com.carlos.ismartshell.features.seller.domain.repositories.SellerRepository
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

interface AppContainer {
    val authRepository: AuthRepository
    val storeRepository: StoreRepository
    val sellerRepository: SellerRepository
    val tokenManager: TokenManager
}

class DefaultAppContainer(context: Context) : AppContainer {

    override val tokenManager = TokenManager(context)

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(AuthInterceptor(tokenManager))
        .build()

    // CAMBIA LA URL SI ES NECESARIO (10.0.2.2 para emulador Android standard)
    private val retrofit = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:8080/")
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient)
        .build()

    private val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }

    override val authRepository: AuthRepository by lazy {
        AuthRepositoryImpl(apiService, tokenManager)
    }

    override val storeRepository: StoreRepository by lazy {
        StoreRepositoryImpl(apiService)
    }

    override val sellerRepository: SellerRepository by lazy {
        SellerRepositoryImpl(apiService)
    }
}