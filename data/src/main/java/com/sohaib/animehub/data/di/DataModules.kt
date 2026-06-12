package com.sohaib.animehub.data.di

import com.sohaib.animehub.data.dataSources.local.AnimeLocalDataSource
import com.sohaib.animehub.data.dataSources.local.FavouriteLocalDataSource
import com.sohaib.animehub.data.dataSources.local.PreferencesLocalDataSource
import com.sohaib.animehub.data.dataSources.remote.AnimeRemoteDataSource
import com.sohaib.animehub.data.repositories.AnimeRepositoryImpl
import com.sohaib.animehub.data.repositories.FavouriteRepositoryImpl
import com.sohaib.animehub.data.repositories.PreferencesRepositoryImpl
import com.sohaib.animehub.domain.repositories.AnimeRepository
import com.sohaib.animehub.domain.repositories.FavouriteRepository
import com.sohaib.animehub.domain.repositories.PreferencesRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.lazyModule

val dataSourceModule = lazyModule {
    // Local DataSource
    single { AnimeLocalDataSource(get()) }
    single { FavouriteLocalDataSource(get()) }
    single { PreferencesLocalDataSource(androidContext(), get()) }

    // Remote DataSource
    single { AnimeRemoteDataSource(get()) }
}

val repositoriesModule = lazyModule {
    single<AnimeRepository> { AnimeRepositoryImpl(get(), get(), get()) }
    single<FavouriteRepository> { FavouriteRepositoryImpl(get()) }
    single<PreferencesRepository> { PreferencesRepositoryImpl(get()) }
}