package com.sohaib.animehub.feature.settings.di

import com.sohaib.animehub.feature.settings.viewModel.SettingViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.lazyModule

val settingFeatureModule = lazyModule {
    viewModel { SettingViewModel(get()) }
}