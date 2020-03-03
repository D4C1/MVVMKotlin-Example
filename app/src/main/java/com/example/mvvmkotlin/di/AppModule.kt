package com.example.mvvmkotlin.di

import android.app.Application
import androidx.room.Room
import com.example.mvvmkotlin.api.GithubApi
import com.example.mvvmkotlin.db.GithubDb
import com.example.mvvmkotlin.db.RepoDao
import com.example.mvvmkotlin.db.UserDao
import com.example.mvvmkotlin.utils.LiveDataCallAdapterFactory
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import javax.inject.Singleton

@Module(includes = [ViewModelModule::class])
class AppModule {
    @Singleton
    @Provides
    fun providesGithubApi(): GithubApi{
        return Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(LiveDataCallAdapterFactory())
            .build().create(GithubApi::class.java)
    }

    @Singleton
    @Provides
    fun provideDb(app: Application): GithubDb{
        return Room.databaseBuilder(app, GithubDb::class.java, "github.db")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun provideUserDao(db: GithubDb): UserDao {
        return db.userDao()
    }

    @Singleton
    @Provides
    fun provideRepoDao(db: GithubDb): RepoDao {
        return db.repoDao()
    }
}