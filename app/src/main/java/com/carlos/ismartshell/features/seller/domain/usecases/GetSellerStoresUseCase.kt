package com.carlos.ismartshell.features.seller.domain.usecases
import com.carlos.ismartshell.features.seller.domain.entities.SellerStore
import com.carlos.ismartshell.features.seller.domain.repositories.SellerRepository
import javax.inject.Inject

class GetSellerStoresUseCase @Inject constructor(private val repository: SellerRepository) {
    suspend operator fun invoke(): List<SellerStore> = repository.getStores()
}
