package com.moddetector

import android.app.Activity
import android.app.Application
import android.app.Service
import android.content.BroadcastReceiver
import androidx.fragment.app.Fragment
import com.moddetector.dagger.AppModule
import com.moddetector.dagger.DaggerAppComponent
import dagger.android.*
import dagger.android.support.HasSupportFragmentInjector
import javax.inject.Inject

class ModApp: Application() , HasActivityInjector, HasServiceInjector, HasSupportFragmentInjector,
    HasBroadcastReceiverInjector {

    @Inject
    lateinit var dispatchingActivityInjector: DispatchingAndroidInjector<Activity>
    @Inject
    lateinit var dispatchingServiceInjector: DispatchingAndroidInjector<Service>
    @Inject
    lateinit var dispatchingFragmentInjector: DispatchingAndroidInjector<Fragment>
    @Inject
    lateinit var dispatchingBroadcastReceiverInjector: DispatchingAndroidInjector<BroadcastReceiver>

    override fun onCreate() {
        super.onCreate()

        val appComponent = DaggerAppComponent.builder().appModule(AppModule(this)).build()
        appComponent.inject(this)
    }

    override fun activityInjector(): DispatchingAndroidInjector<Activity> {
        return dispatchingActivityInjector
    }

    override fun serviceInjector(): DispatchingAndroidInjector<Service> {
        return dispatchingServiceInjector
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return dispatchingFragmentInjector
    }

    override fun broadcastReceiverInjector(): AndroidInjector<BroadcastReceiver> {
        return dispatchingBroadcastReceiverInjector
    }
}