package com.sohaib.animehub.data.di

import com.sohaib.animehub.data.dataSources.local.AnimeLocalDataSource
import com.sohaib.animehub.data.dataSources.local.PreferencesLocalDataSource
import com.sohaib.animehub.data.dataSources.remote.AnimeRemoteDataSource
import com.sohaib.animehub.data.repositories.AnimeRepositoryImpl
import com.sohaib.animehub.data.repositories.PreferencesRepositoryImpl
import com.sohaib.animehub.domain.repositories.AnimeRepository
import com.sohaib.animehub.domain.repositories.PreferencesRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.lazyModule

val dataSourceModule = lazyModule {
    // Local DataSource
    single { AnimeLocalDataSource(get()) }
    single { PreferencesLocalDataSource(androidContext()) }

    // Remote DataSource
    single { AnimeRemoteDataSource(get()) }
}

val repositoriesModule = lazyModule {
    single<AnimeRepository> { AnimeRepositoryImpl(get(), get(), get()) }
    single<PreferencesRepository> { PreferencesRepositoryImpl(get()) }
}