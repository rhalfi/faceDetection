package com.moddetector.dagger
import com.moddetector.ui.mod.ModFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
abstract class FragmentModule {

    @ContributesAndroidInjector
    internal abstract fun contributeModFragmentInjector(): ModFragment

}