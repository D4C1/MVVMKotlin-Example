package com.example.mvvmkotlin.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mvvmkotlin.ui.repo.RepoViewModerl
import com.example.mvvmkotlin.ui.search.SearchViewModel
import com.example.mvvmkotlin.ui.user.UserViewModel
import com.example.mvvmkotlin.viewmodel.GithubViewModelFactory
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(UserViewModel::class)
    abstract fun bindUserViewModel(userViewModel: UserViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(RepoViewModerl::class)
    abstract fun bindRepoViewModel(repoViewModerl: RepoViewModerl): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SearchViewModel::class)
    abstract fun bindSearchViewModel(searchViewModel: SearchViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(factory:GithubViewModelFactory): ViewModelProvider.Factory

}