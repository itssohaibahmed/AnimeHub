package com.sohaib.animehub.feature.favourites.di

import com.sohaib.animehub.feature.favourites.viewModel.FavouriteViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.lazyModule

val favouriteFeatureModule = lazyModule {
    viewModel { FavouriteViewModel(get(), get()) }
}