package com.sohaib.animehub.core.database.di

import androidx.room.Room
import com.sohaib.animehub.core.database.AppDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.lazyModule

val coreDatabaseModule = lazyModule {
    single {
        Room
            .databaseBuilder(
                context = androidContext(),
                klass = AppDatabase::class.java,
                name = "animehub_db"
            )
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()
    }

    single { get<AppDatabase>().animeDao() }
    single { get<AppDatabase>().animeRemoteKeysDao() }
    single { get<AppDatabase>().favouriteAnimeDao() }
}