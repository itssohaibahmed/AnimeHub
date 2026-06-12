package com.sohaib.animehub.feature.anime.details.di

import com.sohaib.animehub.feature.anime.details.viewModel.AnimeDetailsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.lazyModule

val animeDetailsFeatureModule = lazyModule {
    viewModel { AnimeDetailsViewModel(get(), get(), get(), get()) }
}