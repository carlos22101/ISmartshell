package com.carlos.ismartshell.features.seller.domain.usecases
import com.carlos.ismartshell.features.seller.domain.repositories.SellerRepository
import javax.inject.Inject

class DeleteStoreUseCase @Inject constructor(private val repository: SellerRepository) {
    suspend operator fun invoke(id: Int) = repository.deleteStore(id)
}