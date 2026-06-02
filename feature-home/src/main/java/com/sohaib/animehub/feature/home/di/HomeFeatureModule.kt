package com.sohaib.animehub.feature.home.di

import com.sohaib.animehub.feature.home.viewModel.HomeViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.lazyModule

val homeFeatureModule = lazyModule {
    viewModel { HomeViewModel(get(), get(), get()) }
}