package com.moddetector.dagger

import com.moddetector.services.ApiService
import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
abstract class ServicesModule {
    @ContributesAndroidInjector
    internal abstract fun contributeApiServiceInjector(): ApiService

}