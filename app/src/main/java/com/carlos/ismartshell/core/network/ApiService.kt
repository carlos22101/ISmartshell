package com.carlos.ismartshell.core.network

import com.carlos.ismartshell.core.notifications.FcmTokenRequest
import com.carlos.ismartshell.features.auth.data.models.AuthModels
import com.carlos.ismartshell.features.buyer.data.models.BuyerStoreDto
import com.carlos.ismartshell.features.seller.data.models.SellerModels
import retrofit2.Response
import retrofit2.http.*


data class ApiResponse<T>(val data: T? = null, val error: String? = null)

interface ApiService {

    // ── Auth ──────────────────────────────────────────────────────────────────
    @POST("api/v1/auth/register")
    suspend fun register(@Body body: AuthModels.RegisterRequest): Response<ApiResponse<AuthModels.AuthData>>

    @POST("api/v1/auth/login")
    suspend fun login(@Body body: AuthModels.LoginRequest): Response<ApiResponse<AuthModels.AuthData>>

    @GET("api/v1/users/me")
    suspend fun me(): Response<ApiResponse<AuthModels.UserDto>>

    // ── Businesses ────────────────────────────────────────────────────────────
    @GET("api/v1/businesses")
    suspend fun getNearbyBusinesses(
        @Query("lat") lat: Double,
        @Query("lng") lng: Double,
        @Query("radius") radiusKm: Double = 5.0
    ): Response<ApiResponse<List<BuyerStoreDto.BusinessDto>>>

    @GET("api/v1/businesses/{id}")
    suspend fun getBusinessById(@Path("id") id: String): Response<ApiResponse<BuyerStoreDto.BusinessDto>>

    @GET("api/v1/businesses/mine")
    suspend fun getMyBusinesses(): Response<ApiResponse<List<SellerModels.SellerBusinessDto>>>

    @POST("api/v1/businesses")
    suspend fun createBusiness(@Body body: SellerModels.CreateBusinessRequest): Response<ApiResponse<SellerModels.SellerBusinessDto>>

    @PUT("api/v1/businesses/{id}")
    suspend fun updateBusiness(
        @Path("id") id: String,
        @Body body: SellerModels.UpdateBusinessRequest
    ): Response<ApiResponse<SellerModels.SellerBusinessDto>>

    @DELETE("api/v1/businesses/{id}")
    suspend fun deleteBusiness(@Path("id") id: String): Response<Unit>

    @POST("api/v1/businesses/{id}/delivery-points")
    suspend fun addDeliveryPoint(
        @Path("id") businessId: String,
        @Body body: SellerModels.DeliveryPointRequest
    ): Response<ApiResponse<BuyerStoreDto.DeliveryPointDto>>

    // ── Products ──────────────────────────────────────────────────────────────
    @GET("api/v1/businesses/{businessId}/products")
    suspend fun getProductsByBusiness(@Path("businessId") businessId: String): Response<ApiResponse<List<BuyerStoreDto.ProductDto>>>

    @POST("api/v1/businesses/{businessId}/products")
    suspend fun createProduct(
        @Path("businessId") businessId: String,
        @Body body: SellerModels.CreateProductRequest
    ): Response<ApiResponse<BuyerStoreDto.ProductDto>>

    @PUT("api/v1/products/{id}")
    suspend fun updateProduct(
        @Path("id") id: String,
        @Body body: SellerModels.UpdateProductRequest
    ): Response<ApiResponse<BuyerStoreDto.ProductDto>>

    @PATCH("api/v1/products/{id}/stock")
    suspend fun updateProductStock(
        @Path("id") id: String,
        @Body body: SellerModels.UpdateStockRequest
    ): Response<ApiResponse<BuyerStoreDto.ProductDto>>

    @DELETE("api/v1/products/{id}")
    suspend fun deleteProduct(@Path("id") id: String): Response<Unit>

    // ── Orders ────────────────────────────────────────────────────────────────
    @POST("api/v1/orders")
    suspend fun createOrder(@Body body: BuyerStoreDto.CreateOrderRequest): Response<ApiResponse<BuyerStoreDto.OrderDto>>

    @GET("api/v1/orders/my")
    suspend fun getMyOrders(): Response<ApiResponse<List<BuyerStoreDto.OrderDto>>>

    @GET("api/v1/orders/{id}")
    suspend fun getOrderById(@Path("id") id: String): Response<ApiResponse<BuyerStoreDto.OrderDto>>

    @POST("api/v1/orders/{id}/cancel")
    suspend fun cancelOrder(@Path("id") id: String): Response<ApiResponse<BuyerStoreDto.OrderDto>>

    @GET("api/v1/businesses/{businessId}/orders")
    suspend fun getOrdersByBusiness(@Path("businessId") businessId: String): Response<ApiResponse<List<BuyerStoreDto.OrderDto>>>

    @POST("api/v1/orders/scan")
    suspend fun scanOrderQr(@Body body: SellerModels.ScanQrRequest): Response<ApiResponse<BuyerStoreDto.OrderDto>>

    @POST("api/v1/orders/{id}/ready")
    suspend fun markOrderAsReady(@Path("id") id: String): Response<ApiResponse<BuyerStoreDto.OrderDto>>

    @POST("api/v1/users/fcm-token")
    suspend fun registerFcmToken(@Body request: FcmTokenRequest): Response<Unit>
}
