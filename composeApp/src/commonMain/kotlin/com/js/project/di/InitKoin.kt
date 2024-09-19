package com.js.project.di

import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initKoin(
    config: KoinAppDeclaration? = null
) {
    startKoin {
        config?.invoke(this)
        modules(
            serviceModule,
            manageModule,
            platformModule,
            dataSourceModules,
            repositoryModules,
            useCaseModules,
            viewModelModule
        )
    }
}