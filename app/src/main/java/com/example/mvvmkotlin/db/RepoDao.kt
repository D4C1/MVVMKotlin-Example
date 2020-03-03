package com.example.mvvmkotlin.db

import android.util.SparseIntArray
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mvvmkotlin.model.Contributor
import com.example.mvvmkotlin.model.Repo
import com.example.mvvmkotlin.model.RepoSearchResponse
import com.example.mvvmkotlin.model.RepoSearchResult
import java.util.*

@Dao
abstract class RepoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(vararg repos: Repo)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertContributors(contributors: List<Contributor>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertRepos(repositories: List<Repo>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun createRepoIfNoExists(repo: Repo): Long


    @Query("SELECT * FROM repo WHERE owner_login = :ownerLogin and name = :name")
    abstract fun load(ownerLogin: String, name: String): LiveData<Repo>

    @Query("Select login, avatarUrl, repoName, repoOwner, contributions from contributor where repoName=:name and repoOwner=:owner order by contributions desc")
    abstract fun loadContributors(owner: String, name: String): LiveData<List<Contributor>>

    @Query("SELECT * from repo where owner_login = :owner order by stars desc")
    abstract fun loadRepositories(owner: String) : LiveData<List<Repo>>



    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(result: RepoSearchResult)

    @Query("SELECT * from reposearchresult where `query` = :query")
    abstract fun  search (query: String): LiveData<RepoSearchResult>

    fun loadOrdered(repoIds: List<Int>): LiveData<List<Repo>>{
        val order = SparseIntArray()
        repoIds.withIndex().forEach{
            order.put(it.value, it.index)
        }
        return Transformations.map(loadById(repoIds)) {
            repositories ->
                Collections.sort(repositories) {
                    r1, r2 ->
                        val pos1 = order.get(r1.id)
                        val pos2 = order.get(r2.id)
                    pos1 - pos2
                }
                repositories
        }
    }

    @Query("Select * from repo where id in (:repoIds)")
    protected abstract fun loadById(repoIds: List<Int>): LiveData<List<Repo>>

    @Query("Select * from reposearchresult where `query`= :query")
    abstract fun findSearchResult(query: String): RepoSearchResult?



}