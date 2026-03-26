package com.carlos.ismartshell.features.seller.data.repositories

import com.carlos.ismartshell.core.network.ApiService
import com.carlos.ismartshell.features.buyer.data.mappers.BuyerStoreMapper
import com.carlos.ismartshell.features.buyer.domain.entities.Order
import com.carlos.ismartshell.features.buyer.domain.entities.Product
import com.carlos.ismartshell.features.seller.data.mappers.SellerMapper
import com.carlos.ismartshell.features.seller.data.models.SellerModels
import com.carlos.ismartshell.features.seller.domain.entities.SellerStore
import com.carlos.ismartshell.features.seller.domain.repositories.SellerRepository
import com.google.gson.Gson
import com.carlos.ismartshell.core.network.ApiResponse
import javax.inject.Inject

class SellerRepositoryImpl @Inject constructor(
    private val api: ApiService
) : SellerRepository {

    override suspend fun getMyStores(): Result<List<SellerStore>> = runCatching {
        val res = api.getMyBusinesses()
        res.body()?.data?.map { SellerMapper.toDomain(it) }
            ?: error(res.body()?.error ?: "Error al obtener tus tiendas")
    }

    override suspend fun getStoreDetail(id: String): Result<SellerStore> = runCatching {
        val res = api.getBusinessById(id)
        val body = res.body()?.data ?: error(res.body()?.error ?: "Error al obtener el detalle")
        
        val storeBase = SellerMapper.toDomain(
            SellerModels.SellerBusinessDto(
                id = body.id,
                ownerId = body.ownerId,
                name = body.name,
                description = body.description,
                type = body.type,
                latitude = body.latitude,
                longitude = body.longitude,
                active = body.active
            )
        )

        val productsRes = api.getProductsByBusiness(id)
        val products = productsRes.body()?.data
            ?.map { BuyerStoreMapper.productToDomain(it) } ?: emptyList()
            
        val ordersRes = api.getOrdersByBusiness(id)
        val orders = ordersRes.body()?.data
            ?.map { BuyerStoreMapper.orderToDomain(it) } ?: emptyList()

        storeBase.copy(products = products, orders = orders)
    }

    override suspend fun createStore(
        name: String, description: String, type: String, lat: Double, lng: Double
    ): Result<SellerStore> = runCatching {
        val res = api.createBusiness(SellerModels.CreateBusinessRequest(name, description, type, lat, lng))
        val dto = res.body()?.data ?: error(res.body()?.error ?: "Error al crear tienda")
        SellerMapper.toDomain(dto)
    }

    override suspend fun updateStore(
        id: String, name: String, description: String, type: String, lat: Double, lng: Double
    ): Result<SellerStore> = runCatching {
        val res = api.updateBusiness(id, SellerModels.UpdateBusinessRequest(name, description, type, lat, lng))
        val dto = res.body()?.data ?: error(res.body()?.error ?: "Error al actualizar tienda")
        SellerMapper.toDomain(dto)
    }

    override suspend fun deleteStore(id: String): Result<Unit> = runCatching {
        val res = api.deleteBusiness(id)
        if (!res.isSuccessful) {
            if (res.code() == 405) error("Error 405: DELETE no permitido en el servidor.")
            val errorBody = res.errorBody()?.string()
            val errorMsg = if (!errorBody.isNullOrBlank()) {
                try { Gson().fromJson(errorBody, ApiResponse::class.java).error } catch(e: Exception) { null }
            } else null
            error(errorMsg ?: "Error al eliminar tienda (${res.code()})")
        }
    }

    override suspend fun createProduct(
        businessId: String, name: String, description: String, price: Double, stock: Int
    ): Result<Product> = runCatching {
        val res = api.createProduct(businessId, SellerModels.CreateProductRequest(name, description, price, stock))
        val dto = res.body()?.data ?: error(res.body()?.error ?: "Error al crear producto")
        BuyerStoreMapper.productToDomain(dto)
    }

    override suspend fun updateProduct(
        productId: String, name: String, description: String, price: Double, stock: Int
    ): Result<Product> = runCatching {
        val res = api.updateProduct(productId, SellerModels.UpdateProductRequest(name, description, price, stock))
        val dto = res.body()?.data ?: error(res.body()?.error ?: "Error al actualizar producto")
        BuyerStoreMapper.productToDomain(dto)
    }

    override suspend fun updateProductStock(productId: String, newStock: Int): Result<Product> = runCatching {
        val res = api.updateProductStock(productId, SellerModels.UpdateStockRequest(newStock))
        if (!res.isSuccessful) {
            if (res.code() == 404) error("Error 404: La ruta de stock no existe en el servidor. Verifica el router de Go.")
            val errorBody = res.errorBody()?.string()
            val errorMsg = if (!errorBody.isNullOrBlank()) {
                try { Gson().fromJson(errorBody, ApiResponse::class.java).error } catch(e: Exception) { null }
            } else null
            error(errorMsg ?: "Error al actualizar stock (${res.code()})")
        }
        val dto = res.body()?.data ?: error("Error al procesar respuesta de stock")
        BuyerStoreMapper.productToDomain(dto)
    }

    override suspend fun deleteProduct(productId: String): Result<Unit> = runCatching {
        val res = api.deleteProduct(productId)
        if (!res.isSuccessful) {
            val errorBody = res.errorBody()?.string()
            val errorMsg = if (!errorBody.isNullOrBlank()) {
                try { Gson().fromJson(errorBody, ApiResponse::class.java).error } catch(e: Exception) { null }
            } else null
            error(errorMsg ?: "Error al eliminar producto (${res.code()})")
        }
    }

    override suspend fun getOrdersByBusiness(businessId: String): Result<List<Order>> = runCatching {
        val res = api.getOrdersByBusiness(businessId)
        res.body()?.data?.map { BuyerStoreMapper.orderToDomain(it) }
            ?: error(res.body()?.error ?: "Error al obtener órdenes")
    }

    override suspend fun scanOrderQr(qrCode: String): Result<Order> = runCatching {
        val res = api.scanOrderQr(SellerModels.ScanQrRequest(qrCode))
        if (!res.isSuccessful) {
            val errorBody = res.errorBody()?.string()
            var errorMsg = try { Gson().fromJson(errorBody, ApiResponse::class.java).error } catch(e: Exception) { null }
            
            if (errorMsg == "invalid status transition") {
                errorMsg = "La orden aún no está marcada como 'LISTA'."
            } else if (res.code() == 410 || errorMsg?.contains("expired") == true) {
                errorMsg = "La orden ha expirado."
            }

            error(errorMsg ?: "Error al escanear QR (${res.code()})")
        }
        val dto = res.body()?.data ?: error("Código inválido")
        BuyerStoreMapper.orderToDomain(dto)
    }

    override suspend fun markOrderAsReady(orderId: String): Result<Order> = runCatching {
        val res = api.markOrderAsReady(orderId)
        if (!res.isSuccessful) {
            val errorBody = res.errorBody()?.string()
            val errorMsg = try { Gson().fromJson(errorBody, ApiResponse::class.java).error } catch(e: Exception) { null }
            error(errorMsg ?: "Error al marcar como listo (${res.code()})")
        }
        val dto = res.body()?.data ?: error("Error al procesar respuesta")
        BuyerStoreMapper.orderToDomain(dto)
    }
}
