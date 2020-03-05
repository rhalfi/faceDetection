package com.moddetector.dagger

import com.moddetector.ModApp
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(
        modules = [
            AndroidSupportInjectionModule::class,
            AppModule::class,
            ServicesModule::class,
            FragmentModule::class,
            BroadcastReceiverModule::class,
            ActivitiesModule::class]
)
interface AppComponent : AndroidInjector<ModApp> {
}
