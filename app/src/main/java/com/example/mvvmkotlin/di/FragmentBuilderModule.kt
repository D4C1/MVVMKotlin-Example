package com.example.mvvmkotlin.di

import com.example.mvvmkotlin.ui.repo.RepoFragment
import com.example.mvvmkotlin.ui.search.SearchFragment
import com.example.mvvmkotlin.ui.user.UserFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentBuilderModule {

    @ContributesAndroidInjector
    abstract fun contributeRepoFragment(): RepoFragment

    @ContributesAndroidInjector
    abstract fun contributeUserFragment(): UserFragment

    @ContributesAndroidInjector
    abstract fun contributeSearchFragment(): SearchFragment
}