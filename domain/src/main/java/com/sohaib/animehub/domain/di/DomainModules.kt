package com.sohaib.animehub.domain.di

import com.sohaib.animehub.domain.useCases.GetAnimeDetailsByIdUseCase
import com.sohaib.animehub.domain.useCases.GetAnimeListUseCase
import com.sohaib.animehub.domain.useCases.GetFavouriteAnimeListUseCase
import com.sohaib.animehub.domain.useCases.ObserveFavouriteAnimeIdsUseCase
import com.sohaib.animehub.domain.useCases.RefreshAnimeDetailsByIdUseCase
import com.sohaib.animehub.domain.useCases.ToggleFavouriteAnimeUseCase
import org.koin.dsl.lazyModule

val domainModule = lazyModule {
    factory { GetAnimeListUseCase(get()) }
    factory { GetAnimeDetailsByIdUseCase(get()) }
    factory { RefreshAnimeDetailsByIdUseCase(get()) }
    factory { ObserveFavouriteAnimeIdsUseCase(get()) }
    factory { ToggleFavouriteAnimeUseCase(get()) }
    factory { GetFavouriteAnimeListUseCase(get()) }
}