package com.moddetector.dagger

import android.content.Context
import com.moddetector.dagger.factory.ViewModelModule
import com.moddetector.modules.AndroidApisModule
import com.moddetector.modules.azure.AINetworkModule

import dagger.Module
import dagger.Provides
import javax.inject.Singleton


@Module(includes = [ViewModelModule::class])
open class AppModule(val context: Context) {

    @Singleton
    @Provides
    open fun provideAINetworkModule(): AINetworkModule{
        return AINetworkModule(context)
    }

    @Singleton
    @Provides
    open fun provideAndroidApisModule(): AndroidApisModule{
        return AndroidApisModule(context)
    }


}