package top.k88936.nextcloud_tv.di

import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import top.k88936.nextcloud_tv.data.local.CredentialStore
import top.k88936.nextcloud_tv.data.repository.AuthRepository
import top.k88936.nextcloud_tv.data.repository.FilesRepository
import top.k88936.nextcloud_tv.ui.auth.AuthViewModel
import top.k88936.nextcloud_tv.ui.screens.files.FilesViewModel

val dataModule = module {
    single { CredentialStore(androidContext()) }
    single { AuthRepository(get()) }
    single { FilesRepository() }
}

val viewModelModule = module {
    viewModel { AuthViewModel(get()) }
    viewModel { FilesViewModel(get(), get()) }
}

val appModules = listOf(dataModule, viewModelModule)
