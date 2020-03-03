package com.example.mvvmkotlin.model

import androidx.room.Entity
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.mvvmkotlin.db.GithubTypeConverters

@Entity(
    primaryKeys = ["query"]
)
@TypeConverters(GithubTypeConverters::class)
class RepoSearchResult (
    val query: String,
    val repoIds: List<Int>,
    val totalCount: Int,
    val next: Int?
)