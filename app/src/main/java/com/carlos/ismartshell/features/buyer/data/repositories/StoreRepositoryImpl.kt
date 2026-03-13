package com.carlos.ismartshell.features.buyer.data.repositories

import com.carlos.ismartshell.core.network.ApiService
import com.carlos.ismartshell.features.buyer.data.mappers.BuyerStoreMapper
import com.carlos.ismartshell.features.buyer.data.models.BuyerStoreDto
import com.carlos.ismartshell.features.buyer.domain.entities.*
import com.carlos.ismartshell.features.buyer.domain.repositories.StoreRepository
import javax.inject.Inject

class StoreRepositoryImpl @Inject constructor(
    private val api: ApiService
) : StoreRepository {

    override suspend fun getNearbyStores(lat: Double, lng: Double, radiusKm: Double): Result<List<BuyerStore>> =
        runCatching {
            val res = api.getNearbyBusinesses(lat, lng, radiusKm)
            res.body()?.data?.map { BuyerStoreMapper.toDomain(it) }
                ?: error(res.body()?.error ?: "Error al obtener tiendas")
        }

    override suspend fun getStoreById(id: String): Result<BuyerStore> = runCatching {
        val res = api.getBusinessById(id)
        val dto = res.body()?.data ?: error(res.body()?.error ?: "Tienda no encontrada")
        val store = BuyerStoreMapper.toDomain(dto)
        // Cargar productos
        val products = api.getProductsByBusiness(id).body()?.data
            ?.map { BuyerStoreMapper.productToDomain(it) } ?: emptyList()
        store.copy(products = products)
    }

    override suspend fun getProductsByStore(storeId: String): Result<List<Product>> = runCatching {
        val res = api.getProductsByBusiness(storeId)
        res.body()?.data?.map { BuyerStoreMapper.productToDomain(it) }
            ?: error(res.body()?.error ?: "Error al obtener productos")
    }

    override suspend fun createOrder(
        businessId: String, type: String,
        items: List<Pair<String, Int>>,
        deliveryPointId: String?,
        reservationHours: Int
    ): Result<Order> = runCatching {
        val request = BuyerStoreDto.CreateOrderRequest(
            businessId       = businessId,
            type             = type,
            items            = items.map { BuyerStoreDto.CreateOrderItemRequest(it.first, it.second) },
            deliveryPointId  = deliveryPointId,
            reservationHours = reservationHours
        )
        val res = api.createOrder(request)
        val dto = res.body()?.data ?: error(res.body()?.error ?: "Error al crear orden")
        BuyerStoreMapper.orderToDomain(dto)
    }

    override suspend fun getMyOrders(): Result<List<Order>> = runCatching {
        val res = api.getMyOrders()
        res.body()?.data?.map { BuyerStoreMapper.orderToDomain(it) }
            ?: error(res.body()?.error ?: "Error al obtener órdenes")
    }

    override suspend fun cancelOrder(orderId: String): Result<Order> = runCatching {
        val res = api.cancelOrder(orderId)
        val dto = res.body()?.data ?: error(res.body()?.error ?: "Error al cancelar")
        BuyerStoreMapper.orderToDomain(dto)
    }
}