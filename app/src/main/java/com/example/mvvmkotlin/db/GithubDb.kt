package com.example.mvvmkotlin.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.mvvmkotlin.model.Contributor
import com.example.mvvmkotlin.model.Repo
import com.example.mvvmkotlin.model.RepoSearchResult
import com.example.mvvmkotlin.model.User

@Database(
    entities = [
        User::class,
        Repo::class,
        Contributor::class,
        RepoSearchResult::class
    ],
    version = 1
)
abstract class GithubDb: RoomDatabase() {
    abstract fun userDao(): UserDao

    abstract fun repoDao(): RepoDao

}