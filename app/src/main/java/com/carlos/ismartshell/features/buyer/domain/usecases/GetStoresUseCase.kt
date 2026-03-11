package com.carlos.ismartshell.features.buyer.domain.usecases
import com.carlos.ismartshell.features.buyer.domain.entities.BuyerStore
import com.carlos.ismartshell.features.buyer.domain.repositories.StoreRepository
import javax.inject.Inject

class GetStoresUseCase @Inject constructor(private val repository: StoreRepository) {
    suspend operator fun invoke(): List<BuyerStore> = repository.getStores()
}
