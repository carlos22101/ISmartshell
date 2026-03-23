package com.carlos.ismartshell.features.seller.data.repositories

import com.carlos.ismartshell.core.network.ApiService
import com.carlos.ismartshell.features.buyer.data.mappers.BuyerStoreMapper
import com.carlos.ismartshell.features.buyer.domain.entities.Order
import com.carlos.ismartshell.features.buyer.domain.entities.Product
import com.carlos.ismartshell.features.seller.data.mappers.SellerMapper
import com.carlos.ismartshell.features.seller.data.models.SellerModels
import com.carlos.ismartshell.features.seller.domain.entities.SellerStore
import com.carlos.ismartshell.features.seller.domain.repositories.SellerRepository
import javax.inject.Inject

class SellerRepositoryImpl @Inject constructor(
    private val api: ApiService
) : SellerRepository {

    override suspend fun getMyStores(): Result<List<SellerStore>> = runCatching {
        val res = api.getMyBusinesses()
        res.body()?.data?.map { SellerMapper.toDomain(it) }
            ?: error(res.body()?.error ?: "Error")
    }

    override suspend fun getStoreDetail(id: String): Result<SellerStore> = runCatching {
        val res = api.getBusinessById(id)
        val body = res.body()?.data ?: error(res.body()?.error ?: "Error")
        
        // Mapeamos el DTO de Business a SellerBusinessDto para usar el mapper de Seller
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
        if (!res.isSuccessful) error("Error al eliminar tienda")
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

    override suspend fun deleteProduct(productId: String): Result<Unit> = runCatching {
        val res = api.deleteProduct(productId)
        if (!res.isSuccessful) error("Error al eliminar producto")
    }

    override suspend fun getOrdersByBusiness(businessId: String): Result<List<Order>> = runCatching {
        val res = api.getOrdersByBusiness(businessId)
        res.body()?.data?.map { BuyerStoreMapper.orderToDomain(it) }
            ?: error(res.body()?.error ?: "Error al obtener órdenes")
    }

    override suspend fun scanOrderQr(qrCode: String): Result<Order> = runCatching {
        val res = api.scanOrderQr(SellerModels.ScanQrRequest(qrCode))
        val dto = res.body()?.data ?: error(res.body()?.error ?: "QR inválido")
        BuyerStoreMapper.orderToDomain(dto)
    }
}
