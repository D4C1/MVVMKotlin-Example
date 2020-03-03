package com.example.mvvmkotlin.di

import com.example.mvvmkotlin.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MainActivityModule {

    @ContributesAndroidInjector(modules = [FragmentBuilderModule::class])
    abstract fun contributesMainActivity(): MainActivity
}