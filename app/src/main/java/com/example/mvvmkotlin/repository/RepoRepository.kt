package com.example.mvvmkotlin.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.example.mvvmkotlin.AppExecutors
import com.example.mvvmkotlin.api.ApiResponse
import com.example.mvvmkotlin.api.ApiSuccessResponse
import com.example.mvvmkotlin.api.GithubApi
import com.example.mvvmkotlin.db.GithubDb
import com.example.mvvmkotlin.db.RepoDao
import com.example.mvvmkotlin.model.Contributor
import com.example.mvvmkotlin.model.Repo
import com.example.mvvmkotlin.model.RepoSearchResponse
import com.example.mvvmkotlin.model.RepoSearchResult
import com.example.mvvmkotlin.utils.AbsentLiveData
import com.example.mvvmkotlin.utils.RateLimiter
import java.security.acl.Owner
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RepoRepository @Inject constructor(
    private val appExecutors: AppExecutors,
    private val db: GithubDb,
    private val repoDao: RepoDao,
    private val githubApi: GithubApi
){
    private val repoListRateLimiter = RateLimiter<String>(10, TimeUnit.MINUTES)

    fun loadRepos(owner: String): LiveData<Resource<List<Repo>>> {
        return  object : NetworkBoundResource<List<Repo>, List<Repo>>(appExecutors) {
            override fun saveCallResult(item: List<Repo>) {
                repoDao.insertRepos(item)
            }

            override fun shouldFetch(data: List<Repo>?): Boolean {
                return  data == null || data.isEmpty() || repoListRateLimiter.shouldFetch(owner)
            }

            override fun loadFromDb(): LiveData<List<Repo>> = repoDao.loadRepositories(owner)


            override fun createCall(): LiveData<ApiResponse<List<Repo>>> = githubApi.getRepos(owner)


            override fun onFetchFailed() {
                repoListRateLimiter.reset(owner)
            }

        }.asLiveData()
    }

    fun loadRepo(owner: String, name: String): LiveData<Resource<Repo>> {
        return object : NetworkBoundResource<Repo, Repo> (appExecutors) {
            override fun saveCallResult(item: Repo) {
                repoDao.insert(item)
            }

            override fun shouldFetch(data: Repo?): Boolean = data == null


            override fun loadFromDb(): LiveData<Repo> = repoDao.load(owner, name)


            override fun createCall(): LiveData<ApiResponse<Repo>> = githubApi.getRepo(owner, name)


        }.asLiveData()
    }

    fun loadContributors(owner: String, name: String) : LiveData<Resource<List<Contributor>>> {
        return object : NetworkBoundResource<List<Contributor>, List<Contributor>>(appExecutors) {
            override fun saveCallResult(item: List<Contributor>) {
                item.forEach{
                    it.repoName = name
                    it.repoOwner = owner
                }
                db.runInTransaction{
                    repoDao.createRepoIfNoExists(
                        Repo(
                            id = Repo.UNKOWN_ID,
                            name = name,
                            fullName = "$owner/$name",
                            description = "",
                            owner = Repo.Owner(owner, null),
                            stars = 0
                        )
                    )
                    repoDao.insertContributors(item)
                }
            }

            override fun shouldFetch(data: List<Contributor>?): Boolean {
               return data == null || data.isEmpty()
            }

            override fun loadFromDb(): LiveData<List<Contributor>> = repoDao.loadContributors(owner, name)


            override fun createCall(): LiveData<ApiResponse<List<Contributor>>> = githubApi.getContributors(owner, name)

        }.asLiveData()
    }

    fun searchNextPage(query: String): LiveData<Resource<Boolean>> {
        val fetchNextSearchPageTask = FetchNextSearchPageTask(query, githubApi, db)
        appExecutors.networkIO().execute(fetchNextSearchPageTask)
        return fetchNextSearchPageTask.liveData
    }

    fun search(query: String) : LiveData<Resource<List<Repo>>> {
        return object : NetworkBoundResource<List<Repo>, RepoSearchResponse>(appExecutors) {
            override fun saveCallResult(item: RepoSearchResponse) {
                val repoIds = item.items.map { it.id }
                val repoSearchResult = RepoSearchResult(query, repoIds, item.totalCount, item.nexPage)

                db.runInTransaction {
                    repoDao.insertRepos(item.items)
                    repoDao.insert(repoSearchResult)
                }
            }

            override fun shouldFetch(data: List<Repo>?): Boolean = data == null

            override fun loadFromDb(): LiveData<List<Repo>> = Transformations.switchMap(repoDao.search(query)) {
                searchData ->
                if (searchData == null){
                    AbsentLiveData.create()
                } else {
                    repoDao.loadOrdered(searchData.repoIds)
                }
            }

            override fun createCall(): LiveData<ApiResponse<RepoSearchResponse>> = githubApi.searchRepos(query)

            override fun processResponse(response: ApiSuccessResponse<RepoSearchResponse>): RepoSearchResponse {
                val body = response.body
                body.nexPage = response.nextPage
                return body
            }

        }.asLiveData()
    }
}