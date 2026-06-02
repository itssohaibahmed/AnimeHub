package com.sohaib.animehub.di

import com.sohaib.animehub.core.common.di.coreCommonModule
import com.sohaib.animehub.core.database.di.coreDatabaseModule
import com.sohaib.animehub.core.network.di.coreNetworkModule
import com.sohaib.animehub.data.di.dataSourceModule
import com.sohaib.animehub.data.di.repositoriesModule
import com.sohaib.animehub.domain.di.domainModule
import com.sohaib.animehub.feature.anime.details.di.animeDetailsFeatureModule
import com.sohaib.animehub.feature.home.di.homeFeatureModule
import com.sohaib.animehub.feature.settings.di.settingFeatureModule
import com.sohaib.animehub.feature.splash.di.splashFeatureModule

class KoinModules {

    private val coreList = listOf(
        coreCommonModule,
        coreDatabaseModule,
        coreNetworkModule
    )
    private val dataList = listOf(
        dataSourceModule,
        repositoriesModule
    )
    private val domainList = listOf(
        domainModule,
    )
    private val featureList = listOf(
        splashFeatureModule,
        homeFeatureModule,
        animeDetailsFeatureModule,
        settingFeatureModule,
    )

    val list = coreList + dataList + domainList + featureList
}