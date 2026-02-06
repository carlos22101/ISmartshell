package com.carlos.ismartshell.core.di

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.carlos.ismartshell.ISmartShellApplication
import com.carlos.ismartshell.features.auth.di.AuthModule
import com.carlos.ismartshell.features.auth.presentation.viewmodels.LoginViewModel
import com.carlos.ismartshell.features.buyer.di.BuyerModule
import com.carlos.ismartshell.features.buyer.presentation.viewmodels.HomeBuyerViewModel
import com.carlos.ismartshell.features.seller.di.SellerModule
import com.carlos.ismartshell.features.seller.presentation.viewmodels.CreateStoreViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        // Auth
        initializer {
            val app = ismartApplication()
            val useCase = AuthModule.provideLoginUseCase(app.container.authRepository)
            LoginViewModel(useCase)
        }
        // Buyer
        initializer {
            val app = ismartApplication()
            val useCase = BuyerModule.provideGetStoresUseCase(app.container.storeRepository)
            HomeBuyerViewModel(useCase)
        }
        // Seller
        initializer {
            val app = ismartApplication()
            val useCase = SellerModule.provideCreateStoreUseCase(app.container.sellerRepository)
            CreateStoreViewModel(useCase)
        }
    }
}

fun CreationExtras.ismartApplication(): ISmartShellApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as ISmartShellApplication)