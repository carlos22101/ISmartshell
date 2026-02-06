package com.carlos.ismartshell.core.di

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.carlos.ismartshell.ISmartShellApplication
import com.carlos.ismartshell.features.auth.di.AuthModule
import com.carlos.ismartshell.features.auth.presentation.viewmodels.LoginViewModel
import com.carlos.ismartshell.features.auth.presentation.viewmodels.RegisterViewModel
import com.carlos.ismartshell.features.buyer.di.BuyerModule
import com.carlos.ismartshell.features.buyer.presentation.viewmodels.HomeBuyerViewModel
import com.carlos.ismartshell.features.seller.di.SellerModule
import com.carlos.ismartshell.features.seller.presentation.viewmodels.CreateStoreViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {

        initializer {
            val app = ismartApplication()
            val useCase = AuthModule.provideLoginUseCase(app.container.authRepository)
            LoginViewModel(useCase)
        }
        initializer {
            val app = ismartApplication()
            val useCase = AuthModule.provideRegisterUseCase(app.container.authRepository)
            RegisterViewModel(useCase)
        }

        initializer {
            val app = ismartApplication()
            val useCase = BuyerModule.provideGetStoresUseCase(app.container.storeRepository)
            HomeBuyerViewModel(useCase)
        }

        initializer {
            val app = ismartApplication()
            val repo = app.container.sellerRepository


            CreateStoreViewModel(
                getStoresUseCase = SellerModule.provideGetStoresUseCase(repo),
                createStoreUseCase = SellerModule.provideCreateStoreUseCase(repo),
                updateStoreUseCase = SellerModule.provideUpdateStoreUseCase(repo),
                deleteStoreUseCase = SellerModule.provideDeleteStoreUseCase(repo),
                getDetailUseCase = SellerModule.provideGetDetailUseCase(repo)
            )
        }
    }
}

fun CreationExtras.ismartApplication(): ISmartShellApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as ISmartShellApplication)