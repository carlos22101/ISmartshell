package com.carlos.ismartshell.core.network

import com.carlos.ismartshell.features.auth.data.models.LoginRequest
import com.carlos.ismartshell.features.auth.data.models.LoginResponseDto
import com.carlos.ismartshell.features.auth.data.models.RegisterRequest
import com.carlos.ismartshell.features.auth.data.models.UserDto
import com.carlos.ismartshell.features.buyer.data.models.BuyerStoreDto
import com.carlos.ismartshell.features.seller.data.models.CreateStoreRequest
import com.carlos.ismartshell.features.seller.data.models.SellerStoreDto
import com.carlos.ismartshell.features.seller.data.models.UpdateStoreRequest
import retrofit2.http.*

interface ApiService {

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): LoginResponseDto

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponseDto

    @GET("api/auth/me")
    suspend fun getMe(): UserDto

    @GET("api/stores")
    suspend fun getStores(): List<BuyerStoreDto>

    @GET("api/stores/{id}")
    suspend fun getStoreDetail(@Path("id") id: Int): BuyerStoreDto

    @POST("api/stores")
    suspend fun createStore(@Body store: CreateStoreRequest): SellerStoreDto


    @PUT("api/stores/{store_id}")
    suspend fun updateStore(
        @Path("store_id") id: Int, 
        @Body store: UpdateStoreRequest
    ): SellerStoreDto

    @DELETE("api/stores/{id}")
    suspend fun deleteStore(@Path("id") id: Int): Unit

    @POST("api/stores/{id}/points")
    suspend fun addDeliveryPoint(@Path("id") id: Int, @Body point: Map<String, Any>): Any

    @GET("api/stores/{id}/points")
    suspend fun getDeliveryPoints(@Path("id") id: Int): List<Any>
}
