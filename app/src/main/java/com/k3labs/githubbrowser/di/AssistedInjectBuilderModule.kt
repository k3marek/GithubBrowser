package com.k3labs.githubbrowser.di

import androidx.lifecycle.ViewModel
import com.k3labs.githubbrowser.ui.user.UserViewModel
import com.k3labs.githubbrowser.workers.SeedDatabaseWorker
import com.squareup.inject.assisted.dagger2.AssistedModule
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

/**
 * For usage with AssistedInject
 * 1. annotate your Dagger module (which has ViewModel bindings) with @AssistedModule.
 * 2. include "AssistedInject_YourNameOfModule::class" which is generated automatically
 *
 * {@see https://github.com/mlykotom/connecting-the-dots-sample}
 */
@Module(includes = [AssistedInject_AssistedInjectBuilderModule::class])
@AssistedModule
interface AssistedInjectBuilderModule : AssistedWorkerBindingModule,
    AssistedViewModelBindingModule

@Suppress("unused")
@Module
interface AssistedWorkerBindingModule {
    @Binds
    @IntoMap
    @WorkerKey(SeedDatabaseWorker::class)
    fun bindSeedDatabaseWorker(factory: SeedDatabaseWorker.Factory): AssistedCoroutineWorkerFactory

}

@Suppress("unused")
@Module
interface AssistedViewModelBindingModule {
    @Binds
    @IntoMap
    @ViewModelKey(UserViewModel::class)
    fun bindVMFactoryForUserViewModel(f: UserViewModel.Factory): AssistedSavedStateViewModelFactory<out ViewModel>

}